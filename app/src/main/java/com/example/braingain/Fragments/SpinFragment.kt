package com.example.braingain.Fragments

import android.os.Bundle
import android.os.CountDownTimer
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

    companion object {
    }
}