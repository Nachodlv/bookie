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
import com.example.bookie.models.toObject
import com.example.bookie.utils.DateUtils
import com.example.bookie.utils.DownloadImage
import java.util.*


class FollowerCommentFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_follower_comment, container, false)

        val data = this.arguments!!.getString("data")!!.toObject<FollowerComment>()

        val eventDescTextView = inflatedView!!.findViewById<TextView>(R.id.event_desc)
        eventDescTextView.text = "${data.author} commented on review of..."

        val titleTextView = inflatedView.findViewById<TextView>(R.id.book_title)
        titleTextView.text = data.title

        val previewTextView = inflatedView.findViewById<TextView>(R.id.comment_preview)
        previewTextView.text = data.preview

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