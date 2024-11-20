package com.example.braingain.Fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.braingain.Adapter.categoryAdapter
import com.example.braingain.ModelClass.AuthenticationUserModel
import com.example.braingain.ModelClass.cateogoryModelClass
import com.example.braingain.R
import com.example.braingain.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var adapter : categoryAdapter
    private var categoryList = ArrayList<cateogoryModelClass>()
    private var filteredCategoryList = ArrayList<cateogoryModelClass>()
    private lateinit var filteredAdapter : categoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)



        categoryList.add(cateogoryModelClass(R.drawable.maths,"science"))
        categoryList.add(cateogoryModelClass(R.drawable.english,"english"))
        categoryList.add(cateogoryModelClass(R.drawable.history,"history"))
        categoryList.add(cateogoryModelClass(R.drawable.law,"law"))
        categoryList.add(cateogoryModelClass(R.drawable.android,"android"))
        categoryList.add(cateogoryModelClass(R.drawable.dsa,"dataStructure"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // for BottomSheetDialog when i'll click on the withdraw coin option
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
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.categoryRv.layoutManager = GridLayoutManager(requireContext(),2)
        adapter = categoryAdapter(categoryList, requireActivity()){category ->
            handleCategoryClick(category)
        }
        binding.categoryRv.adapter = adapter
        binding.categoryRv.setHasFixedSize(true)


        // to filter the category user will search for
        // setUp filtered category Recycler View
        binding.filteredCategoryRv.layoutManager = GridLayoutManager(requireContext(), 1)
        filteredAdapter = categoryAdapter(filteredCategoryList, requireActivity()){category ->
            handleCategoryClick(category)
        }
        binding.filteredCategoryRv.adapter = filteredAdapter

        // Handle Search
        binding.category.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val query = s.toString().lowercase()
                // check if the query matches any of the category
                if(query.isNotEmpty()){
                    filteredCategoryList.clear()
                    filteredCategoryList.addAll(categoryList.filter {
                        it.catText.lowercase().contains(query)
                    })

                    filteredAdapter.notifyDataSetChanged()
                    binding.filteredCategoryRv.visibility = View.VISIBLE
                    binding.categoryRv.visibility = View.GONE
                }else{
                    binding.filteredCategoryRv.visibility = View.GONE
                    binding.categoryRv.visibility = View.VISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}


        })
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
                    if(binding.filteredCategoryRv.visibility == View.VISIBLE){
                        resetToOriginalList(adapter)
                    }else{
//                        isEnable = false
                        requireActivity().onBackPressed()
                    }
                }


        Log.e("authentication", "HomePage User Status:  ${Firebase.auth.currentUser?.uid}")
    }

    private fun resetToOriginalList(adapter: categoryAdapter) {
        binding.category.text.clear()
        filteredCategoryList.clear()
        binding.filteredCategoryRv.visibility = View.GONE
        binding.categoryRv.visibility = View.VISIBLE

        adapter.notifyDataSetChanged()
    }


    private fun handleCategoryClick(category: cateogoryModelClass) {
        Toast.makeText(requireContext(), "Clicked on ${category.catText}", Toast.LENGTH_SHORT).show()
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