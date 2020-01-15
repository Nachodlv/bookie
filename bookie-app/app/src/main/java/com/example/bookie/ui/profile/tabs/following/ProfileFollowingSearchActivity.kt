package com.example.bookie.ui.profile.tabs.following

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.UserPreview
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.UserRepository
import com.example.bookie.utils.FollowingsAdapter
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.profile_followings_search_main.*


class ProfileFollowingSearchActivity : AppCompatActivity() {

    private val injector = KodeinInjector()
    private val userRepository: UserRepository by injector.instance()
    private val pageSize: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_followings_search_main)

        injector.inject(appKodein())

        setupToolbar()

        if (intent.hasExtra("searchQuery")) {
            val bundle = intent.extras ?: return
            val query = bundle.getString("searchQuery") ?: return
            searchForUsers(query)
        }
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu!!.findItem(R.id.search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            //isIconifiedByDefault = false // Do not iconify the widget; expand it by default
        }
        return true
    }

    private fun searchForUsers(query: String) {

        userRepository.searchUser(query, 0, pageSize).observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> setUpRecyclerView(
                    query,
                    it.data.toMutableList()
                )
                is RepositoryStatus.Error -> SnackbarUtil.showSnackbar(toolbar.rootView, it.error)
            }
        })


        /*bookRepository.searchBooks(query, { books ->
            runOnUiThread { buildList(books.map { it.toBookFeed() }.toMutableList(), query)}
        }, { error -> showError(error) }, 0)*/
    }

    private fun setUpRecyclerView(query: String, users: MutableList<UserPreview>) {

        val recList = findViewById<RecyclerView>(R.id.list_container)
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = FollowingsAdapter(users, this)
        viewManager.orientation = LinearLayoutManager.VERTICAL

        recList.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }

        recList.addOnScrollListener(
            OnScrollListener(
                viewManager,
                viewAdapter,
                users,
                pageSize
            ) { index, callback ->
                userRepository.searchUser(query, index, pageSize).observe(this, Observer {
                    when (it) {
                        is RepositoryStatus.Success -> callback(it.data.toMutableList())
                        is RepositoryStatus.Error -> callback(mutableListOf())
                    }
                })
            }
        )
    }

    private fun showError(errorMessage: String) {
        SnackbarUtil.showSnackbar(feed_container.rootView, errorMessage)
    }


}
