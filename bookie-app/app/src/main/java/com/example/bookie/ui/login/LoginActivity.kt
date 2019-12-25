package com.example.bookie.ui.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bookie.R
import com.example.bookie.utils.TextValidator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_loader.*
import kotlinx.android.synthetic.main.login_main.*
import java.util.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)

        email.addTextChangedListener(emailValidator)

        password.setOnEditorActionListener { textView, _, _ ->
            login(textView.rootView)
            password.clearFocus()
            true
        }

        login_button.setOnClickListener { login(it) }

        loader.visibility = View.GONE

    }

    private val emailValidator: TextValidator
        get() =
            object : TextValidator(email) {
                override fun validate(textView: TextView?, text: String?) {
                    if (text == null) return

                    if (TextUtils.isEmpty(text) || !Patterns.EMAIL_ADDRESS.matcher(text).matches())
                        if (textView != null)
                            textView.error = resources.getString(R.string.invalid_email)
                }
            }

    private fun login(view: View) {
        Snackbar.make(
            view,
            "Email: ${email.text}, password: ${password.text}",
            Snackbar.LENGTH_LONG
        )
            .setAction("Action", null).show()
        showLoader()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    hideLoader()
                }
            }
        }, 2000)
    }


    private fun showLoader() {
        loader.visibility = View.VISIBLE
        login_button.visibility = View.GONE
    }

    private fun hideLoader() {
        loader.visibility = View.GONE
        login_button.visibility = View.VISIBLE
    }

}