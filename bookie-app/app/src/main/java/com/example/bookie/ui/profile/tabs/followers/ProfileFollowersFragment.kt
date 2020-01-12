package com.example.bookie.ui.profile.tabs.followers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.R
import com.example.bookie.utils.FollowingsAdapter
import com.example.bookie.utils.OnScrollListener
import com.example.bookie.utils.OnScrollListenerMock

class ProfileFollowersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_followers_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //TODO get from backend

        val followers: MutableList<String> = arrayListOf(
                "Eduardo Lalor",
                "Pedro Perez",
                "Jacobo Santos de La Virgen de Nazareth Segundo",
                "Juan Carlos",
                "Bob",
                "Gianluca Scolaro 2",
                "Gianluca Scolaro 3",
                "Gianluca Scolaro 4",
                "Gianluca Scolaro 5",
                "Gianluca Scolaro 6",
                "Gianluca Scolaro 7",
                "Gianluca Scolaro 8",
                "Gianluca Scolaro 9",
                "Gianluca Scolaro 10",
                "Gianluca Scolaro 11",
                "Gianluca Scolaro 12",
                "Gianluca Scolaro 13",
                "Gianluca Scolaro 14"
        )

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