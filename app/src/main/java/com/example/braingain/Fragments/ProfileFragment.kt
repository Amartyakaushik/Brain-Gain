package com.example.braingain.Fragments

//import com.google.firebase.auth.RA
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.braingain.Authenticate.LogInPage
import com.example.braingain.ModelClass.AuthenticationUserModel
import com.example.braingain.R
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
    private val db = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding.dp.setImageResource(avatar)
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

        binding.dp.setOnClickListener{
            showAvatarSelectionDialog()
        }


        return binding.root
    }
        fun updateProfilePicture(avt: Int) {
            binding.dp.setImageResource(avt)
            FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("profilePicture").setValue(avt)
        }

        // changing the dp and update it in the database
        fun showAvatarSelectionDialog(){
            val avatars = arrayOf(
                R.drawable.dp,
                R.drawable.avt_boy,
                R.drawable.avt_girl,
                R.drawable.avt_boy,
                R.drawable.dp
            )
        val avtDrawables = avatars.map { resources.getDrawable(it, null) }
            val avtName = arrayOf("avt1","avt2","avt3","avt4","avt5")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your Avatar")
                .setItems(avtName){_, which ->
                    updateProfilePicture(avatars[which])
                }
                .show()
        }




//    override fun onResume() {
//        super.onResume()
//        filteredCategoryList.clear()
//        filteredCategoryList.addAll(categoryList)
//        adapter.notifyDataSetChanged()
//    }
}