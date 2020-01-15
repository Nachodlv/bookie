package com.example.bookie.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.bookie.R
import com.example.bookie.models.User
import com.example.bookie.repositories.AuthRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.UserRepository
import com.example.bookie.ui.profile.header.ProfileHeaderFragment
import com.example.bookie.ui.profile.header.ProfileHeaderViewModel
import com.example.bookie.utils.SnackbarUtil
import com.example.bookie.utils.ViewPagerAdapter
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileHeaderViewModel: ProfileHeaderViewModel

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2

    private val injector = KodeinInjector()
    private val userRepository: UserRepository by injector.instance()
    private val authRepository: AuthRepository by injector.instance()

    private var privateProfile = true //TODO modify
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injector.inject(appKodein())

        userId = arguments?.getString("userId")
        privateProfile = userId == null

        profileHeaderViewModel = activity?.run {
            ViewModelProvider(this).get(ProfileHeaderViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel = activity?.run {
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fragment = ProfileHeaderFragment()
        fragment.arguments = Bundle().apply {
            putBoolean("allow_follow", !privateProfile)
        }
        parentFragmentManager.beginTransaction()
            .add(R.id.header_container, fragment).commit()


        val observer = Observer<RepositoryStatus<User>> {
            when (it) {
                is RepositoryStatus.Success -> buildTabs(view, it.data)
                is RepositoryStatus.Error -> SnackbarUtil.showSnackbar(view, it.error)
            }
        }

        if (privateProfile)
            authRepository.getUserLoggedIn().observe(viewLifecycleOwner, observer)
        else {
            val userId = userId ?: return
            userRepository.getUser(userId).observe(viewLifecycleOwner, observer)
        }

    }

    private fun buildTabs(view: View, user: User) {
        profileHeaderViewModel.storeUser(user)
        profileHeaderViewModel.storeFollowers(user.followerAmount)

        // View page and tabs initialization
        viewPagerAdapter = ViewPagerAdapter(this, user.id, privateProfile)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = viewPagerAdapter

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (privateProfile) when (position) {
                0 -> "Reviews"
                1 -> "Following"
                2 -> "Followers"
                else -> "OutOfBounds $position"
            }
            else when (position) {
                0 -> "Reviews"
                else -> "OutOfBounds $position"
            }
        }.attach()

        activity?.supportFragmentManager?.findFragmentById(R.id.fragment_loader)

    }


}