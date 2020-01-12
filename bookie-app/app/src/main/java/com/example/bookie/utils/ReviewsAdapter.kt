package com.example.bookie.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.*



class ReviewsAdapter(private val myDataset: List<ReviewTab>) :
        RecyclerView.Adapter<ReviewsAdapter.ReviewCardViewHolder>() {

    class ReviewCardViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.book_image)
        val bookTitle: TextView = view.findViewById(R.id.book_title)
        val preview: TextView = view.findViewById(R.id.preview)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        val likes: TextView = view.findViewById(R.id.likes)
        val time: TextView = view.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ReviewCardViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_review_card, parent, false)

        return ReviewCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        val data = myDataset[position]

        val imageErrorCallback = object : Callback {
            override fun onSuccess() {}
            override fun onError(e: Exception) {
                println(e)
            }
        }

        Picasso.get().load(data.image).into(holder.bookImage, imageErrorCallback)
        holder.bookTitle.text = data.title
        holder.preview.text = data.preview
        holder.ratingBar.rating = data.rating
        holder.likes.text = data.likes.toString()
        holder.time.text = DateUtils.getDifference(data.time, Date())
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}