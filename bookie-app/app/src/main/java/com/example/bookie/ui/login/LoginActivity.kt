package com.example.bookie.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bookie.MainActivity
import com.example.bookie.R
import com.example.bookie.models.User
import com.example.bookie.repositories.AuthRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.ui.loader.LoaderFragment
import com.example.bookie.utils.EmailValidator
import com.example.bookie.utils.SnackbarUtil
import com.example.bookie.utils.TextValidator
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
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

        forgot_password.setOnClickListener { forgotPassword(it) }
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

        SnackbarUtil.showSnackbar(view, response)
    }

    private fun goToRegisterView() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun showMessage() {
        val bundle = intent.extras ?: return
        val message = bundle.getString("message") ?: return
        SnackbarUtil.showSnackbar(email.rootView, message)
    }

    private fun containsValidToken() {
        val (isLogged, observable) = authRepository.isUserLogged()

        if (!isLogged) return

        observable?.observe(this, Observer<RepositoryStatus<User>> { status ->
            when (status) {
                is RepositoryStatus.Loading -> return@Observer
                is RepositoryStatus.Error -> return@Observer
                is RepositoryStatus.Success -> startActivity(Intent(this, MainActivity::class.java))
            }
        })
    }

    private fun forgotPassword(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(applicationContext.getString(R.string.recover_password))
        // Set up the input
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.hint = applicationContext.getString(R.string.email_input)

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(500, 0, 500, 0)
        input.layoutParams = lp
        input.addTextChangedListener(EmailValidator(input))

        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton(
            "Reset password"
        ) { _, _ -> }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        val dialog = builder.create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (!TextValidator.hasErrors(input))
                authRepository.recoverPassword(input.text.toString(), {
                    dialog.cancel()
                    SnackbarUtil.showSnackbar(
                        view,
                        applicationContext.getString(R.string.email_sent)
                    )
                }, { error, code ->
                    if (code == 404) input.error = error
                    else SnackbarUtil.showSnackbar(view, error)
                })
        }
    }


}