package com.example.bookie.repositories

import androidx.lifecycle.LiveData
import com.example.bookie.api.client.ReviewClient
import com.example.bookie.dao.ReviewDao
import com.example.bookie.models.Book
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

}