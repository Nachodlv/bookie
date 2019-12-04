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
import kotlinx.android.synthetic.main.login_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)

        email.addTextChangedListener(emailValidator)

        login_button.setOnClickListener { login(it) }
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
    }


}