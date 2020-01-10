package com.example.bookie.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackbarUtil {
    companion object {
        fun showSnackbar(view: View, message: String) {
            Snackbar.make(
                view,
                message,
                Snackbar.LENGTH_LONG
            ).setAction("Action", null).show()
        }
    }
}