package com.example.g1_final_project.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.*
import androidx.navigation.ui.*
import com.example.g1_final_project.R
import com.example.g1_final_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentContainerView)
        val drawerLayout = binding.drawerLayout
        binding.navView.setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.historyFragment,
                R.id.aboutFragment,
                R.id.settingsFragment,
                R.id.faqFragment,
                R.id.mapsFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            logout()
            true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun logout() {
        val logout = Intent(this, LoginActivity::class.java)
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(logout)
        finish()
    }
}