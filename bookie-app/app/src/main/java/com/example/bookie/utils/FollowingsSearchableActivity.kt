package com.example.bookie.utils

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FollowingsSearchableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.search)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
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
        print("Hello")
        // TODO go to search result
    }

    private fun getById(id: String) {
        //TODO get book by id
    }




}
