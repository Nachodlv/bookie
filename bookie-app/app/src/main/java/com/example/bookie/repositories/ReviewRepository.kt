package com.example.bookie.repositories

import androidx.lifecycle.LiveData
import com.example.bookie.api.client.ReviewClient
import com.example.bookie.dao.ReviewDao
import com.example.bookie.models.Book
import com.example.bookie.models.Review
import java.util.concurrent.Executor

class ReviewRepository(
    private val reviewDao: ReviewDao,
    private val reviewClient: ReviewClient,
    private val executor: Executor,
    private val bookRepository: BookRepository
) {
    fun postReview(
        bookId: String,
        comment: String,
        score: Int,
        newReview: Boolean
    ): LiveData<RepositoryStatus<Book>> {
        val status = RepositoryStatus.initStatus<Book>()
        reviewClient.postReview(bookId, comment, score, {
            val review = it.toReview()
            executor.execute { reviewDao.save(review) }
            bookRepository.addReview(bookId, score, newReview, status)
        }, { error -> executor.execute { status.postValue(RepositoryStatus.Error(error)) } })
        return status
    }

    fun getReviews(bookId: String, index: Int, page: Int = 10): LiveData<RepositoryStatus<List<Review>>> {
        val status = RepositoryStatus.initStatus<List<Review>>()

        reviewClient.getReviews(bookId, index, page, {reviews ->
            executor.execute {
                status.postValue(RepositoryStatus.Success(reviews.map { it.toReview() }))
            }
        }, {errorMessage -> status.postValue(RepositoryStatus.Error(errorMessage)) })

        return status
    }

}