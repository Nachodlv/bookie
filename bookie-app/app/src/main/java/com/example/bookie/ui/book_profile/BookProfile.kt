package com.example.bookie.ui.book_profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.bookie.R
import com.example.bookie.models.Book
import com.example.bookie.repositories.BookRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_book_profile.*

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

        bookRepository.getById(bookId).observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> setBookData(it.data)
                is RepositoryStatus.Loading -> return@Observer
                is RepositoryStatus.Error -> setError(it.error)
            }
        })
    }

    private fun setBookData(book: Book) {

        book_title.text = book.title
        book_author.text = book.authors?.reduce { a, b -> "$a, $b" } ?: ""
        Picasso.get().load(book.imageLinks?.thumbnail)
            .into(book_image)
        book_categories.text = book.categories?.reduce { a, b -> "$a, $b" } ?: ""
        if (book.review != null) {
            rating_bar.visibility = View.VISIBLE
            reviews_quantity.visibility = View.VISIBLE
            rating_bar.rating = book.review?.rating ?: 0f
            reviews_quantity.text = "(${book.review?.amountOfReviews ?: 0})"
        } else {
            reviews_quantity.visibility = View.GONE
            rating_bar.visibility = View.GONE
        }

    }

    private fun setError(errorMessage: String) {
        SnackbarUtil.showSnackbar(book_title.rootView, errorMessage)
    }
}
