package com.example.bookie.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class OnScrollListenerMock<T, F: RecyclerView.ViewHolder>(
    private val layoutManager: LinearLayoutManager, private val adapter: RecyclerView.Adapter<F>,
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

class OnScrollListener<T, F: RecyclerView.ViewHolder>(
    private val layoutManager: LinearLayoutManager,
    private val adapter: RecyclerView.Adapter<F>,
    private var dataList: MutableList<T>,
    private val pageSize: Int,
    private val loadMore: (index: Int, callback: (List<T>) -> Unit) -> Unit
) : RecyclerView.OnScrollListener() {

    private var loading = false
    private var finished = false

    init {
        if(dataList.size < pageSize) finished = true
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (!loading && !finished) {
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
            if(list.size < pageSize) finished = true
            dataList.addAll(currentIndex, list)
            recyclerView.post { adapter.notifyDataSetChanged() }
            loading = false
        }
    }
}