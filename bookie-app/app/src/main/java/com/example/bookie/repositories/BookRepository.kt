package com.example.bookie.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookie.api.client.BookApiClient
import com.example.bookie.dao.BookDao
import com.example.bookie.models.Book
import com.example.bookie.repositories.UserRepository.Companion.FRESH_TIMEOUT
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executor

class BookRepository constructor(
    private val bookApiClient: BookApiClient,
    private val bookDao: BookDao,
    private val executor: Executor
) {

    fun searchRecommendation(query: String, completion: (List<Book>) -> Unit) {
        searchBooks(query, completion, {}, 4)
    }


    fun searchBooks(
        query: String,
        completion: (List<Book>) -> Unit,
        error: (String) -> Unit,
        limitation: Int = 40
    ) {

        bookApiClient.searchBook(
            query,
            limitation,
            { books ->
                completion(books)
                executor.execute {
                    books.forEach { b -> b.lastFetch = Calendar.getInstance().timeInMillis }
                    bookDao.save(*books.toTypedArray())
                }
            },
            error)
    }

    fun getById(id: String): LiveData<RepositoryStatus<Book>> {
        val status = RepositoryStatus.initStatus<Book>()
        refreshBook(id, status)
        return status
    }

    fun searchByIsbn(isbn: String): LiveData<RepositoryStatus<Book>> {
        val initStatus = RepositoryStatus.initStatus<Book>()

        bookApiClient.searchByIsbn(
            isbn,
            { book ->
                initStatus.value = RepositoryStatus.Success(book)
                book.lastFetch = Calendar.getInstance().timeInMillis
                executor.execute { bookDao.save(book) }
            },
            { error -> initStatus.value = RepositoryStatus.Error(error) })

        return initStatus
    }

    private fun refreshBook(bookId: String, status: MutableLiveData<RepositoryStatus<Book>>) {
        // Runs in a background thread.
        executor.execute {
            // Check if user data was fetched recently.
            val bookExists = bookDao.hasBook(bookId, FRESH_TIMEOUT)
            if (bookExists == null) {
                // Refreshes the data.
                bookApiClient.getBookById(bookId, { book ->
                    executor.execute {
                        book.lastFetch = Calendar.getInstance().timeInMillis
                        book.let { bookDao.save(it) }
                        status.value = RepositoryStatus.Success(book)

                    }
                }, { error -> GlobalScope.launch { status.postValue(RepositoryStatus.Error(error)) }})
            } else GlobalScope.launch { status.postValue(RepositoryStatus.Success(bookExists))}
        }
    }

}