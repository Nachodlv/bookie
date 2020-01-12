package com.example.bookie.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.models.FeedItem


class OnScrollListenerMock<T>(
    private val layoutManager: LinearLayoutManager, private val adapter: MyAdapter,
    private var dataList: MutableList<T>, private val fullList: MutableList<T>
) : RecyclerView.OnScrollListener() {

    private var loading = false

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (!loading) {
            if (layoutManager.findLastCompletelyVisibleItemPosition() == dataList.size - 1) {
                //bottom of list!
                loading = true
                loadMore(recyclerView)
            }
        }
    }

    private fun loadMore(recyclerView: RecyclerView) {
        val currentSize = dataList.size
        val nextLimit = if (currentSize + 10 > fullList.size) fullList.size else currentSize + 10

        dataList.addAll(currentSize, fullList.subList(currentSize, nextLimit))

        recyclerView.post { adapter.notifyDataSetChanged() }
        loading = false
    }
}

class OnScrollListener<T>(
    private val layoutManager: LinearLayoutManager,
    private val adapter: MyAdapter,
    private var dataList: MutableList<T>,
    private val loadMore: (index: Int, callback: (List<T>) -> Unit) -> Unit
) : RecyclerView.OnScrollListener() {

    private var loading = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (!loading) {
            if (layoutManager.findLastCompletelyVisibleItemPosition() == dataList.size - 1) {
                //bottom of list!
                loading = true
                loadMore(recyclerView)
            }
        }
    }

    private fun loadMore(recyclerView: RecyclerView) {
        val currentIndex = dataList.size
        loadMore(currentIndex) { list ->
            dataList.addAll(currentIndex, list)
            recyclerView.post { adapter.notifyDataSetChanged() }
            loading = false
        }
    }
}