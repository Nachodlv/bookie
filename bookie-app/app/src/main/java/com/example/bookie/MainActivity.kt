package com.example.bookie

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.bookie.repositories.AuthRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.ui.login.LoginActivity
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var drawerLayout: DrawerLayout

    private val injector = KodeinInjector()


    private val authRepository: AuthRepository by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        injector.inject(appKodein())

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
//
//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        // Set up Action Bar
        val navController = host.navController

        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_scan,
                R.id.nav_shelf, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBar(navController)
        setupNavigationMenu(navController)
    }


    /**
     * When the side-menu is open, pressing the back button should close it.
     * */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavigationMenu(navController: NavController) {
        val sideNavView = findViewById<NavigationView>(R.id.nav_view) ?: return
        sideNavView.setupWithNavController(navController)
        sideNavView.setNavigationItemSelectedListener {
            onNavigationItemSelected(
                it,
                navController
            )
        }
        authRepository.getUserLoggedIn().observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> text_nav_header.text =
                    "Bookie - ${it.data.firstName} ${it.data.lastName}"
                is RepositoryStatus.Error -> text_nav_header.text = "Bookie"
            }
        })
    }

    private fun setupActionBar(navController: NavController) {
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    private fun onNavigationItemSelected(
        menuItem: MenuItem,
        navController: NavController
    ): Boolean {
        when (menuItem.itemId) {
            R.id.action_logout -> onLogoutPressed()
            else -> menuItem.onNavDestinationSelected(navController)
        }
        drawerLayout.closeDrawers()
        return true
    }

    private fun onLogoutPressed() {
        authRepository.logout()
        startActivity(Intent(this, LoginActivity::class.java))
    }


    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/


}
