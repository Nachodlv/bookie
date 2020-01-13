package com.example.bookie.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookie.ui.profile.tabs.followers.ProfileFollowersFragment
import com.example.bookie.ui.profile.tabs.following.ProfileFollowingsFragment
import com.example.bookie.ui.profile.tabs.reviews.ProfileReviewsFragment

class ViewPagerAdapter(fragment: Fragment, private val userId: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return when(position) {
            0 -> {
                val bundle = Bundle()
                bundle.putString("userId", userId)
                val fragment = ProfileReviewsFragment()
                fragment.arguments = bundle
                fragment
            }
            1 -> ProfileFollowingsFragment()
            2 -> ProfileFollowersFragment()
            else -> throw Exception("Invalid pager position")
        }
    }
}