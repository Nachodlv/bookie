package com.example.bookie.ui.profile.tabs.followers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.UserPreview
import com.example.bookie.ui.profile.ProfileViewModel
import com.example.bookie.utils.FollowingsAdapter
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.OnScrollListenerMock
import kotlinx.android.synthetic.main.fragment_followings_tab.*

class ProfileFollowersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_followers_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val profileViewModel = activity?.run {
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        profileViewModel.users.observe(viewLifecycleOwner,
                Observer<MutableList<UserPreview>> {
                    users -> setUpRecyclerView(view,
                        users.filter { user -> user.isFollower }.map { user -> "${user.firstName} ${user.lastName}" }.toMutableList()
                )
                })
    }

    private fun setUpRecyclerView(view: View, followers: MutableList<String>){
        val myDataSet: MutableList<String> = mutableListOf<String>().apply { addAll(followers.subList(0, 10)) }

        val recList = view.findViewById(R.id.followers_container) as RecyclerView
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = FollowingsAdapter(myDataSet)
        viewManager.orientation = LinearLayoutManager.VERTICAL

        recList.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }

        recList.addOnScrollListener(OnScrollListenerMock(viewManager, viewAdapter, myDataSet, followers))
    }
}