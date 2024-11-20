package com.example.braingain.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.braingain.Adapter.historyAdapter
import com.example.braingain.ModelClass.AuthenticationUserModel
import com.example.braingain.ModelClass.historyModelClass
import com.example.braingain.databinding.FragmentHistoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.util.Collections

class HistoryFragment : Fragment() {
    private lateinit var binding : FragmentHistoryBinding
    private var historyDataList = ArrayList<historyModelClass>()
    private lateinit var adapter: historyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
//        historyDataList.add(historyModelClass("12:32","$100"))
//        historyDataList.add(historyModelClass("08:42","$900"))
//        historyDataList.add(historyModelClass("10:53","$300"))
//        historyDataList.add(historyModelClass("01:45","$6100"))
//        historyDataList.add(historyModelClass("02:12","$1300"))
//        historyDataList.add(historyModelClass("12:32","$100"))
        FirebaseDatabase.getInstance().reference.child("PlayerAccountHistory").child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                            var historyDataList1 = ArrayList<historyModelClass>()
                        for(data in snapshot.children){
                            val data = data.getValue(historyModelClass::class.java)

                            historyDataList1.add(data!!)
                        }
                        Collections.reverse(historyDataList1)
                        historyDataList.addAll(historyDataList1)
                            adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
            )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        adapter = historyAdapter(historyDataList)
        val historyRV = binding.historyRv
        historyRV.layoutManager = LinearLayoutManager(requireContext())
        historyRV.adapter = adapter
        historyRV.setHasFixedSize(true)


        // For withdrawing money UI we use  BottomSheetDialogFragment through Withdrawal Fragment
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
                    val userData = snapshot.getValue<AuthenticationUserModel>()
//                    userData?.let {
//                    binding.name.text = it.name
//                    binding.profilePassword.text = it.password
//                    }
                    binding.userName.text = userData?.name
                }
                override fun onCancelled(error: DatabaseError) {}
            }
        )

        // to retrieve total coin from
        FirebaseDatabase.getInstance().reference.child("CoinsEarned").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val currentCoin = snapshot.getValue() as Long
                        binding.withdrawalAmount.text = currentCoin.toString()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}

            }
        )

        // update the dp (fetch from firebase and assign to dp) each time view is created
        FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profilePicture").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val avatar = snapshot.getValue(Int::class.java)
                    avatar?.let {
                        binding.dp.setImageResource(it)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}

            })
        return binding.root
    }

}