package com.example.triponai.ui.plan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.triponai.data.remote.ActivityItem
import com.example.triponai.databinding.ItemActivityBinding

class ActivityAdapter(
    private val activities: List<ActivityItem>,
    private val onRemoveClick: (ActivityItem) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val item = activities[position]
        holder.binding.tvActivityTime.text = item.time
        holder.binding.tvActivityName.text = item.activity
        holder.binding.tvActivityDesc.text = item.description
        
        holder.binding.viewLine.visibility = if (position == activities.size - 1) View.INVISIBLE else View.VISIBLE
        
        holder.binding.btnRemoveActivity.setOnClickListener {
            onRemoveClick(item)
        }
    }

    override fun getItemCount() = activities.size
}
