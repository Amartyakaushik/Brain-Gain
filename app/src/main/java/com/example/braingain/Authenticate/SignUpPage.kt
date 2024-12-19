package com.example.braingain.Authenticate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.braingain.ModelClass.AuthenticationUserModel
import com.example.braingain.databinding.ActivitySignupPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class SignUpPage : AppCompatActivity() {
    private lateinit var binding : ActivitySignupPageBinding
    private lateinit var auth : FirebaseAuth
    private var db = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // For authentication using firebase
        binding = ActivitySignupPageBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

//        var singUpBtn = binding.singUpBtn
//
//        var name = binding.name
//        var age = binding.age
////        val age = binding.age.text.toString()
//        var email = binding.email
//        var password = binding.password


//        val name = binding.name.text.toString()
//        val age = binding.age
////        val age = binding.age.text.toString()
//        val email = binding.email.text.toString()
//        val password = binding.password.text.toString()
        binding.singUpBtn.setOnClickListener {
            if(binding.name.text.toString().equals("") || binding.age.text.toString().equals("") || binding.email.text.toString().equals("")  || binding.password.text.toString().equals("")){
//            if(name.equals("") || age.text.toString().equals("") || email.equals("")  || password.equals("")){
                Toast.makeText(this, "Please fill all the details first", Toast.LENGTH_SHORT).show()
//                Log.e("authentication", "userCreationStatus: ${it.exception?.localizedMessage}")
            }else{
                auth.createUserWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
                    .addOnCompleteListener {
                        Log.i("authentication", "let's check: ${it.exception?.localizedMessage}, ${auth.currentUser?.uid}")
                    if(it.isSuccessful){
                        auth.currentUser!!.sendEmailVerification()
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    Log.e("authentication", "isSuccessful: ${it.exception?.localizedMessage}, ${auth.currentUser?.uid}")
                                    var user = AuthenticationUserModel(binding.name.text.toString(), binding.age.text.toString().toInt(), binding.email.text.toString(), binding.password.text.toString())
                                    db.child("AuthenticatedUserList").child(auth.currentUser!!.uid).setValue(user)
                                        .addOnSuccessListener {
//                                startActivity(Intent(this,HomePage::class.java))
//                            Toast.makeText(this,"dababase saved",Toast.LENGTH_SHORT).show()
//                            LoginException(it.)
                                            Log.e("authentication", "saving isSuccessful: , ${auth.currentUser?.uid}")
//                            val intentLogin = (Intent(this,HomePage::class.java))
//                            startActivity(intentLogin)
//                            finish()
                                            Toast.makeText(this, "User Registered Successfully. Please verify your email id through the link sent to your email.`", Toast.LENGTH_SHORT).show()
//                                startActivity(Intent(this, LogInPage::class.java))
                                            auth.signOut()
                                            val logInIntent = Intent(this, LogInPage::class.java)
                                            logInIntent.putExtra("fromSignUp",true)
                                            startActivity(logInIntent)
                                            finish()
////                                val logInIntent = Intent(this, HomePage::class.java)
//                                startActivity(logInIntent)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("authentication", "Database Error: ${e.message}")
                                            Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }

                                }else{
                                    Toast.makeText(this, "userVerification failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }else{
                        Toast.makeText(this,it.exception?.localizedMessage,Toast.LENGTH_LONG).show()
                        Log.e("authentication", "userCreationStatus: ${it.exception?.localizedMessage}, ${Firebase.auth.currentUser?.uid}")
                    }
                }

            }
//        binding.singUpBtn.setOnClickListener {
//            if(name.text.toString().equals("")|| age.text.toString().equals("") || email.text.toString().equals("")  || password.text.toString().equals("")){
////            if(name.equals("") || age.text.toString().equals("") || email.equals("")  || password.equals("")){
//                Toast.makeText(this, "Please fill all the details first", Toast.LENGTH_SHORT).show()
////                Log.e("authentication", "userCreationStatus: ${it.exception?.localizedMessage}")
//            }else{
//                Firebase.auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener {
//                        Log.e("authentication", "userCreationStatus: ${it.exception?.localizedMessage}, ${Firebase.auth.currentUser?.uid}")
////                    if(it.isSuccessful){
//                        var user = AuthenticationUserModel(name.text.toString(), age.text.toString().toInt(), email.text.toString(), password.text.toString())
//                        Firebase.database.reference.child("AuthenticatedUserList").child(Firebase.auth.currentUser!!.uid).setValue(user).addOnSuccessListener {
//                            startActivity(Intent(this,HomePage::class.java))
//                            finish()
//                        }
////                    }else{
////                        Toast.makeText(this,it.exception?.localizedMessage,Toast.LENGTH_LONG).show()
////                        Log.e("authentication", "userCreationStatus: ${it.exception?.localizedMessage}, ${Firebase.auth.currentUser?.uid}")
////                    }
//                }
//
//            }

        }
            binding.btnLogin.setOnClickListener {
                val intent = Intent(this, LogInPage::class.java)
                intent.putExtra("fromSignUp",true)
                startActivity(intent)
                finish()
            }
    }

//    override fun onStart() {
//        super.onStart()
//        if(Firebase.auth.currentUser != null){
//            startActivity(Intent(this,HomePage::class.java))
//            finish()
//        }
//    }
}


//        registration number - 12218153
//        name - Amartya Kaushik
//        section - RZ
//        courseCode - CSE205
//        scheduled CA date - 23rd Nov
//
//        Cocubes scheduled date - 23rd Nov



