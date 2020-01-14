package com.example.bookie.utils

import android.content.Context
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.MyApplication
import com.example.bookie.MyApplication.Companion.appKodein
import com.example.bookie.R
import com.example.bookie.models.Book
import com.example.bookie.models.EmptyReviewTab
import com.example.bookie.models.ReviewTab
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.ReviewRepository
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import com.example.bookie.ui.loader.LoaderFragment
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

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val reviewText: TextView = view.findViewById(R.id.review_text)
        val submitButton: Button = view.findViewById(R.id.submit_button)
        val bookTitle: TextView = view.findViewById(R.id.book_title)
        val bookAuthor: TextView = view.findViewById(R.id.book_author)
        val bookImage: ImageView = view.findViewById(R.id.book_image)
        val bookCategories: TextView = view.findViewById(R.id.book_categories)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        val reviewsQuantity: TextView = view.findViewById(R.id.reviews_quantity)
        val reviewRating: RatingBar = view.findViewById(R.id.review_rating)
    }

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
    ): RecyclerView.ViewHolder {
        injector.inject(appKodein)
        when(viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_book_profile_header, parent, false)

                return HeaderViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_review_card, parent, false)

                return ReviewCardViewHolder(view)
            }
            else -> throw Exception("Invalid ReviewTab type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = myDataSet[position]

        if (position == 0 && data.image == null) {
            handleHeader(holder as HeaderViewHolder, data as EmptyReviewTab)
        } else {
            handleBody(holder as ReviewCardViewHolder, data)
        }


    }

    private fun handleHeader(holder: HeaderViewHolder, data: EmptyReviewTab) {
        val loaderFragment: LoaderFragment?
        setBookData(holder, data.book)

        val currentContext = context as AppCompatActivity
        val fragment: Fragment? = currentContext.supportFragmentManager.findFragmentById(R.id.fragment_loader)
        loaderFragment = fragment as LoaderFragment?
        loaderFragment?.hideLoader(holder.submitButton)

        holder.submitButton.setOnClickListener { onSubmitReview(it, data.book, holder, loaderFragment, currentContext) }
    }

    private fun setBookData(holder: HeaderViewHolder, book: Book) {

        holder.bookTitle.text = book.title
        holder.bookAuthor.text = book.authors?.reduce { a, b -> "$a, $b" } ?: ""
        Picasso.get().load(book.imageLinks?.thumbnail)
                .into(holder.bookImage)
        holder.bookCategories.text = book.categories?.reduce { a, b -> "$a, $b" } ?: ""
        if (book.review != null) {
            holder.ratingBar.visibility = View.VISIBLE
            holder.reviewsQuantity.visibility = View.VISIBLE
            holder.ratingBar.rating = book.review?.rating ?: 0f
            holder.reviewsQuantity.text = "(${book.review?.reviewAmount ?: 0})"
        } else {
            holder.reviewsQuantity.visibility = View.GONE
            holder.ratingBar.visibility = View.GONE
        }

    }

    private fun onSubmitReview(view: View, book: Book, holder: HeaderViewHolder, loaderFragment: LoaderFragment?, currentContext: AppCompatActivity) {

        val reviewRepository = reviewRepository ?: return

        var bookReviewed = false

        val recyclerView = currentContext.findViewById<RecyclerView>(R.id.reviews_container)

        if (!TextValidator.hasErrors(holder.reviewText)) {
            if (holder.reviewRating.rating == 0f) SnackbarUtil.showSnackbar(
                    view,
                    currentContext.getString(R.string.rating_required)
            ) else {
                loaderFragment?.showLoader(holder.submitButton)
                val (bookStatus, reviewStatus) = reviewRepository.postReview(
                        book.id,
                        holder.reviewText.text.toString(),
                        holder.reviewRating.rating.toInt(),
                        !bookReviewed
                )
                bookStatus.observe(currentContext, Observer<RepositoryStatus<Book>> {
                    when (it) {
                        is RepositoryStatus.Success -> {
                            loaderFragment?.hideLoader(holder.submitButton)
                            holder.submitButton.text = currentContext.getText(R.string.edit_review)
                            bookReviewed = true
                            SnackbarUtil.showSnackbar(
                                    view,
                                    currentContext.getString(R.string.successful_review)
                            )
                            setBookData(holder, it.data)
                        }
                        is RepositoryStatus.Error -> {
                            loaderFragment?.hideLoader(holder.submitButton)
                            SnackbarUtil.showSnackbar(view, it.error)
                        }
                        is RepositoryStatus.Loading -> return@Observer
                    }
                })
                reviewStatus.observe(currentContext, Observer {
                    when (it) {
                        is RepositoryStatus.Success -> {
                            val index = myDataSet.indexOfFirst { r -> r.userId == it.data.userId }
                            if (index != -1) {
                                myDataSet.removeAt(index)
                                this.notifyItemRemoved(index)
                            }
                            myDataSet.add(1, it.data.toReviewTab())
                            this.notifyItemInserted(1)
                            recyclerView.scrollToPosition(1)
                        }
                    }
                })

            }
        }
    }

    private fun handleBody(holder: ReviewCardViewHolder, data: ReviewTab) {
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

    override fun getItemViewType(position: Int): Int = if (position == 0 && myDataSet[position].image == null) 0 else 1

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