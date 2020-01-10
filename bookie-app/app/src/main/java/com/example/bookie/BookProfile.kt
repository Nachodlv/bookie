package com.example.bookie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.bookie.models.Book
import com.example.bookie.repositories.BookRepository
import com.example.bookie.repositories.RepositoryStatus
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_book_list.*

class BookProfile : AppCompatActivity() {

    private val injector = KodeinInjector()
    private val bookRepository: BookRepository by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_profile)

        injector.inject(appKodein())

        getBook()
    }

    private fun getBook() {
        val bundle = intent.extras ?: return
        val bookId = bundle.getString("bookId") ?: return

        bookRepository.getById(bookId).observe(this, Observer{
            when(it) {
                is RepositoryStatus.Success -> setBookData(it.data)
                is RepositoryStatus.Loading -> return@Observer
                is RepositoryStatus.Error -> setError(it.error)
            }
        })
    }

    private fun setBookData(book: Book) {
        text.setText(book.title)
    }

    private fun setError(errorMessage: String) {
        text.setText(errorMessage)
    }
}
