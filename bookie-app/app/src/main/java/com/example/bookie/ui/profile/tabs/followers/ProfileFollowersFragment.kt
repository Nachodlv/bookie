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
import com.example.bookie.models.User
import com.example.bookie.models.UserPreview
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.UserRepository
import com.example.bookie.ui.profile.ProfileViewModel
import com.example.bookie.utils.FollowingsAdapter
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.OnScrollListenerMock
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.fragment_followings_tab.*

class ProfileFollowersFragment : Fragment() {

    private val injector = KodeinInjector()
    private val userRepository: UserRepository by injector.instance()

    private var userId: String? = null
    private val pageSize = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userId = arguments?.getString("userId")

        return inflater.inflate(R.layout.fragment_followers_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        injector.inject(appKodein())

       searchFollowers(view)
    }

    override fun onResume() {
        super.onResume()
        val container= followings_container
        if(container != null) searchFollowers(container.rootView)
    }

    private fun searchFollowers(view: View) {
        val userId = userId ?: return
        userRepository.getUserFollowers(userId, 0, pageSize)
            .observe(viewLifecycleOwner, Observer<RepositoryStatus<List<UserPreview>>> {
                when (it) {
                    is RepositoryStatus.Success -> setUpRecyclerView(
                        view,
                        it.data.filter { user -> user.isFollowedByMe }.toMutableList()
                    )
                    is RepositoryStatus.Error -> SnackbarUtil.showSnackbar(view, it.error)
                }

            })
    }

    private fun setUpRecyclerView(view: View, followers: MutableList<UserPreview>){

        val recList = view.findViewById(R.id.followers_container) as RecyclerView
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = FollowingsAdapter(followers, context)
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

        recList.addOnScrollListener(
            OnScrollListener(
                viewManager,
                viewAdapter,
                followers,
                pageSize
            ) { index, callback ->
                val userId = userId
                if (userId != null) {
                    userRepository.getUserFollowers(userId, index, pageSize)
                        .observe(viewLifecycleOwner, Observer {
                            when (it) {
                                is RepositoryStatus.Success -> callback(it.data)
                                is RepositoryStatus.Error -> callback(emptyList())
                            }
                        })
                }
            })
    }
}