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

class ProfileHeaderFragment : Fragment() {

    private lateinit var profileHeaderViewModel: ProfileHeaderViewModel

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


    private fun addFollowListener(user: User, button: Button) {

    }


}