package com.example.bookie.ui.book_list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.bookie.R
import com.example.bookie.repositories.BookRepository
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_book_list.*

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
        toolbar.setTitle(R.string.register)
        toolbar.setSubtitle(R.string.register)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun searchForBooks() {
        val bundle = intent.extras ?: return
        val query = bundle.getString("searchQuery") ?: return
        bookRepository.searchBooks(query, { books ->
            SnackbarUtil.showSnackbar(text.rootView, books.joinToString { "${it.title}, " })
        }, { error -> showError(error) })
    }

    private fun showError(errorMessage: String) {
        text.setText(errorMessage)
    }
}
