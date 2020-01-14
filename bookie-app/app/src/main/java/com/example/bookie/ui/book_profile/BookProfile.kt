package com.example.bookie.ui.book_profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.Book
import com.example.bookie.models.EmptyReviewTab
import com.example.bookie.models.ReviewTab
import com.example.bookie.repositories.BookRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.ReviewRepository
import com.example.bookie.utils.*
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_book_profile.*

class BookProfile : AppCompatActivity() {

    private val injector = KodeinInjector()
    private val bookRepository: BookRepository by injector.instance()
    private val reviewRepository: ReviewRepository by injector.instance()

    private val pageSize: Int = 10
    private var dataSet: MutableList<ReviewTab> = mutableListOf()
    private var reviewsAdapter: ReviewsAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_profile)

        injector.inject(appKodein())

        setupToolbar()
        getBookId(reviews_container.rootView)
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun getBookId(view: View) {
        val bundle = intent.extras ?: return
        val bookId = bundle.getString("bookId") ?: return
        getBook(view, bookId)
    }

    private fun getBook(view: View, bookId: String) {

        bookRepository.getById(bookId).observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> {
//                    book = it.data
                    // setBookData(it.data)
                    runOnUiThread { loadReviews(view, it.data) }
                }
                is RepositoryStatus.Loading -> return@Observer
                is RepositoryStatus.Error -> setError(it.error)
            }
        })
    }

    private fun setError(errorMessage: String) {
        SnackbarUtil.showSnackbar(reviews_container.rootView, errorMessage)
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

        dataSet.add(0, EmptyReviewTab(book))
        this.dataSet = dataSet

        val recList = view.findViewById(R.id.reviews_container) as RecyclerView
        val viewManager = LinearLayoutManager(applicationContext)
        val viewAdapter = ReviewsAdapter(dataSet, this, this, true)
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
