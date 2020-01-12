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



class FollowingsAdapter(private val myDataSet: List<String>) :
        RecyclerView.Adapter<FollowingsAdapter.FollowingCardViewHolder>() {

    class FollowingCardViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FollowingCardViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_following_card, parent, false)

        return FollowingCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingCardViewHolder, position: Int) {
        val data = myDataSet[position]
        holder.name.text = data
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataSet.size
}