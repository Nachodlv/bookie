package com.example.bookie.utils

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookie.BookProfile
import com.example.bookie.ui.book_list.BookList

class SearchableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.search)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                doSearch(query)
            }
        } else if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data ?: return
            val id = uri.lastPathSegment ?: return

            print(id)
            getById(id)
        }
        finish()
    }

    private fun doSearch(query: String) {
        val intent = Intent(this, BookList::class.java)
        val bundle = Bundle()
        bundle.putString("searchQuery", query)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun getById(id: String) {
        val intent = Intent(this, BookProfile::class.java)
        val bundle = Bundle()
        bundle.putString("bookId", id)
        intent.putExtras(bundle)
        startActivity(intent)
    }




}
