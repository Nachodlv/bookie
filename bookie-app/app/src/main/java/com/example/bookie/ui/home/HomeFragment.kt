package com.example.bookie.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.FeedItem
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.UserRepository
import com.example.bookie.utils.FeedItemsAdapter
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance

class HomeFragment : Fragment() {

    private val injector = KodeinInjector()
    private val userRepository: UserRepository by injector.instance()
    private val pageSize: Int = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        injector.inject(appKodein())
        setHasOptionsMenu(true)
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)
        getFeed(homeView) { createList(homeView, it.toMutableList()) }
        return homeView
    }

    private fun getFeed(view: View, action: (List<FeedItem>) -> Unit) {
        userRepository.getFeed(pageSize).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is RepositoryStatus.Success -> action(it.data)
                is RepositoryStatus.Error -> SnackbarUtil.showSnackbar(view, it.error)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val myActivity = activity
        if (myActivity != null) {
            val searchManager = myActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            (menu.findItem(R.id.search).actionView as SearchView).apply {
                setSearchableInfo(searchManager.getSearchableInfo(myActivity.componentName))
            }
        }

    }

    private fun createList(view: View, dataSet: MutableList<FeedItem>) {

        val recList = view.findViewById(R.id.feed_container) as RecyclerView
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = FeedItemsAdapter(dataSet, context)
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
                dataSet,
                pageSize
            ) { _, callback ->
                getFeed(view) { callback(it) }
            })
    }
}