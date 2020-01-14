package com.example.bookie.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.example.bookie.api.client.ReviewClient
import com.example.bookie.dao.ReviewDao
import com.example.bookie.models.Book
import com.example.bookie.models.Review
import java.util.concurrent.Executor

class ReviewRepository(
    private val reviewDao: ReviewDao,
    private val reviewClient: ReviewClient,
    private val executor: Executor,
    private val bookRepository: BookRepository,
    private val authRepository: AuthRepository
) {
    fun postReview(
        bookId: String,
        comment: String,
        score: Int,
        newReview: Boolean
    ): Pair<LiveData<RepositoryStatus<Book>>, LiveData<RepositoryStatus<Review>>> {
        val bookStatus = RepositoryStatus.initStatus<Book>()
        val reviewStatus = RepositoryStatus.initStatus<Review>()
        reviewClient.postReview(bookId, comment, score, {
            val review = it.toReview()
            authRepository.getUserLoggedIn()
            executor.execute {
                reviewDao.save(review)
                reviewStatus.postValue(
                    RepositoryStatus.Success(
                        review
                    )
                )
            }
            bookRepository.addReview(bookId, score, newReview, bookStatus)
        }, { error ->
            executor.execute {
                bookStatus.postValue(RepositoryStatus.Error(error))
                reviewStatus.postValue(RepositoryStatus.Error(error))
            }
        })
        val reviewWithId =
            Transformations.switchMap(authRepository.getUserLoggedIn()) { userStatus ->
                Transformations.map(reviewStatus) { reviewStatus ->
                    if (userStatus is RepositoryStatus.Success && reviewStatus is RepositoryStatus.Success) {
                        reviewStatus.data.userId = userStatus.data.id
                    }
                    reviewStatus
                }
            }
        return Pair(bookStatus, reviewWithId)
    }

    fun getReviews(
        bookId: String,
        index: Int,
        page: Int = 10
    ): LiveData<RepositoryStatus<List<Review>>> {
        val status = RepositoryStatus.initStatus<List<Review>>()

        reviewClient.getReviews(bookId, index / page, page, { reviews ->
            executor.execute {
                status.postValue(RepositoryStatus.Success(reviews.map { it.toReview() }))
            }
        }, { errorMessage -> status.postValue(RepositoryStatus.Error(errorMessage)) })

        return status
    }

    fun likeReview(bookId: String, userId: String): LiveData<RepositoryStatus<String>> {
        return likeOrUnLikeReview(bookId, userId, true)
    }

    fun unLikeReview(bookId: String, userId: String): LiveData<RepositoryStatus<String>> {
        return likeOrUnLikeReview(bookId, userId, false)
    }

    private fun likeOrUnLikeReview(
        bookId: String,
        userId: String,
        isLike: Boolean
    ): LiveData<RepositoryStatus<String>> {
        val status = RepositoryStatus.initStatus<String>()

        reviewClient.likeReview(
            bookId,
            userId,
            isLike,
            { executor.execute { status.postValue(RepositoryStatus.Success("")) } },
            { error -> executor.execute { status.postValue(RepositoryStatus.Error(error)) } })

        return status
    }

    fun getReviewLoggedUser(bookId: String): LiveData<RepositoryStatus<Review?>> {

        val user = authRepository.getUserLoggedIn()
        val mediator = MediatorLiveData<RepositoryStatus<Review?>>()
        mediator.addSource(user) {
            if (it is RepositoryStatus.Success) {
                executor.execute {
                    val review =
                        reviewDao.hasReview(bookId, it.data.id, UserRepository.FRESH_TIMEOUT)
                    if (review == null) {
                        reviewClient.getReviewOfLoggedUser(bookId,
                            { executor.execute { mediator.postValue(RepositoryStatus.Success(it?.toReview())) } },
                            { executor.execute { mediator.postValue(RepositoryStatus.Error(it)) } })
                    } else {
                        mediator.postValue(RepositoryStatus.Success(review))
                    }
                }
            } else if (it is RepositoryStatus.Error) {
                executor.execute { mediator.postValue(RepositoryStatus.Error(it.error)) }
            }
        }

        return mediator
    }
}