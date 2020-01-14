package com.example.bookie.ui.profile.tabs.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.models.ReviewTab
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.UserRepository
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.ReviewsAdapter
import com.example.bookie.utils.SnackbarUtil
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance


class ProfileReviewsFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_reviews_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        injector.inject(appKodein())

        loadReviews(view)

    }

    private fun loadReviews(view: View) {
        val userId = this.userId ?: return
        userRepository.getUserReviews(userId, 0, pageSize).observe(viewLifecycleOwner, Observer {
            when (it) {
                is RepositoryStatus.Success -> loadList(view, it.data.map { r -> r.toReviewTab() })
                is RepositoryStatus.Error -> SnackbarUtil.showSnackbar(view, it.error)
            }
        })
    }

    private fun loadList(view: View, reviews: List<ReviewTab>) {
        val myDataSet: MutableList<ReviewTab> = reviews.toMutableList()

        val recList = view.findViewById(R.id.reviews_container) as RecyclerView
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = ReviewsAdapter(myDataSet, context, false)
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
                myDataSet,
                pageSize
            ) { index, callback ->
                val userId = this.userId
                if (userId != null)
                    userRepository.getUserReviews(userId, index, pageSize).observe(
                        viewLifecycleOwner,
                        Observer {
                            when (it) {
                                is RepositoryStatus.Success -> callback(it.data.map { r -> r.toReviewTab() })
                                is RepositoryStatus.Error -> callback(emptyList())
                            }
                        })
            }
        )
    }
}