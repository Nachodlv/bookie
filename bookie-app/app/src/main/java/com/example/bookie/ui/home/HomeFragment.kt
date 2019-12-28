package com.example.bookie.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.bookie.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val myActivity = activity
        if(myActivity != null) {
            val searchManager = myActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            (menu.findItem(R.id.search).actionView as SearchView).apply {
                setSearchableInfo(searchManager.getSearchableInfo(myActivity.componentName))
            }
        }



//        super.onCreateOptionsMenu(menu, inflater)
    }
}