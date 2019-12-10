package com.example.bookie.ui.login

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.bookie.R
import com.example.bookie.ui.loader.LoaderFragment
import com.example.bookie.utils.EmailValidator
import com.example.bookie.utils.TextValidator
import kotlinx.android.synthetic.main.register_main.*

class RegisterActivity : AppCompatActivity() {

    private var loaderFragment: LoaderFragment = LoaderFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)

        email.addTextChangedListener(EmailValidator(email, resources))
        password.addTextChangedListener(passwordValidator)
        repeat_password.addTextChangedListener(repeatPasswordValidator)


        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_loader)
        if(fragment != null) loaderFragment = fragment as LoaderFragment

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setTitle(R.string.register)
        toolbar.setNavigationOnClickListener { finish() }

        register_button.setOnClickListener { register() }

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
                if (text != email.text.toString()) {
                    textView.error = resources.getString(R.string.invalid_repeat_password)
                }
            }

        }

    private fun register() {

    }
}
