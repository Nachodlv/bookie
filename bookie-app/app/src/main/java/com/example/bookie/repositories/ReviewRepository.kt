package com.example.bookie.repositories

import androidx.lifecycle.LiveData
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

}