package com.example.bookie.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookie.ui.profile.tabs.followers.ProfileFollowersFragment
import com.example.bookie.ui.profile.tabs.following.ProfileFollowingsFragment
import com.example.bookie.ui.profile.tabs.reviews.ProfileReviewsFragment

class ViewPagerAdapter(fragment: Fragment, userId: String, private val isPrivateProfile: Boolean) :
    FragmentStateAdapter(fragment) {

    val bundle = Bundle()

    init {
        bundle.putString("userId", userId)
    }

    override fun getItemCount(): Int = if(isPrivateProfile) 3 else 1

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return when (position) {
            0 -> addBundle(ProfileReviewsFragment())
            1 -> addBundle(ProfileFollowingsFragment())
            2 -> addBundle(ProfileFollowersFragment())
            else -> throw Exception("Invalid pager position")
        }
    }

    private fun addBundle(fragment: Fragment): Fragment {
        fragment.arguments = bundle
        return fragment
    }
}