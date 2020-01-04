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
import com.example.bookie.models.toObject
import com.example.bookie.utils.DownloadImage


class BookCardFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_card_book, container, false)

        val data = this.arguments!!.getString("data")!!.toObject<BookFeed>()

        val titleTextView = inflatedView!!.findViewById<TextView>(R.id.book_title)
        titleTextView.text = data.title

        val authorTextView = inflatedView.findViewById<TextView>(R.id.book_author)
        authorTextView.text = data.author

        val ratingBar = inflatedView.findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.rating = data.rating

        DownloadImage(inflatedView.findViewById(R.id.book_image))
                .execute(data.image)

        return inflatedView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        ratingBar.visibility = View.GONE
    }
}