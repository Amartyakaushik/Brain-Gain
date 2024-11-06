package com.example.braingain.Authenticate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.braingain.HomePage
import com.example.braingain.databinding.ActivityLogInPageBinding
import com.google.firebase.auth.FirebaseAuth

class LogInPage : AppCompatActivity() {
    private lateinit var binding : ActivityLogInPageBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInPageBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

//        binding.animLogIn.visibility = View.VISIBLE
//        binding.animLogIn.setAnimation(R.raw.you_won)
        binding.animLogIn.playAnimation()
        binding.logInBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, HomePage::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                            Log.e("SignIn", "Sign-In Error: ${it.exception?.message}")
                        }
                    }
            }

        }

        // to register user
        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpPage::class.java))
        }
    }


    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this,HomePage::class.java))
            finish()
        }
    }
}

