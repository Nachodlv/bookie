package com.example.bookie.ui.loader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.bookie.R
import kotlinx.android.synthetic.main.fragment_loader.*

class LoaderFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loader, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loader.visibility = View.GONE
    }

    fun showLoader(button: Button) {
        loader.visibility = View.VISIBLE
        button.visibility = View.GONE
    }

    fun hideLoader(button: Button) {
        loader.visibility = View.GONE
        button.visibility = View.VISIBLE
    }
}