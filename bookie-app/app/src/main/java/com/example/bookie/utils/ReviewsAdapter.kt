package com.example.bookie.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.MyApplication
import com.example.bookie.R
import com.example.bookie.models.ReviewTab
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.*


class ReviewsAdapter(private val myDataSet: List<ReviewTab>, private val context: Context?) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewCardViewHolder>() {

    class ReviewCardViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.book_image)
        val bookTitle: TextView = view.findViewById(R.id.book_title)
        val preview: TextView = view.findViewById(R.id.preview)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        val likes: TextView = view.findViewById(R.id.likes)
        val time: TextView = view.findViewById(R.id.time)
        val readMore: TextView = view.findViewById(R.id.read_more)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_review_card, parent, false)

        return ReviewCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        val data = myDataSet[position]

        val imageErrorCallback = object : Callback {
            override fun onSuccess() {}
            override fun onError(e: Exception) {
                println(e)
            }
        }

        val currentContext = MyApplication.appContext
        if (data.image != null) Picasso.get().load(data.image).into(
            holder.bookImage,
            imageErrorCallback
        )
        else if (currentContext != null) holder.bookImage.setImageDrawable(
            currentContext.getDrawable(
                R.drawable.ic_account_circle_large
            )
        )
        holder.bookTitle.text = data.title
        holder.preview.text = data.preview
        holder.ratingBar.rating = data.rating
        holder.likes.text = data.likes.toString()
        holder.time.text = DateUtils.getDifference(data.time, Date())
        holder.readMore.text = context?.getString(R.string.read_more)

        if (data.isLiked) {
            holder.likes.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_thumb_up_green,0)
            holder.likes.setOnClickListener {
                val context = context ?: return@setOnClickListener
                // TODO deslike @Nacho
            }
        } else {
            holder.likes.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_thumb_up,0)
            holder.likes.setOnClickListener {
                val context = context ?: return@setOnClickListener
                // TODO like @Nacho
            }
        }

        if (holder.preview.text.length > 39 * holder.preview.maxLines) {
            holder.readMore.visibility = View.VISIBLE
            holder.readMore.setOnClickListener {
                val context = context?:return@setOnClickListener
                holder.preview.maxLines =
                    if (holder.readMore.text == context.getString(R.string.read_more)) {
                        holder.readMore.text = context.getString(R.string.read_less)
                        Integer.MAX_VALUE
                    } else {
                        holder.readMore.text = context.getString(R.string.read_more)
                        3
                    }
            }
        } else {
            holder.readMore.visibility = View.GONE
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataSet.size
}