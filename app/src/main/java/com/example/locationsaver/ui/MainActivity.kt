package com.example.locationsaver.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.example.locationsaver.R
import com.example.locationsaver.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.EntryPoint


@EntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    lateinit var mainActivityBinding: ActivityMainBinding
    lateinit var navigationController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    //    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: BottomNavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initViews()
        createNavigationDrawer()


    }

    private fun createNavigationDrawer() {
//        navigationView.setupWithNavController(navigationController)
        navigationView.setupWithNavController(navigationController)
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initViews() {
        navigationView = mainActivityBinding.fragmentHomeBottomNav
//        drawerLayout = mainActivityBinding.activityMainDrawerLayout
//        toolbar = mainActivityBinding.activityMainToolBar
//        setSupportActionBar(toolbar)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.activityMain_frameLayout) as NavHostFragment
        navigationController = navHost.findNavController()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.favouriteFragment,
                R.id.locationsFragment,
            )
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        return navigationController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navigationController) || super.onOptionsItemSelected(
            item
        )
    }


}