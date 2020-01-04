package com.example.bookie.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bookie.MainActivity
import com.example.bookie.R
import com.example.bookie.repositories.AuthRepository
import com.example.bookie.repositories.UserRepository
import com.example.bookie.ui.loader.LoaderFragment
import com.example.bookie.utils.EmailValidator
import com.example.bookie.utils.TextValidator
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.login_main.*


class LoginActivity : AppCompatActivity() {

    private var loaderFragment: LoaderFragment = LoaderFragment()
    private val injector = KodeinInjector()
    private val authRepository: AuthRepository by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)

        injector.inject(appKodein())

        containsValidToken()

        email.addTextChangedListener(EmailValidator(email))

        password.setOnEditorActionListener { textView, _, _ ->
            login(textView.rootView)
            password.clearFocus()
            true
        }

        login_button.setOnClickListener { login(it) }

        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_loader)
        if (fragment != null) loaderFragment = fragment as LoaderFragment

        register_button.setOnClickListener { goToRegisterView() }

        showMessage()
    }

    private fun login(view: View) {
        if (TextValidator.hasErrors(email).or(
                TextValidator.hasErrors(password)
            )
        ) return

        loaderFragment.showLoader(login_button)
        authRepository.loginUser(
            email.text.toString(),
            password.text.toString(),
            { loginSuccessful() },
            { _, message -> loginUnsuccessful(message, view) })
    }

    private fun loginSuccessful() {
        loaderFragment.hideLoader(login_button)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun loginUnsuccessful(response: String, view: View) {
        loaderFragment.hideLoader(login_button)

        Snackbar.make(
            view,
            response,
            Snackbar.LENGTH_LONG
        ).setAction("Action", null).show()

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

    private fun containsValidToken() {
        authRepository.isUserLogged()
    }

}