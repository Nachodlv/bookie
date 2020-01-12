package com.example.bookie.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.exceptions.InvalidFeedItemTypeExc
import com.example.bookie.models.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.*



class FeedItemsAdapter(private val myDataset: List<FeedItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.

    class BookCardViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.book_image)
        val bookTitle: TextView = view.findViewById(R.id.book_title)
        val bookAuthor: TextView = view.findViewById(R.id.book_author)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
    }

    class FollowerCommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.book_image)
        val eventDesc: TextView = view.findViewById(R.id.event_desc)
        val bookTitle: TextView = view.findViewById(R.id.book_title)
        val preview: TextView = view.findViewById(R.id.comment_preview)
        val time: TextView = view.findViewById(R.id.time)
    }

    class FollowerReviewViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.book_image)
        val eventDesc: TextView = view.findViewById(R.id.event_desc)
        val bookTitle: TextView = view.findViewById(R.id.book_title)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        val time: TextView = view.findViewById(R.id.time)
    }



    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            FeedItemType.BOOK.id -> {
                // create a new view
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_card_book, parent, false)

                return BookCardViewHolder(view)
            }
            FeedItemType.COMMENT.id -> {
                // create a new view
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_follower_comment, parent, false)

                return FollowerCommentViewHolder(view)
            }
            FeedItemType.REVIEW.id -> {
                // create a new view
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_follower_review, parent, false)

                return FollowerReviewViewHolder(view)
            }
            else -> throw InvalidFeedItemTypeExc()
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val data = myDataset[position]

        val imageErrorCallback = object : Callback {
            override fun onSuccess() {}
            override fun onError(e: Exception) {
                println(e)
            }
        }

        when(data.type){
            FeedItemType.BOOK -> {
                val bookCardViewHolder = holder as BookCardViewHolder
                val bookData = data as BookFeed

                Picasso.get().load(bookData.image).into(bookCardViewHolder.bookImage, imageErrorCallback)
                bookCardViewHolder.bookTitle.text = bookData.title
                bookCardViewHolder.bookAuthor.text = bookData.author
                bookCardViewHolder.ratingBar.rating = bookData.rating
            }
            FeedItemType.COMMENT -> {
                val followerCommentViewHolder = holder as FollowerCommentViewHolder
                val commentData = data as FollowerComment

                Picasso.get().load(commentData.image).into(followerCommentViewHolder.bookImage, imageErrorCallback)
                followerCommentViewHolder.eventDesc.text = "${commentData.author} commented on review of..."
                followerCommentViewHolder.bookTitle.text = commentData.title
                followerCommentViewHolder.preview.text = commentData.preview
                followerCommentViewHolder.time.text = DateUtils.getDifference(commentData.time, Date())
            }
            FeedItemType.REVIEW -> {
                val followerReviewViewHolder = holder as FollowerReviewViewHolder
                val reviewData = data as FollowerReview

                Picasso.get().load(reviewData.image).into(followerReviewViewHolder.bookImage, imageErrorCallback)
                followerReviewViewHolder.eventDesc.text = "${reviewData.author} reviewed..."
                followerReviewViewHolder.bookTitle.text = reviewData.title
                followerReviewViewHolder.ratingBar.rating = reviewData.rating
                followerReviewViewHolder.time.text = DateUtils.getDifference(reviewData.time, Date())
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    override fun getItemViewType(position: Int): Int = myDataset[position].type.id

}