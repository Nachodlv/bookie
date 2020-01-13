package com.example.bookie.ui.profile.tabs.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.ReviewTab
import com.example.bookie.utils.OnScrollListenerMock
import com.example.bookie.utils.ReviewsAdapter
import java.util.*

class ProfileReviewsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reviews_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //TODO get from backend

        val coverImage = "http://books.google.com/books/content?id=zaRoX10_UsMC&printsec=frontcover&img=1&zoom=5&edge=curl&imgtk=AFLRE70-WOhpalNjXmQHvsBr3kHikU9KUEtIHzSrFk2W_ehR0VaKktBtXXFLm3pOr0EVxAoTg4-jhTA1hhz-xp3cEgA7_dC2TVawKxbILkmbmwj-Gw-K0bhIj76mAZfuB1Yusj2AdrkF&source=gbs_api"

        val reviews: MutableList<ReviewTab> = arrayListOf(
                ReviewTab("0", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend. This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("1", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("2", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("3", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("4", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("5", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("6", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("7", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("9", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("10", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("11", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("12", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("13", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("14", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date()),
                ReviewTab("15", "0", "The Fellowship of the Ring 1", "This a review. I kinda liked it. Idk sue me. 5/7 would recommend. This a review. I kinda liked it. Idk sue me. 5/7 would recommend. This a review. I kinda liked it. Idk sue me. 5/7 would recommend. This a review. I kinda liked it. Idk sue me. 5/7 would recommend. This a review. I kinda liked it. Idk sue me. 5/7 would recommend. This a review. I kinda liked it. Idk sue me. 5/7 would recommend.This a review. I kinda liked it. Idk sue me. 5/7 would recommend",
                        coverImage, 5.0F, 255, Date())
        )

        val myDataSet: MutableList<ReviewTab> = mutableListOf<ReviewTab>().apply { addAll(reviews.subList(0, 10)) }

        val recList = view.findViewById(R.id.reviews_container) as RecyclerView
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = ReviewsAdapter(myDataSet, context)
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

        recList.addOnScrollListener(OnScrollListenerMock(viewManager, viewAdapter, myDataSet, reviews))
    }
}