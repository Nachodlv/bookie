package com.example.bookie.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bookie.R
import com.example.bookie.ui.loader.LoaderFragment
import com.example.bookie.utils.EmailValidator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.login_main.*
import java.util.*


class LoginActivity : AppCompatActivity() {

    private var loaderFragment: LoaderFragment = LoaderFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)

        email.addTextChangedListener(EmailValidator(email))

        password.setOnEditorActionListener { textView, _, _ ->
            login(textView.rootView)
            password.clearFocus()
            true
        }

        login_button.setOnClickListener { login(it) }

        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_loader)
        if(fragment != null) loaderFragment = fragment as LoaderFragment

        register_button.setOnClickListener { goToRegisterView() }

        showMessage()
    }

    private fun login(view: View) {
        Snackbar.make(
            view,
            "Email: ${email.text}, password: ${password.text}",
            Snackbar.LENGTH_LONG
        )
            .setAction("Action", null).show()
        loaderFragment.showLoader(login_button)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    loaderFragment.hideLoader(login_button)
                }
            }
        }, 2000)
    }

    private fun goToRegisterView() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun showMessage() {
        val bundle = intent.extras ?: return
        val message = bundle.getString("message") ?: return
        Snackbar.make(
            email.rootView,
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Action", null).show()
    }

}