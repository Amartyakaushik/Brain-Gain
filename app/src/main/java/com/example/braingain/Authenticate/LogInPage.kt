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

        // Shared preference -> ( to store the username and show when the user navigate back to the application or when the logout
        val editor = getSharedPreferences("LOGIN_AUTH_DETAIL", MODE_PRIVATE)
        binding.email.setText(editor.getString("userName",null))

        binding.animLogIn.playAnimation()
        binding.logInBtn.setOnClickListener {

            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            // Shared preference ( 31st dec 2024)
            val editor = getSharedPreferences("LOGIN_AUTH_DETAIL", MODE_PRIVATE).edit()
            editor.putString("userName",email)
            editor.apply()


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            if(auth.currentUser!!.isEmailVerified){
                                startActivity(Intent(this, HomePage::class.java))
                                finish()
                            }else{
                                Toast.makeText(this, "Please verify your email.", Toast.LENGTH_SHORT).show()
                            }
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


//    override fun onStart() {
//        super.onStart()
//        val fromSignUp = loginIntent.getBooleanExtra()
//        if(FirebaseAuth.getInstance().currentUser != null){
//            startActivity(Intent(this,HomePage::class.java))
//            finish()
//        }
//    }

    override fun onStart() {
        super.onStart()
        // Check if navigating from Sign-Up page
        val fromSignUp = intent.getBooleanExtra("fromSignUp", false)

        if (!fromSignUp && FirebaseAuth.getInstance().currentUser != null) {
            if(auth.currentUser!!.isEmailVerified){
                startActivity(Intent(this, HomePage::class.java))
                finish()
            }
        }

        // Remove the flag to avoid interference in future launches
        intent.removeExtra("fromSignUp")
    }

}
// nothing just added some more feature to ensure the user is authorizing with the correct email only by verifying the email through the verification link sent on the email
