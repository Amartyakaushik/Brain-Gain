package com.example.braingain.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.braingain.Authenticate.LogInPage
import com.example.braingain.ModelClass.AuthenticationUserModel
import com.example.braingain.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private var isExpanded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        // for providing arrow up and down icon for personal info
        binding.arrowIcon.setOnClickListener{
            if(isExpanded){
//                binding.arrowIcon.setImageResource(R.drawable.ic_arrow_down)
//                binding.arrowIcon.invalidate()
                binding.arrowIcon.rotation = 0f
                binding.layoutInfo.visibility = View.GONE
            }else{
//                binding.arrowIcon.setImageResource(R.drawable.ic_arrow_up)
//                binding.arrowIcon.invalidate()
                binding.arrowIcon.rotation = 180f
                binding.layoutInfo.visibility = View.VISIBLE
            }
            isExpanded = !isExpanded

        }

        // for assigning values to the info field by retrieving data from the firebase store
        FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var userData = snapshot.getValue<AuthenticationUserModel>()

//                    userData?.let {
//                    binding.name.text = it.name
//                    binding.profilePassword.text = it.password
//                    }
                    binding.name.text = userData?.name
                    binding.profileName.text = userData?.name
                    binding.profileEmail.text = userData?.email
                    binding.profilePassword.text = userData?.password
                    binding.profileAge.text = userData?.age.toString()
                    binding.profileCountry.text = "India"

                }

                override fun onCancelled(error: DatabaseError) {}

            }
        )
        binding.logOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LogInPage::class.java))
            // to prevent going back to profile fragment after successful log out
            requireActivity().finish()

            // or
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
        }

        return binding.root
    }

}