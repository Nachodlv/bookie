package com.example.bookie.repositories

import androidx.lifecycle.LiveData
import com.example.bookie.api.client.BookApiClient
import com.example.bookie.dao.BookDao
import com.example.bookie.models.Book
import java.util.concurrent.Executor

class BookRepository constructor(
    private val bookApiClient: BookApiClient,
    private val bookDao: BookDao,
    private val executor: Executor
) {

    fun searchRecommendation(query: String, completion: (List<Book>) -> Unit) {
        bookApiClient.searchRecommendation(
            query,
            4,
            { books ->
                completion(books)
                executor.execute { bookDao.save(*books.toTypedArray()) }
            },
            {})
    }

    fun searchByIsbn(isbn: String): LiveData<RepositoryStatus<Book>> {
        val initStatus = RepositoryStatus.initStatus<Book>()

        bookApiClient.searchByIsbn(
            isbn,
            { book ->
                initStatus.value = RepositoryStatus.Success(book)
                executor.execute { bookDao.save(book) }
            },
            { error -> initStatus.value = RepositoryStatus.Error(error) })

        return initStatus
    }

}