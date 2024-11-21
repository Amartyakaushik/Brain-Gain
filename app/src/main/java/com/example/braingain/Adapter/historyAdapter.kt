package com.example.braingain.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.braingain.ModelClass.historyModelClass
import com.example.braingain.R
import com.example.braingain.databinding.HistoryItemListBinding
import java.sql.Date
import java.sql.Timestamp

class historyAdapter(var historyList : ArrayList<historyModelClass>) :
    RecyclerView.Adapter<historyAdapter.MyHistoryViewHolder>() {
    class MyHistoryViewHolder(var binding: HistoryItemListBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHistoryViewHolder {
        return MyHistoryViewHolder(HistoryItemListBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun getItemCount() = historyList.size

    override fun onBindViewHolder(holder: MyHistoryViewHolder, position: Int) {
//        holder.binding.time.text = historyList[position].timeAndDate
        // for showing exact when the amount is credited
        var timeStamp = Timestamp(historyList.get(position).timeAndDate.toLong())
        holder.binding.time.text = Date(timeStamp.time).toString()

        // to inform if the money is debited or credited
        if(historyList.get(position).isWithdrawal) {
            holder.binding.status.text = "Debited"
//            holder.binding.status.setTextColor(Color.RED)
        holder.binding.coinCount.text = "- " + historyList[position].coin
            holder.binding.coinCount.setTextColor(Color.RED)
            holder.binding.withdrawalStatus.setImageResource(R.drawable.ic_debit)

        }else{
            holder.binding.status.text = "Credited"
//            holder.binding.status.setTextColor(Color.GREEN)
        holder.binding.coinCount.text = "-" + historyList[position].coin
            holder.binding.coinCount.setTextColor(Color.GREEN)
            holder.binding.withdrawalStatus.setImageResource(R.drawable.ic_credit)
        }

    }
}