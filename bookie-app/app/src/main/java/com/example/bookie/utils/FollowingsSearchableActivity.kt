package com.example.bookie.utils

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookie.ui.profile.PublicProfile
import com.example.bookie.ui.profile.tabs.following.ProfileFollowingSearchActivity

class FollowingsSearchableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val intent = Intent(this, ProfileFollowingSearchActivity::class.java)
        val bundle = Bundle()
        bundle.putString("searchQuery", query)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun getById(id: String) {
        val intent = Intent(this, PublicProfile::class.java)
        val bundle = Bundle()
        bundle.putString("userId", id)
        intent.putExtras(bundle)
        startActivity(intent)
    }




}
