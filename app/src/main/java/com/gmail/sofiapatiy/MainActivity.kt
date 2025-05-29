package com.gmail.sofiapatiy

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gmail.sofiapatiy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val navView: BottomNavigationView = binding.navView
        setContentView(binding.root)

        val onDestinationChangedListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                navView.isVisible = when (destination.id) {
                    R.id.navigation_auth,
                    R.id.navigation_signup,
                    R.id.navigation_new_task,
                    R.id.navigation_task_details -> false

                    else -> true
                }
            }

        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                ?.findNavController() ?: return

        // remove previously attached listener (if any), to avoid possible memory leaks
        try {
            navController.removeOnDestinationChangedListener(onDestinationChangedListener)
        } catch (e: Exception) {
            Log.d("MainActivity", "destinationChangedListener failure: $e")
        } finally {
            navController.addOnDestinationChangedListener(onDestinationChangedListener)
        }
        navView.setupWithNavController(navController)

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                when (navController.currentDestination?.id) {
                    R.id.navigation_auth,
                    R.id.navigation_home,
                    R.id.navigation_calendar,
                    R.id.navigation_settings,
                    R.id.navigation_about -> finish()

                    else -> navController.navigateUp()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}