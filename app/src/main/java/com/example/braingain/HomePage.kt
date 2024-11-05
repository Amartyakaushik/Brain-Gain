package com.example.braingain

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

@Keep
class HomePage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //---------------------------------------------------------------------------------
//        var navController:NavController = findNavController(R.id.containerView)
//        var navBar:BottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_bar)
//                                          OR

//            val navHostFragment = supportFragmentManager.findFragmentById(R.id.containerView) as NavHostFragment
//            val navController = navHostFragment.navController
//            val navBar = findViewById<BottomNavigationView>(R.id.nav_bar)
//            navBar.setupWithNavController(navController)
        //----------------------------------   OR   ------------------------------------------

        try {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.containerView) as NavHostFragment
            Log.d("HomePage2", "NavHostFragment initialized: $navHostFragment")
            val navController = navHostFragment.navController
            Log.d("HomePage2", "NavController initialized: $navController")

            val navBar = findViewById<BottomNavigationView>(R.id.nav_bar)
            Log.d("HomePage2", "BottomNavigationView initialized: $navBar")

            navBar.setupWithNavController(navController)
            Log.d("HomePage2", "BottomNavigationView linked with NavController successfully")

        } catch (e: Exception) {
            Log.e("HomePage2", "Error setting up navigation: ${e.localizedMessage}", e)
        }
    }
}