package com.example.bookie.ui.book_list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.MyApplication
import com.example.bookie.R
import com.example.bookie.models.BookFeed
import com.example.bookie.repositories.BookRepository
import com.example.bookie.utils.MyAdapter
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.fragment_home.*

class BookList : AppCompatActivity() {

    private val injector = KodeinInjector()
    private val bookRepository: BookRepository by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        injector.inject(appKodein())

        setupToolbar()
        searchForBooks()


    }


    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun searchForBooks() {
        val bundle = intent.extras ?: return
        val query = bundle.getString("searchQuery") ?: return
        bookRepository.searchBooks(query, { books ->
            runOnUiThread { buildList(books.map { it.toBookFeed() }.toMutableList(), query)}
        }, { error -> showError(error) }, 0)
    }


    private fun buildList(books: MutableList<BookFeed>, query: String) {
        val recList = findViewById<RecyclerView>(R.id.feed_container)
        val viewManager = LinearLayoutManager(applicationContext)
        val viewAdapter = MyAdapter(books, this)
        viewManager.orientation = LinearLayoutManager.VERTICAL

        recList.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(false)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }

        recList.addOnScrollListener(
            OnScrollListener(
                viewManager,
                viewAdapter,
                books
            ) { index, callback ->
                bookRepository.searchBooks(
                    query,
                    { newBook -> callback(newBook.map { it.toBookFeed() }) },
                    { error ->
                        showError(error)
                        callback(emptyList())
                    },
                    index
                )
            })
    }


    private fun showError(errorMessage: String) {
        SnackbarUtil.showSnackbar(feed_container.rootView, errorMessage)
    }
}
