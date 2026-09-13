package com.example.triponai.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.triponai.data.entity.TripEntity
import com.example.triponai.data.remote.GeminiTripResponse
import com.example.triponai.databinding.ItemTripBinding
import com.google.gson.Gson
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripAdapter(
    private val trips: List<TripEntity>,
    private val onTripClick: (TripEntity) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.binding.tvTripDestination.text = trip.destination
        
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.binding.tvTripDate.text = sdf.format(Date(trip.startDate))
        
        holder.binding.tvTripType.text = trip.tripType
        
        val duration = ((trip.endDate - trip.startDate) / (24 * 60 * 60 * 1000)).toInt()
        holder.binding.tvTripDuration.text = "${if (duration > 0) duration else 1} Days"

        // Budget Summary
        val format = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
        holder.binding.tvTripPlannedBudget.text = format.format(trip.budget)
        
        var estimatedCost = "N/A"
        if (trip.aiResponseJson != null) {
            try {
                val response = Gson().fromJson(trip.aiResponseJson, GeminiTripResponse::class.java)
                estimatedCost = format.format(response.budgetBreakdown.totalEstimatedCost)
            } catch (e: Exception) {}
        }
        holder.binding.tvTripEstimatedCost.text = estimatedCost

        holder.itemView.setOnClickListener {
            onTripClick(trip)
        }
    }

    override fun getItemCount() = trips.size
}
