package com.example.braingain.Fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.braingain.ModelClass.AuthenticationUserModel
import com.example.braingain.ModelClass.historyModelClass
import com.example.braingain.databinding.FragmentSpinBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.util.Random
//import com.example.braingain.R
//import com.example.braingain.databinding.FragmentHomeBinding

class SpinFragment : Fragment() {
    private lateinit var binding: FragmentSpinBinding
    private lateinit var timer : CountDownTimer
    private val itemTitles = arrayOf("100","Nothing !! LOL 😄","1200","Better luck next time","500","Better luck next time","200","Better luck next time")
    private var playChance = 0L
    var currentCoin = 0L
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSpinBinding.inflate(inflater, container, false)
        binding.withdrawalCoin.setOnClickListener{
            val bottomSHeetDialog : BottomSheetDialogFragment = Withdrawal() // name of the Withdrawal Fragment
            bottomSHeetDialog.show(requireActivity().supportFragmentManager, "TEST")
            bottomSHeetDialog.enterTransition
        }
        binding.withdrawalAmount.setOnClickListener{
            val bottomSHeetDialog : BottomSheetDialogFragment = Withdrawal() // name of the Withdrawal Fragment
            bottomSHeetDialog.show(requireActivity().supportFragmentManager, "TEST")
            bottomSHeetDialog.enterTransition
        }

        // for assigning values to the info field by retrieving data from the firebase store
        FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var userData = snapshot.getValue<AuthenticationUserModel>()
//                    userData?.let {
//
//                    binding.name.text = it.name
//                    binding.profilePassword.text = it.password

//                    }
                    binding.userName.text = userData?.name

                }

                override fun onCancelled(error: DatabaseError) {}

            }
        )

        // providing extra chance to spin every time the user wins
        FirebaseDatabase.getInstance().reference.child("PlayChance").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        playChance = snapshot.value as Long
                        binding.chanceCount.text = (snapshot.value as Long).toString()
                    }else{
                        val temp = 0
                        binding.chanceCount.text = temp.toString()
                        binding.spinBtn.isEnabled = false
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            }
        )

        // to retrieve total coin from
        FirebaseDatabase.getInstance().reference.child("CoinsEarned").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        currentCoin = snapshot.getValue() as Long
                        binding.withdrawalAmount.text = currentCoin.toString()
                    }
                    }

                override fun onCancelled(error: DatabaseError) {}

            }
        )

        // 20Nov
        // update the dp (fetch from firebase and assign to dp) each time view is created
//        FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList")
//            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profilePicture").addValueEventListener(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val avatar = snapshot.getValue(Int::class.java)
//                    avatar?.let {
//                        binding.dp.setImageResource(it)
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {}
//
//            })

        loadUserProfile()
            return binding.root
    }





    private fun showResult(itemTitle : String, spin : Int){
        if(spin % 2 == 0){
            var winCoin = itemTitle.toInt()
            FirebaseDatabase.getInstance().reference.child("CoinsEarned").child(FirebaseAuth.getInstance().uid!!).setValue(winCoin + currentCoin)
            binding.withdrawalAmount.text = (winCoin + currentCoin).toString()

            // to store the data in the history that if money is credited or debited
            var historyList = historyModelClass(System.currentTimeMillis().toString(), itemTitle, false)
            FirebaseDatabase.getInstance().reference.child("PlayerAccountHistory").child(FirebaseAuth.getInstance().uid!!)
                .push()
                .setValue(historyList)
        }

        Toast.makeText(requireContext(), itemTitle, Toast.LENGTH_SHORT).show()
        // decrease value of chances every time user spin the wheeler
        FirebaseDatabase.getInstance().reference.child("PlayChance").child(FirebaseAuth.getInstance().uid!!).setValue(playChance - 1)

        binding.spinBtn.isEnabled = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            binding.spinBtn.setOnClickListener {
//            Toast.makeText(requireContext(), "btn clicked", Toast.LENGTH_SHORT).show()
        if(playChance > 0){
            binding.spinBtn.isEnabled = false
            val spin = Random().nextInt(8)
            Log.i("randomNumber", "random Number: $spin")
            val degrees = 45f * spin
            Log.i("randomNumber", "degree: $degrees")
            timer = object : CountDownTimer(10000, 25){
                var rotation  = 0f

                override fun onTick(millisUntilFinished: Long) {
                    rotation += 5f
            Log.i("randomNumber", "rotation value: $rotation")
                    if(rotation >= degrees){
                        rotation = degrees
                        timer.cancel()
                        showResult(itemTitles[spin], spin)
                    }
                    binding.spinnerWheel.rotation = rotation
                }

                override fun onFinish() {
                }
            }.start()
        }else{
            Toast.makeText(requireContext(), "Out of spin chance", Toast.LENGTH_SHORT).show()
        }
        }
    }

    companion object {}



    ////// ############################ Bitmap from firebase and assign to profile picture
// retrieve bitMap from the database and assign to dp each time the view is created
    private fun loadUserProfile() {
        FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
//                        val userData = snapshot.getValue(AuthenticationUserModel::class.java)
//                        binding.name.text = userData?.name
//                        binding.profileName.text = userData?.name
//                        binding.profileEmail.text = userData?.email
//                        binding.profilePassword.text = userData?.password
//                        binding.profileAge.text = userData?.age.toString()
//                        binding.profileCountry.text = "India"

                        // Load and decode profile picture
                        val base64Image = snapshot.child("profilePicture").getValue(String::class.java)
                        base64Image?.let {
                            val bitmap = base64ToBitmap(it)
                            binding.dp.setImageBitmap(bitmap)
                        }
                    } catch (e: Exception) {
                        Log.e("dpChanged", "Error loading user data: ${e.message}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("dpChanged", "Failed to load profile: ${error.message}")
                }
            })
    }

    // Convert Base64 string to Bitmap
    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}