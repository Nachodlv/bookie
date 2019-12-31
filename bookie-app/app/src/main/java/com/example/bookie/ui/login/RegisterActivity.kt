package com.example.bookie.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.bookie.R
import com.example.bookie.api.client.UserClient
import com.example.bookie.models.User
import com.example.bookie.repositories.UserRepository
import com.example.bookie.ui.loader.LoaderFragment
import com.example.bookie.utils.EmailValidator
import com.example.bookie.utils.TextValidator
import com.example.bookie.view_models.UserViewModel
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.register_main.*

class RegisterActivity : AppCompatActivity() {

    private var loaderFragment: LoaderFragment = LoaderFragment()
    val injector = KodeinInjector()
    val repository: UserRepository by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)

        injector.inject(appKodein())

        addListeners()

        setupToolbar()

        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_loader)
        if (fragment != null) loaderFragment = fragment as LoaderFragment

    }

    private fun addListeners() {
        email.addTextChangedListener(EmailValidator(email))
        password.addTextChangedListener(passwordValidator)
        repeat_password.addTextChangedListener(repeatPasswordValidator)

        repeat_password.setOnEditorActionListener { textView, _, _ ->
            register(textView.rootView)
            true
        }

        register_button.setOnClickListener { register(it) }
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setTitle(R.string.register)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private val passwordValidator: TextValidator
        get() = object : TextValidator(password) {
            override fun validate(textView: TextView, text: String) {
                if (text.length < 6) {
                    textView.error = resources.getString(R.string.invalid_password)
                }
            }

        }

    private val repeatPasswordValidator: TextValidator
        get() = object : TextValidator(repeat_password) {
            override fun validate(textView: TextView, text: String) {
                if (text != password.text.toString()) {
                    textView.error = resources.getString(R.string.invalid_repeat_password)
                }
            }

        }

    private fun register(view: View) {
        if (TextValidator.hasErrors(email).or(
                TextValidator.hasErrors(name)
            ).or(
                TextValidator.hasErrors(password)
            ).or(
                TextValidator.hasErrors(repeat_password)
            ).or(TextValidator.hasErrors(lastName))
        ) return

        loaderFragment.showLoader(register_button)

        repository.registerUser(
            email.text.toString(),
            password.text.toString(),
            name.text.toString(),
            lastName.text.toString()
        ) { user, response -> onFinishRegistering(user, response, view) }
    }

    private fun onFinishRegistering(user: User?, response: String, view: View) {
        loaderFragment.hideLoader(register_button)

        if (user == null) {
            Snackbar.make(
                view,
                response,
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null).show()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
