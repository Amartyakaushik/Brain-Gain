package com.example.braingain.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.braingain.ModelClass.historyModelClass
import com.example.braingain.databinding.FragmentWithdrawalBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Withdrawal : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentWithdrawalBinding
    var totalCoin = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWithdrawalBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_withdrawal, container, false)

        // to retrieve total coin from apne database
        FirebaseDatabase.getInstance().reference.child("CoinsEarned").child(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        totalCoin = (snapshot.getValue() as Long)
//                        binding.withdrawalAmount.text = currentCoin.toString()
                        binding.totalCoin.text = totalCoin.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })


        binding.transferBtn.setOnClickListener {
            var withdrawMoney = binding.amount.text.toString()
            if(withdrawMoney.toDouble() <= totalCoin){
                FirebaseDatabase.getInstance().reference.child("CoinsEarned")
                    .child(FirebaseAuth.getInstance().uid!!)
                    .setValue(totalCoin - withdrawMoney.toDouble())

                // update history
                var historyList = historyModelClass(System.currentTimeMillis().toString(), withdrawMoney, true)
                FirebaseDatabase.getInstance().reference.child("PlayerAccountHistory").child(FirebaseAuth.getInstance().uid!!)
                    .push()
                    .setValue(historyList)
                Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Not enough Money", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root

    }

}