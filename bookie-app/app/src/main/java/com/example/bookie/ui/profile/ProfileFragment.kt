package com.example.bookie.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.bookie.R
import com.example.bookie.models.User
import com.example.bookie.ui.profile.header.ProfileHeaderFragment
import com.example.bookie.ui.profile.header.ProfileHeaderViewModel
import com.example.bookie.ui.profile.tabs.reviews.ProfileReviewsFragment
import com.example.bookie.utils.ViewPagerAdapter
import com.github.salomonbrys.kodein.android.androidSupportFragmentScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator



class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileHeaderViewModel: ProfileHeaderViewModel

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileHeaderViewModel = activity?.run {
            ViewModelProvider(this).get(ProfileHeaderViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fragment = ProfileHeaderFragment()
        fragment.arguments = Bundle().apply {
            putBoolean("allow_follow", false)
        }
        parentFragmentManager.beginTransaction()
                .add(R.id.header_container, fragment).commit()

        //TODO get values from session/backend
        val user = User("17", "gianluca.scolaro@gmail.com", "Gianni", "Scolaro", 1976127012)
        val followers = 321

        profileHeaderViewModel.storeUser(user)
        profileHeaderViewModel.storeFollowers(followers)

        // View page and tabs initialization
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = viewPagerAdapter

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Reviews"
                1 -> "Following"
                2 -> "Followers"
                else -> "OutOfBounds $position"
            }
        }.attach()
    }
}