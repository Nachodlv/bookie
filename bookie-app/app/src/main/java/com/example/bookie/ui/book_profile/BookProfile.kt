package com.example.bookie.ui.book_profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.Book
import com.example.bookie.models.ReviewTab
import com.example.bookie.repositories.BookRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.ReviewRepository
import com.example.bookie.ui.loader.LoaderFragment
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.ReviewsAdapter
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

    private val pageSize: Int = 10
    private var loaderFragment: LoaderFragment? = LoaderFragment()
    private var bookReviewed: Boolean = false
    private var book: Book? = null
    private var dataSet: MutableList<ReviewTab> = mutableListOf()
    private var reviewsAdapter: ReviewsAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_profile)

        injector.inject(appKodein())

        setupToolbar()
        getBook(review_text.rootView)
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

    private fun getBook(view: View) {
        val bundle = intent.extras ?: return
        val bookId = bundle.getString("bookId") ?: return

        bookRepository.getById(bookId).observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> {
                    book = it.data
                    setBookData(it.data)
                    runOnUiThread { loadReviews(view, it.data) }
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
        if (!TextValidator.hasErrors(review_text)) {
            if (review_rating.rating == 0f) SnackbarUtil.showSnackbar(
                view,
                applicationContext.getString(R.string.rating_required)
            ) else {
                loaderFragment?.showLoader(submit_button)
                val (bookStatus, reviewStatus) = reviewRepository.postReview(
                    book.id,
                    review_text.text.toString(),
                    review_rating.rating.toInt(),
                    !bookReviewed
                )
                bookStatus.observe(this, Observer<RepositoryStatus<Book>> {
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
                reviewStatus.observe(this, Observer {
                    when (it) {
                        is RepositoryStatus.Success -> {
                            val index = dataSet.indexOfFirst { r -> r.userId == it.data.userId }
                            if (index != -1) {
                                dataSet.removeAt(index)
                                reviewsAdapter?.notifyItemRemoved(index)
                            }
                            dataSet.add(0, it.data.toReviewTab())
                            reviewsAdapter?.notifyItemInserted(0)
                            recyclerView?.scrollToPosition(0)
                        }
                    }
                })

            }
        }
    }

    private fun loadReviews(view: View, book: Book) {

        reviewRepository.getReviews(book.id, 0, pageSize).observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> loadList(
                    view,
                    book,
                    it.data.map { r -> r.toReviewTab() }.toMutableList()
                )
                is RepositoryStatus.Error -> SnackbarUtil.showSnackbar(view, it.error)
            }
        })


    }

    private fun loadList(view: View, book: Book, dataSet: MutableList<ReviewTab>) {

        this.dataSet = dataSet

        val recList = view.findViewById(R.id.reviews_container) as RecyclerView
        val viewManager = LinearLayoutManager(applicationContext)
        val viewAdapter = ReviewsAdapter(dataSet, this)
        viewManager.orientation = LinearLayoutManager.VERTICAL

        reviewsAdapter = viewAdapter
        recyclerView = recList

        recList.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }

        val scrollListener =
            OnScrollListener(
                viewManager,
                viewAdapter,
                dataSet,
                pageSize
            ) { index, callback ->
                reviewRepository.getReviews(book.id, index, pageSize).observe(this, Observer {
                    when (it) {
                        is RepositoryStatus.Success -> callback(it.data.map { r -> r.toReviewTab() })
                        is RepositoryStatus.Error -> {
                            callback(emptyList())
                            SnackbarUtil.showSnackbar(view, it.error)
                        }
                    }
                })
            }

        recList.addOnScrollListener(scrollListener)

    }

}
