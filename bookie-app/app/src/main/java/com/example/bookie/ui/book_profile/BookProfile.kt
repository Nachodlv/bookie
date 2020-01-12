package com.example.bookie.ui.book_profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bookie.R
import com.example.bookie.models.Book
import com.example.bookie.repositories.BookRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.ReviewRepository
import com.example.bookie.ui.loader.LoaderFragment
import com.example.bookie.utils.SnackbarUtil
import com.example.bookie.utils.TextValidator
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_book_profile.*

class BookProfile : AppCompatActivity() {

    private val injector = KodeinInjector()
    private val bookRepository: BookRepository by injector.instance()
    private val reviewRepository: ReviewRepository by injector.instance()

    private var loaderFragment: LoaderFragment? = LoaderFragment()

    private var bookReviewed: Boolean = false
    private var book: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_profile)

        injector.inject(appKodein())

        setupToolbar()
        getBook()
        submit_button.setOnClickListener { onSubmitReview(it) }

        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_loader)
        loaderFragment = fragment as LoaderFragment?
        loaderFragment?.hideLoader(submit_button)
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun getBook() {
        val bundle = intent.extras ?: return
        val bookId = bundle.getString("bookId") ?: return

        bookRepository.getById(bookId).observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> {
                    book = it.data
                    setBookData(it.data)
                }
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
            reviews_quantity.text = "(${book.review?.reviewAmount ?: 0})"
        } else {
            reviews_quantity.visibility = View.GONE
            rating_bar.visibility = View.GONE
        }

    }

    private fun setError(errorMessage: String) {
        SnackbarUtil.showSnackbar(book_title.rootView, errorMessage)
    }

    private fun onSubmitReview(view: View) {
        val book = book ?: return
        loaderFragment?.showLoader(submit_button)
        if (!TextValidator.hasErrors(review_text)) {
            if (review_rating.rating == 0f) SnackbarUtil.showSnackbar(
                view,
                applicationContext.getString(R.string.rating_required)
            ) else {
                reviewRepository.postReview(
                    book.id,
                    review_text.text.toString(),
                    review_rating.rating.toInt(),
                    !bookReviewed
                ).observe(this, Observer<RepositoryStatus<Book>> {
                    when (it) {
                        is RepositoryStatus.Success -> {
                            loaderFragment?.hideLoader(submit_button)
                            submit_button.text = applicationContext.getText(R.string.edit_review)
                            bookReviewed = true
                            SnackbarUtil.showSnackbar(
                                view,
                                applicationContext.getString(R.string.successful_review)
                            )
                            setBookData(it.data)
                        }
                        is RepositoryStatus.Error -> {
                            loaderFragment?.hideLoader(submit_button)
                            SnackbarUtil.showSnackbar(view, it.error)
                        }
                        is RepositoryStatus.Loading -> return@Observer
                    }
                })

            }
        }
    }

}
