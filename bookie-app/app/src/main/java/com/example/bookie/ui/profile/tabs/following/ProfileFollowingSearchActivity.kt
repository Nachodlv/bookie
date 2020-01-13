package com.example.bookie.ui.profile.tabs.following

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.UserPreview
import com.example.bookie.utils.FollowingsAdapter
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import kotlinx.android.synthetic.main.fragment_home.*


class ProfileFollowingSearchActivity : AppCompatActivity() {

    private val injector = KodeinInjector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_followings_search_main)

        injector.inject(appKodein())

        setupToolbar()

        if(intent.hasExtra("searchQuery")) {
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

        // TODO get from backend, will only return users i don't already follow who's full name matches query, paginated.

        val users: MutableList<UserPreview> = arrayListOf(
                UserPreview("1", "Gianluca", "Scolaro", isFollower = false),
                UserPreview("2", "Pedro", "Perez", isFollower = true),
                UserPreview("3", "Jacobo", "Santos de La Virgen de Nazareth Segundo", isFollower = true),
                UserPreview("4", "Juan", "Carlos", isFollower = true),
                UserPreview("5", "Bob", isFollower = true),
                UserPreview("6", "Gianluca", "Scolaro 2", isFollower = true),
                UserPreview("7", "Gianluca", "Scolaro 3", isFollower = true),
                UserPreview("8", "Gianluca", "Scolaro 4", isFollower = false),
                UserPreview("9", "Gianluca", "Scolaro 5", isFollower = true),
                UserPreview("10", "Gianluca", "Scolaro 6", isFollower = false),
                UserPreview("11", "Gianluca", "Scolaro 7", isFollower = true),
                UserPreview("12", "Gianluca", "Scolaro 8", isFollower = true),
                UserPreview("13", "Gianluca", "Scolaro 9", isFollower = false),
                UserPreview("14", "Gianluca", "Scolaro 10", isFollower = true),
                UserPreview("15", "Gianluca", "Scolaro 11", isFollower = true),
                UserPreview("16", "Gianluca", "Scolaro 12", isFollower = false),
                UserPreview("17", "Gianluca", "Scolaro 13", isFollower = true),
                UserPreview("18", "Gianluca", "Scolaro 14", isFollower = true),
                UserPreview("19", "Gianluca", "Scolaro 15", isFollower = true),
                UserPreview("20", "Gianluca", "Scolaro 16", isFollower = true),
                UserPreview("21", "Gianluca", "Scolaro 17", isFollower = true),
                UserPreview("22", "Gianluca", "Scolaro 18", isFollower = false),
                UserPreview("23", "Gianluca", "Scolaro 19", isFollower = true),
                UserPreview("24", "Gianluca", "Scolaro 20", isFollower = true)
        )

        setUpRecyclerView(users.map { user -> "${user.firstName} ${user.lastName}" }.toMutableList())

        /*bookRepository.searchBooks(query, { books ->
            runOnUiThread { buildList(books.map { it.toBookFeed() }.toMutableList(), query)}
        }, { error -> showError(error) }, 0)*/
    }

    private fun setUpRecyclerView(users: MutableList<String>){
        val myDataSet: MutableList<String> = mutableListOf<String>().apply { addAll(users.subList(0, 10)) }

        val recList = findViewById<RecyclerView>(R.id.list_container)
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = FollowingsAdapter(myDataSet)
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

        recList.addOnScrollListener(OnScrollListener(viewManager, viewAdapter, myDataSet, users))
    }

    private fun showError(errorMessage: String) {
        SnackbarUtil.showSnackbar(feed_container.rootView, errorMessage)
    }


}
