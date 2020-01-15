package com.example.bookie.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bookie.R
import com.example.bookie.repositories.AuthRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.ui.loader.LoaderFragment
import com.example.bookie.utils.TextValidator
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPassword : AppCompatActivity() {

    private val injector = KodeinInjector()
    private val authRepository: AuthRepository by injector.instance()

    private var token: String? = null
    private var loaderFragment: LoaderFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        injector.inject(appKodein())

        token = intent.data?.getQueryParameter("token")
        new_password.addTextChangedListener(passwordValidator)
        repeat_new_password.addTextChangedListener(repeatPasswordValidator)
        change_password_button.setOnClickListener { onSubmit() }

        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_loader)
        if (fragment != null) loaderFragment = fragment as LoaderFragment
    }

    private val passwordValidator: TextValidator
        get() = object : TextValidator(new_password) {
            override fun validate(textView: TextView, text: String) {
                if (text.length < 6) {
                    textView.error = resources.getString(R.string.invalid_password)
                }
            }

        }

    private val repeatPasswordValidator: TextValidator
        get() = object : TextValidator(repeat_new_password) {
            override fun validate(textView: TextView, text: String) {
                if (text != new_password.text.toString()) {
                    textView.error = resources.getString(R.string.invalid_repeat_password)
                }
            }

        }

    private fun onSubmit() {
        if (TextValidator.hasErrors(new_password).or(TextValidator.hasErrors(repeat_new_password))) return
        val token = this.token ?: return
        loaderFragment?.showLoader(change_password_button)
        authRepository.changePassword(new_password.text.toString(), token).observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> goToLoginPage(it.data)
                is RepositoryStatus.Error -> {
                    loaderFragment?.hideLoader(change_password_button)
                }
            }
        })
    }

    private fun goToLoginPage(message: String) {
        val intent = Intent(this, LoginActivity::class.java)
        val bundle = Bundle()
        bundle.putString("message", message)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
