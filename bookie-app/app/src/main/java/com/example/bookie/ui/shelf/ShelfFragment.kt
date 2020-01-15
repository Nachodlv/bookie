package com.example.bookie.ui.shelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.bookie.R

class ShelfFragment : Fragment() {

    private lateinit var shelfViewModel: ShelfViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shelfViewModel =
            ViewModelProviders.of(this).get(ShelfViewModel::class.java)
        return inflater.inflate(R.layout.fragment_shelf, container, false)
    }
}