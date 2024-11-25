package com.example.braingain

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.braingain.Fragments.Withdrawal
import com.example.braingain.ModelClass.AuthenticationUserModel
import com.example.braingain.ModelClass.QuestionModelClass
import com.example.braingain.databinding.ActivityQuizBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.firestore.firestore

private lateinit var binding : ActivityQuizBinding
// for storing the question while retrieving from the firbase firestore not database bujhee !!
private lateinit var questionList : ArrayList<QuestionModelClass>
    var currentQuestion = 0
    var spinChance : Long = 0L

class QuizActivity : AppCompatActivity() {

    private lateinit var db : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // catadp
        binding.categoryImg.setImageResource(intent.getIntExtra("categoryImg",0))


        // for BottomSheetDialog when i'll click on the withdraw coin option
        binding.withdrawalCoin.setOnClickListener{
            val bottomSHeetDialog : BottomSheetDialogFragment = Withdrawal() // name of the Withdrawal Fragment
            bottomSHeetDialog.show(this.supportFragmentManager, "TEST")
            bottomSHeetDialog.enterTransition
        }
        binding.withdrawalAmount.setOnClickListener{
            val bottomSHeetDialog : BottomSheetDialogFragment = Withdrawal() // name of the Withdrawal Fragment
            bottomSHeetDialog.show(this.supportFragmentManager, "TEST")
            bottomSHeetDialog.enterTransition
        }
        // for assigning values to the info field by retrieving data from the firebase store
        FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var userData = snapshot.getValue<AuthenticationUserModel>()
//                    userData?.let {
//                    binding.name.text = it.name
//                    binding.profilePassword.text = it.password

//                    }
                    binding.userName.text = userData?.name

                }

                override fun onCancelled(error: DatabaseError) {}

            }
        )

        // for retrieving data from firestore
        questionList = ArrayList<QuestionModelClass>()
        val questionAsked = binding.questionAsked
        val option1 = binding.option1
        val option2 = binding.option2
        val option3 = binding.option3
        val option4 = binding.option4

//        catadp
        val questionType = intent.getStringExtra("questionType")
        Log.i("question", "retrieved data type: $questionType")
        Firebase.firestore.collection("Questions")
            .document(questionType.toString())
            .collection("basicLevel")
            .get().addOnSuccessListener {questionData ->
                for(data in questionData.documents){
//                    questionList.clear()
                    var question : QuestionModelClass? = data.toObject(QuestionModelClass::class.java)
                    questionList.add(question!!)
                }
                    Log.i("question", "retrieved data is: ${questionList}")
                if(questionList.size > 0){
                    questionAsked.text = questionList.get(currentQuestion).question
                    option1.text = questionList.get(currentQuestion).option1
                    option2.text = questionList.get(currentQuestion).option2
                    option3.text = questionList.get(currentQuestion).option3
                    option4.text = questionList.get(currentQuestion).option4
                }
            }

        option1.setOnClickListener{
            nextQuestionAndUpdateAnswer(option1.text.toString())
        }
        option2.setOnClickListener{
            nextQuestionAndUpdateAnswer(option2.text.toString())
        }
        option3.setOnClickListener{
            nextQuestionAndUpdateAnswer(option3.text.toString())
        }
        option4.setOnClickListener{
            nextQuestionAndUpdateAnswer(option4.text.toString())
        }

        // to retrieve total coin from
        FirebaseDatabase.getInstance().reference.child("CoinsEarned").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var currentCoin = snapshot.getValue() as Long
                        binding.withdrawalAmount.text = currentCoin.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            }
        )

        // providing extra chance to spin every time the user wins
        FirebaseDatabase.getInstance().reference.child("PlayChance").child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        spinChance = (snapshot.value as Long)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )

        // fetching dp from database and assign to the profile picture
        FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val base64Image = snapshot.child("profilePicture").getValue(String::class.java)
                    base64Image?.let {
                        val bitmap = base64ToBitmap(it)
                        binding.dp.setImageBitmap(bitmap)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }

    fun base64ToBitmap(base64String: String) : Bitmap {
        val decodeByte = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodeByte, 0 , decodeByte.size)
    }
    private fun nextQuestionAndUpdateAnswer(option: String?) {
//        if(option.equals(questionList.get(currentQuestion).ans)){
//
//        }
        var score = 0
        if(option.equals(questionList.get(currentQuestion).ans)){
            score += 10;
        }
        currentQuestion++


        if(currentQuestion >= questionList.size ){
//            Toast.makeText(this, "greater size", Toast.LENGTH_SHORT).show()
            // the user has reached to the end of the question. so, now we'll score the user
            if(score > ((score / (questionList.size * 10)) * 100)){
                // we'll change the view to show winner view
                binding.wonAnim.visibility = View.VISIBLE
            Toast.makeText(this, "You won a chance to spin and earn! 🥳", Toast.LENGTH_SHORT).show()
                FirebaseDatabase.getInstance().reference.child("PlayChance").child(FirebaseAuth.getInstance().uid!!).setValue(
                    spinChance + 1)

                                                      // redirecting to the home page once the user won the quiz game
//            Handler(Looper.getMainLooper()).postDelayed({
//                Toast.makeText(this, "reached", Toast.LENGTH_SHORT).show()
//                binding.wonAnim.visibility = View.GONE
//                val transaction = supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.spin_containter, SpinFragment())
//                transaction.addToBackStack(null)
//                transaction.commit()
//            },3000)


            }else{
                // show sorry view
                binding.lostAnim.visibility = View.VISIBLE
            Toast.makeText(this, "Better Luck Next Time! 🫡", Toast.LENGTH_LONG).show()
            }
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, HomePage::class.java))
                    finish()
                },3000)
        }else{
            Log.i("question", "size: ${questionList.size} and ${currentQuestion}")

            val questionAsked = binding.questionAsked
            val option1 = binding.option1
            val option2 = binding.option2
            val option3 = binding.option3
            val option4 = binding.option4
            questionAsked.text = questionList.get(currentQuestion).question
            option1.text = questionList.get(currentQuestion).option1
            option2.text = questionList.get(currentQuestion).option2
            option3.text = questionList.get(currentQuestion).option3
            option4.text = questionList.get(currentQuestion).option4
        }
    }
}

//                Handler(Looper.getMainLooper()).postDelayed({
//                    val spinIntent = Intent(this, SpinFragment::class.java)
//                    startActivity(spinIntent)
//                    finish()
//                },3000)

// problem statemen intro objective how did you encounter the problem
