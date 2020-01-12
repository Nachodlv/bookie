package com.example.bookie.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookie.ui.profile.tabs.reviews.ProfileReviewsFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return when(position) {
            0 -> ProfileReviewsFragment()
            1 -> ProfileReviewsFragment()
            2 -> ProfileReviewsFragment()
            else -> throw Exception("Invalid pager position")
        }
        /*val fragment = ProfileReviewsFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt("object", position + 1)
        }
        return fragment*/
    }
}