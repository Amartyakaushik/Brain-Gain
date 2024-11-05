package com.example.braingain

//import LoginPage
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.braingain.Authenticate.LogInPage

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val startIntent = Intent(this,LogInPage::class.java)
//            val startIntent = Intent(this,SignInPage::class.java)
            startActivity(startIntent)
            finish()
        },1700)
    }
}