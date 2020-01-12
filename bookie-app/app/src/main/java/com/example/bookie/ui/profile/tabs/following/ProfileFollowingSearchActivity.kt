package com.example.bookie.ui.profile.tabs.following

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein


class ProfileFollowingSearchActivity : AppCompatActivity() {

    private val injector = KodeinInjector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.bookie.R.layout.profile_followings_search_main)

        injector.inject(appKodein())

        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(com.example.bookie.R.id.toolbar)

        setSupportActionBar(findViewById(com.example.bookie.R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationIcon(com.example.bookie.R.drawable.ic_arrow_back_black_24dp)
        toolbar.setTitle(com.example.bookie.R.string.register)
        toolbar.setNavigationOnClickListener { finish() }
    }


}
