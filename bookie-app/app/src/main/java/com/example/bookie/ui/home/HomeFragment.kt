package com.example.bookie.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.bookie.R
import java.util.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.models.*
import com.example.bookie.utils.MyAdapter
import com.example.bookie.utils.OnScrollListener
import java.util.Collections.addAll

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
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)
        getFeed(homeView)
        return homeView
    }

    private fun getFeed(homeView: View){
        //TODO get from backend

        val coverImage = "http://books.google.com/books/content?id=zaRoX10_UsMC&printsec=frontcover&img=1&zoom=5&edge=curl&imgtk=AFLRE70-WOhpalNjXmQHvsBr3kHikU9KUEtIHzSrFk2W_ehR0VaKktBtXXFLm3pOr0EVxAoTg4-jhTA1hhz-xp3cEgA7_dC2TVawKxbILkmbmwj-Gw-K0bhIj76mAZfuB1Yusj2AdrkF&source=gbs_api"

        val feedArray: MutableList<FeedItem> = arrayListOf(
                BookFeed("0", "The Fellowship of the Ring 1", "JRR Tolkien",
                coverImage, FeedItemType.BOOK,5.0F),
                BookFeed("1", "Jesus our lord, Bible Study 2", "Charles R. Swindoll",
                        coverImage, FeedItemType.BOOK,3.5F),
                FollowerComment("2", "The Fellowship of the Ring 3", "Eduardo",
                        coverImage, FeedItemType.COMMENT,"I loved this review, really helpful and enjoyable. Thank you!", Date()),
                FollowerReview("3", "The Fellowship of the Ring 4", "Eduardo",
                        coverImage, FeedItemType.REVIEW,5F, Date()),
                BookFeed("4", "The Fellowship of the Ring 5", "JRR Tolkien",
                        coverImage, FeedItemType.BOOK,5.0F),
                BookFeed("5", "Jesus our lord, Bible Study 6", "Charles R. Swindoll",
                        coverImage, FeedItemType.BOOK,3.5F),
                FollowerComment("6", "The Fellowship of the Ring 7", "Eduardo",
                        coverImage, FeedItemType.COMMENT,"I loved this review, really helpful and enjoyable. Thank you!", Date()),
                FollowerReview("7", "The Fellowship of the Ring 8", "Eduardo",
                        coverImage, FeedItemType.REVIEW,5F, Date()),
                BookFeed("8", "The Fellowship of the Ring 9", "JRR Tolkien",
                        coverImage, FeedItemType.BOOK,5.0F),
                BookFeed("9", "Jesus our lord, Bible Study 10", "Charles R. Swindoll",
                        coverImage, FeedItemType.BOOK,3.5F),
                FollowerComment("10", "The Fellowship of the Ring 11", "Eduardo",
                        coverImage, FeedItemType.COMMENT,"I loved this review, really helpful and enjoyable. Thank you!", Date()),
                FollowerReview("11", "The Fellowship of the Ring 12", "Eduardo",
                        coverImage, FeedItemType.REVIEW,5F, Date()),
                BookFeed("12", "The Fellowship of the Ring 13", "JRR Tolkien",
                        coverImage, FeedItemType.BOOK,5.0F),
                BookFeed("13", "Jesus our lord, Bible Study 14", "Charles R. Swindoll",
                        coverImage, FeedItemType.BOOK,3.5F),
                FollowerComment("14", "The Fellowship of the Ring 15", "Eduardo",
                        coverImage, FeedItemType.COMMENT,"I loved this review, really helpful and enjoyable. Thank you!", Date()),
                FollowerReview("15", "The Fellowship of the Ring 16", "Eduardo",
                        coverImage, FeedItemType.REVIEW,5F, Date()),
                BookFeed("16", "The Fellowship of the Ring 17", "JRR Tolkien",
                        coverImage, FeedItemType.BOOK,5.0F),
                BookFeed("17", "Jesus our lord, Bible Study 18", "Charles R. Swindoll",
                        coverImage, FeedItemType.BOOK,3.5F),
                FollowerComment("18", "The Fellowship of the Ring 19", "Eduardo",
                        coverImage, FeedItemType.COMMENT,"I loved this review, really helpful and enjoyable. Thank you!", Date()),
                FollowerReview("19", "The Fellowship of the Ring 20", "Eduardo",
                        coverImage, FeedItemType.REVIEW,5F, Date()),
                BookFeed("20", "The Fellowship of the Ring 21", "JRR Tolkien",
                        coverImage, FeedItemType.BOOK,5.0F),
                BookFeed("21", "Jesus our lord, Bible Study 22", "Charles R. Swindoll",
                        coverImage, FeedItemType.BOOK,3.5F),
                FollowerComment("22", "The Fellowship of the Ring 23", "Eduardo",
                        coverImage, FeedItemType.COMMENT,"I loved this review, really helpful and enjoyable. Thank you!", Date()),
                FollowerReview("23", "The Fellowship of the Ring 24", "Eduardo",
                        coverImage, FeedItemType.REVIEW,5F, Date()),
                BookFeed("24", "The Fellowship of the Ring 25", "JRR Tolkien",
                        coverImage, FeedItemType.BOOK,5.0F),
                BookFeed("25", "Jesus our lord, Bible Study 26", "Charles R. Swindoll",
                        coverImage, FeedItemType.BOOK,3.5F),
                FollowerComment("26", "The Fellowship of the Ring 27", "Eduardo",
                        coverImage, FeedItemType.COMMENT,"I loved this review, really helpful and enjoyable. Thank you!", Date()),
                FollowerReview("27", "The Fellowship of the Ring 28", "Eduardo",
                        coverImage, FeedItemType.REVIEW,5F, Date())
                )

        val myDataset: MutableList<FeedItem> = mutableListOf<FeedItem>().apply { addAll(feedArray.subList(0, 10)) }

        val recList = homeView.findViewById(R.id.feed_container) as RecyclerView
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = MyAdapter(myDataset)
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

        recList.addOnScrollListener(OnScrollListener(viewManager, viewAdapter, myDataset, feedArray))
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