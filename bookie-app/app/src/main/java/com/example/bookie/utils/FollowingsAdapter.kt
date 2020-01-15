package com.example.bookie.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.User
import com.example.bookie.models.UserPreview
import com.example.bookie.ui.profile.ProfileFragment
import com.example.bookie.ui.profile.PublicProfile


class FollowingsAdapter(private val myDataSet: List<UserPreview>, private val context: Context?) :
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
        holder.name.text = "${data.firstName} ${data.lastName}"
        holder.name.setOnClickListener { goToPublicProfile(data.id) }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataSet.size

    private fun goToPublicProfile(userId: String) {
        val currentContext = context?: return
        val intent = Intent(currentContext, PublicProfile::class.java)
        val bundle = Bundle()
        bundle.putString("userId", userId)
        intent.putExtras(bundle)
        ContextCompat.startActivity(currentContext, intent, bundle)
    }
}