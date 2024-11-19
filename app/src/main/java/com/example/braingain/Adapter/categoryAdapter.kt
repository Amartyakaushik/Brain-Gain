package com.example.braingain.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.braingain.Adapter.categoryAdapter.MyCategoryViewHolder
import com.example.braingain.ModelClass.cateogoryModelClass
import com.example.braingain.QuizActivity
import com.example.braingain.databinding.CategoryItemBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class categoryAdapter(
    var categoryList: ArrayList<cateogoryModelClass>,
    var requireActivity: FragmentActivity,
    private val onClick : (cateogoryModelClass) -> Unit   // for handling clicks on categories
) : RecyclerView.Adapter<MyCategoryViewHolder>() {
    class MyCategoryViewHolder(var binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryViewHolder {
        return MyCategoryViewHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun getItemCount()= categoryList.size

    override fun onBindViewHolder(holder: MyCategoryViewHolder, position: Int) {
        var dataList = categoryList[position]
        holder.binding.categoryImage.setImageResource(dataList.catImage)
        holder.binding.categoryText.text = dataList.catText
        holder.binding.categoryBtn.setOnClickListener{

            Log.e("authentication", "userCreationStatus: ${Firebase.auth.currentUser?.uid}")
            var intent = Intent(requireActivity, QuizActivity::class.java)
            intent.putExtra("categoryImg", dataList.catImage)
            intent.putExtra("questionType",dataList.catText)
//            requireActivity.startActivity(intent)
            holder.itemView.setOnClickListener{
                onClick(dataList)
            requireActivity.startActivity(intent)
            }

        }

        // for handling clicks on categories
//        holder.itemView.setOnClickListener{
//            onClick(dataList)
//        }

    }
}