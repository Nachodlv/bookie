package com.example.bookie.utils

import android.content.res.Resources
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bookie.R


abstract class TextValidator(private val textView: TextView) : TextWatcher {
    abstract fun validate(textView: TextView, text: String)

    override fun afterTextChanged(s: Editable) {
        val text = textView.text.toString()
        validate(textView, text)
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
    }

}

class EmailValidator(textView: TextView, private val resources: Resources) :
    TextValidator(textView) {

    override fun validate(textView: TextView, text: String) {

        if (TextUtils.isEmpty(text) || !Patterns.EMAIL_ADDRESS.matcher(text).matches())
            textView.error = resources.getString(R.string.invalid_email)
    }

}