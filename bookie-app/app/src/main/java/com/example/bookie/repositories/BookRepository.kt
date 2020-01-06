package com.example.bookie.repositories

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

}