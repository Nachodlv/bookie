package com.example.bookie.ui.profile.header

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bookie.R
import com.example.bookie.models.User
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.UserRepository
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance

class ProfileHeaderFragment : Fragment() {

    private lateinit var profileHeaderViewModel: ProfileHeaderViewModel
    private val injector = KodeinInjector()
    private val userRepository: UserRepository by injector.instance()

    private var allowFollow: Boolean? = null

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
        allowFollow = arguments?.getBoolean("allow_follow")
        return inflater.inflate(R.layout.fragment_profile_header, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nameTextView = view.findViewById<TextView>(R.id.name)
        val emailTextView = view.findViewById<TextView>(R.id.email)
        val followButton = view.findViewById<Button>(R.id.follow_button)
        val followersTextView = view.findViewById<TextView>(R.id.followers)

        profileHeaderViewModel.user.observe(viewLifecycleOwner,
            Observer<User> { user ->
                nameTextView.text = "${user.firstName} ${user.lastName}"
                emailTextView.text = user.email
                val allowFollow = this.allowFollow?:return@Observer
                if(!allowFollow) return@Observer
                userRepository.isFollowed(user.id).observe(viewLifecycleOwner, Observer {
                    when(it) {
                        is RepositoryStatus.Success -> {
                            if(it.data) addUnFollowListener(user, followButton, followersTextView)
                            else addFollowListener(user, followButton, followersTextView)
                        }
                        is RepositoryStatus.Error -> SnackbarUtil.showSnackbar(view, it.error)
                    }
                })
            }
        )

        profileHeaderViewModel.followers.observe(viewLifecycleOwner,
            Observer<Int> { followers ->
                followersTextView.text = "$followers followers"
            }
        )

        arguments?.takeIf { it.containsKey("allow_follow") }?.apply {
            followButton.visibility = if (getBoolean("allow_follow")) View.VISIBLE else View.GONE
        }


    }


    private fun addFollowListener(user: User, button: Button, followersTextView: TextView) {
        button.text = context?.getString(R.string.follow)
        button.setOnClickListener {
            addUnFollowListener(user, button, followersTextView)
            user.followerAmount++
            setFollowers(followersTextView, user)
            userRepository.followUser(user.id).observe(viewLifecycleOwner, Observer {
                when(it) {
                    is RepositoryStatus.Error -> {
                        user.followerAmount--
                        setFollowers(followersTextView, user)
                        addFollowListener(user, button, followersTextView)
                    }
                }
            })
        }

    }

    private fun addUnFollowListener(user: User, button: Button, followersTextView: TextView) {
        button.text = context?.getString(R.string.unfollow)
        button.setOnClickListener {
            addFollowListener(user, button, followersTextView)
            user.followerAmount--
            setFollowers(followersTextView, user)
            userRepository.unFollowUser(user.id).observe(viewLifecycleOwner, Observer {
                when(it) {
                    is RepositoryStatus.Error -> {
                        user.followerAmount++
                        setFollowers(followersTextView, user)
                        addFollowListener(user, button, followersTextView)
                    }
                }
            })
        }
    }

    private fun setFollowers(followersTextView: TextView, user: User) {
        followersTextView.text = "${user.followerAmount} followers"
    }
}