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
import com.example.bookie.models.UserPreview
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injector.inject(appKodein())

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

        //TODO get from backend, will only return users who follow me or/and who I follow. With pagination.

        val users: MutableList<UserPreview> = arrayListOf(
                UserPreview("1", "Gianluca", "Scolaro", isFollower = false, isFollowedByMe = true),
                UserPreview("2", "Pedro", "Perez", isFollower = true, isFollowedByMe = true),
                UserPreview("3", "Jacobo", "Santos de La Virgen de Nazareth Segundo", isFollower = true, isFollowedByMe = false),
                UserPreview("4", "Juan", "Carlos", isFollower = true, isFollowedByMe = true),
                UserPreview("5", "Bob", isFollower = true, isFollowedByMe = false),
                UserPreview("6", "Gianluca", "Scolaro 2", isFollower = true, isFollowedByMe = true),
                UserPreview("7", "Gianluca", "Scolaro 3", isFollower = true, isFollowedByMe = false),
                UserPreview("8", "Gianluca", "Scolaro 4", isFollower = false, isFollowedByMe = true),
                UserPreview("9", "Gianluca", "Scolaro 5", isFollower = true, isFollowedByMe = true),
                UserPreview("10", "Gianluca", "Scolaro 6", isFollower = false, isFollowedByMe = true),
                UserPreview("11", "Gianluca", "Scolaro 7", isFollower = true, isFollowedByMe = true),
                UserPreview("12", "Gianluca", "Scolaro 8", isFollower = true, isFollowedByMe = false),
                UserPreview("13", "Gianluca", "Scolaro 9", isFollower = false, isFollowedByMe = true),
                UserPreview("14", "Gianluca", "Scolaro 10", isFollower = true, isFollowedByMe = true),
                UserPreview("15", "Gianluca", "Scolaro 11", isFollower = true, isFollowedByMe = false),
                UserPreview("16", "Gianluca", "Scolaro 12", isFollower = false, isFollowedByMe = true),
                UserPreview("17", "Gianluca", "Scolaro 13", isFollower = true, isFollowedByMe = false),
                UserPreview("18", "Gianluca", "Scolaro 14", isFollower = true, isFollowedByMe = true),
                UserPreview("19", "Gianluca", "Scolaro 15", isFollower = true, isFollowedByMe = true),
                UserPreview("20", "Gianluca", "Scolaro 16", isFollower = true, isFollowedByMe = false),
                UserPreview("21", "Gianluca", "Scolaro 17", isFollower = true, isFollowedByMe = false),
                UserPreview("22", "Gianluca", "Scolaro 18", isFollower = false, isFollowedByMe = true),
                UserPreview("23", "Gianluca", "Scolaro 19", isFollower = true, isFollowedByMe = true),
                UserPreview("24", "Gianluca", "Scolaro 20", isFollower = true, isFollowedByMe = true)
        )

        profileViewModel.storeUser(users)

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fragment = ProfileHeaderFragment()
        fragment.arguments = Bundle().apply {
            putBoolean("allow_follow", false)
        }
        parentFragmentManager.beginTransaction()
                .add(R.id.header_container, fragment).commit()


        val userId = "42"

        val observer = Observer<RepositoryStatus<User>> {
            when(it) {
                is RepositoryStatus.Success -> buildTabs(view, it.data)
                is RepositoryStatus.Error -> SnackbarUtil.showSnackbar(view, it.error)
            }
        }

        if(privateProfile)
            authRepository.getUserLoggedIn().observe(viewLifecycleOwner, observer)
        else
            userRepository.getUser(userId).observe(viewLifecycleOwner, observer)

    }

    private fun buildTabs(view: View, user: User) {
        profileHeaderViewModel.storeUser(user)
        profileHeaderViewModel.storeFollowers(user.followerAmount)

        // View page and tabs initialization
        viewPagerAdapter = ViewPagerAdapter(this, user.id)
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

        activity?.supportFragmentManager?.findFragmentById(R.id.fragment_loader)
    }
}