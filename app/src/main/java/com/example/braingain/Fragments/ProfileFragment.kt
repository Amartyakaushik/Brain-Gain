package com.example.braingain.Fragments

//import com.google.firebase.auth.RA
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private var isExpanded = false
    private val db = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding.dp.setImageResource(avatar)
        // update the dp (fetch from firebase and assign to dp) each time view is created
//        FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList")
//            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profilePicture").addValueEventListener(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val avatar = snapshot.getValue(Int::class.java)
//                    avatar?.let {
////                        binding.dp.setImageResource(it)
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {}
//
//            })
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

        // updatation of the profile picture
        loadUserProfile()
        binding.dp.setOnClickListener{
//            showAvatarSelectionDialog()
            openImagePicker()
        }


        return binding.root
    }
        fun updateProfilePicture(avt: Int) {
            binding.dp.setImageResource(avt)
            FirebaseDatabase.getInstance().reference.child("AuthenticatedUserList").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("profilePicture").setValue(avt)
        }
        // changing the dp (avatar) and update it in the database
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

//    private fun PICK_IMAGE_REQUEST = 1
//    private fun imageBitmap : Bitmap? = null
    private fun openImagePicker(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST &&  resultCode == RESULT_OK && data != null && data.data != null){
            try {
                val imgUri = data.data
                val inputStream = requireContext().contentResolver.openInputStream(imgUri!!)
                val imageBitmap = BitmapFactory.decodeStream(inputStream)

                // to handle large image files
                val compressedBitmap = compressBitmap(imageBitmap)
                saveImageToDatabase(imageBitmap)
//            binding.dp.setImageBitmap(imageBitmap)
                binding.dp.setImageBitmap(compressedBitmap)
                
            }catch (e: Exception) {
                Log.e("dpUpload", "Error while uploading image: ${e.message}")
            }
        }
    }

    private fun compressBitmap(imageBitmap: Bitmap): Bitmap? {
        return Bitmap.createScaledBitmap(imageBitmap, 300, 300, true)
    }

    private fun saveImageToDatabase(imageBitmap: Bitmap?) {
        val base64Image = bitmapToBase64(imageBitmap)
        db.child("AuthenticatedUserList")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("profilePicture")
            .setValue(base64Image)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(requireContext(), "Dp changed", Toast.LENGTH_SHORT).show()
                    Log.d("dpUpload", "Image uploaded successfully")
                }else{
                    Toast.makeText(requireContext(), "DP error", Toast.LENGTH_SHORT).show()
                    Log.e("dpUpload", "Failed to upload image")
                }
            }
    }

    private fun bitmapToBase64(imageBitmap: Bitmap?): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }



    // retrieve bitMap from the database and assign to dp each time the view is created
    private fun loadUserProfile() {
        db.child("AuthenticatedUserList")
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
    //    override fun onResume() {
//        super.onResume()
//        filteredCategoryList.clear()
//        filteredCategoryList.addAll(categoryList)
//        adapter.notifyDataSetChanged()
//    }
companion object {
    private const val PICK_IMAGE_REQUEST = 1
}
}

