package com.example.bookie.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookie.R
import com.example.bookie.models.BookFeed
import com.example.bookie.models.FollowerComment
import com.example.bookie.models.FollowerReview
import com.example.bookie.models.toObject
import com.example.bookie.utils.DateUtils
import com.example.bookie.utils.DownloadImage
import java.util.*


class FollowerReviewFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_follower_review, container, false)

        val data = this.arguments!!.getString("data")!!.toObject<FollowerReview>()

        val eventDescTextView = inflatedView!!.findViewById<TextView>(R.id.event_desc)
        eventDescTextView.text = "${data.author} reviewed..."

        val titleTextView = inflatedView.findViewById<TextView>(R.id.book_title)
        titleTextView.text = data.title

        val ratingBar = inflatedView.findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.rating = data.rating

        val timeTextView = inflatedView.findViewById<TextView>(R.id.time)
        timeTextView.text = DateUtils.getDifference(data.time, Date())

        DownloadImage(inflatedView.findViewById(R.id.book_image))
                .execute(data.image)

        return inflatedView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}