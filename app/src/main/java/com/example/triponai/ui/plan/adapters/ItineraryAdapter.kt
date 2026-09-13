package com.example.triponai.ui.plan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.triponai.data.remote.ActivityItem
import com.example.triponai.data.remote.DayItinerary
import com.example.triponai.databinding.ItemDayItineraryBinding

class ItineraryAdapter(
    private val days: List<DayItinerary>,
    private val onAddActivity: (Int) -> Unit,
    private val onRemoveActivity: (Int, ActivityItem) -> Unit
) : RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>() {

    class ItineraryViewHolder(val binding: ItemDayItineraryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryViewHolder {
        val binding = ItemDayItineraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItineraryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItineraryViewHolder, position: Int) {
        val day = days[position]
        holder.binding.tvDayNumber.text = "Day ${day.dayNumber}"
        holder.binding.tvDayTitle.text = day.dayTitle
        
        holder.binding.rvActivities.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ActivityAdapter(day.activities) { activity ->
                onRemoveActivity(day.dayNumber, activity)
            }
        }

        holder.binding.btnAddActivity.setOnClickListener {
            onAddActivity(day.dayNumber)
        }
    }

    override fun getItemCount() = days.size
}
