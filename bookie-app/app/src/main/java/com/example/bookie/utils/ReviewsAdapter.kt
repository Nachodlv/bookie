package com.example.bookie.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.MyApplication
import com.example.bookie.MyApplication.Companion.appKodein
import com.example.bookie.R
import com.example.bookie.models.ReviewTab
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.ReviewRepository
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import com.example.bookie.ui.book_profile.BookProfile
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.*


class ReviewsAdapter(private val myDataSet: List<ReviewTab>,
                     private val context: Context?,
                     private val lifecycleOwner: LifecycleOwner,
                     private val isInBookProfile: Boolean) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewCardViewHolder>() {

    private val injector = KodeinInjector()
    private val reviewRepository: ReviewRepository by injector.instance()

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
        injector.inject(appKodein)
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

        if(!isInBookProfile) {
            holder.bookTitle.setOnClickListener { goToBookProfile(data.id) }
            holder.preview.setOnClickListener { goToBookProfile(data.id) }
            holder.bookImage.setOnClickListener { goToBookProfile(data.id) }
        }

        if (data.isLiked) {
            setLikeListener(holder, data)
        } else {
            setUnLikeListener(holder, data)
        }

        val maxLines = holder.preview.maxLines
        if (holder.preview.text.length > 39 * holder.preview.maxLines) {
            holder.readMore.visibility = View.VISIBLE
            holder.readMore.setOnClickListener {
                val context = context ?: return@setOnClickListener
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

    private fun setUnLikeListener(holder: ReviewCardViewHolder, data: ReviewTab) {
        holder.likes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_thumb_up, 0)
        holder.likes.setOnClickListener {
            setLikeListener(holder, data)
            data.likes++
            holder.likes.text = data.likes.toString()
            reviewRepository.likeReview(data.id, data.userId)
                .observe(lifecycleOwner, androidx.lifecycle.Observer {
                    if (it is RepositoryStatus.Error) {
                        data.likes--
                        holder.likes.text = data.likes.toString()
                        setUnLikeListener(holder, data)
                    }
                })
        }
    }

    private fun setLikeListener(holder: ReviewCardViewHolder, data: ReviewTab) {
        holder.likes.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_thumb_up_green,
            0
        )
        holder.likes.setOnClickListener {
            setUnLikeListener(holder, data)
            data.likes--
            holder.likes.text = data.likes.toString()
            reviewRepository.unLikeReview(data.id, data.userId)
                .observe(lifecycleOwner, androidx.lifecycle.Observer {
                    if (it is RepositoryStatus.Error) {
                        data.likes++
                        holder.likes.text = data.likes.toString()
                        setLikeListener(holder, data)
                    }
                })
        }
    }

    private fun goToBookProfile(bookId: String) {
        val currentContext = context?: return
        val intent = Intent(currentContext, BookProfile::class.java)
        val bundle = Bundle()
        bundle.putString("bookId", bookId)
        intent.putExtras(bundle)
        ContextCompat.startActivity(currentContext, intent, bundle)
    }
}