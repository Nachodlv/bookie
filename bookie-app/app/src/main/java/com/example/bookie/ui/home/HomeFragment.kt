package com.example.bookie.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.bookie.R
import com.example.bookie.models.BookFeed
import com.example.bookie.ui.feed.BookCardFragment



class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        getFeed()
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun getFeed(){
        //TODO get from backend

        val feedArray = arrayOf(BookFeed("0", "The Fellowship of the Ring", "JRR Tolkien",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSJvXW-LO7i-0Ajbf0Os39Y3eMxf8yFRKNnBfWmyJsO4aloaPpc&s", 5.0F),
                BookFeed("1", "Jesus our lord, Bible Study", "Charles R. Swindoll",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSJvXW-LO7i-0Ajbf0Os39Y3eMxf8yFRKNnBfWmyJsO4aloaPpc&s", 3.5F))

        val ft = fragmentManager!!.beginTransaction()
        feedArray.forEach { book -> run {
            val bundle = Bundle()
            bundle.putString("data", book.toJSON())
            val fragInfo = BookCardFragment()
            fragInfo.arguments = bundle
            ft.add(R.id.feed_container, fragInfo, "book-"+book.id)
//            println(book.toJSON())
        }}
        ft.commit()
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