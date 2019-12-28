package com.example.bookie.ui.shelf

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShelfViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is shelf Fragment"
    }
    val text: LiveData<String> = _text
}