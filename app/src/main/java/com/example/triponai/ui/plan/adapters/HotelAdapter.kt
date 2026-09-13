package com.example.triponai.ui.plan.adapters

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.triponai.R
import com.example.triponai.data.remote.Hotel
import com.example.triponai.databinding.ItemHotelBinding

class HotelAdapter(
    private val hotels: List<Hotel>,
    private val onHotelSelected: (Hotel) -> Unit,
    private val onRemoveClick: (Hotel) -> Unit
) : RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    class HotelViewHolder(val binding: ItemHotelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val binding = ItemHotelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HotelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hotels[position]
        holder.binding.tvHotelName.text = hotel.name
        holder.binding.tvHotelDesc.text = hotel.description
        holder.binding.tvHotelRating.text = hotel.starRating
        holder.binding.tvHotelPrice.visibility = View.GONE

        // Selection state UI
        holder.binding.checkboxSelectHotel.setOnCheckedChangeListener(null)
        holder.binding.checkboxSelectHotel.isChecked = hotel.isSelected
        
        if (hotel.isSelected) {
            holder.binding.cardHotel.strokeColor = holder.itemView.context.getColor(R.color.tripon_green)
        } else {
            holder.binding.cardHotel.strokeColor = Color.TRANSPARENT
        }

        holder.binding.checkboxSelectHotel.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onHotelSelected(hotel)
            }
        }

        holder.binding.btnRemoveHotel.setOnClickListener {
            onRemoveClick(hotel)
        }

        holder.binding.btnMapHotel.setOnClickListener {
            val query = Uri.encode("${hotel.name}, Sri Lanka")
            val uri = Uri.parse("geo:0,0?q=$query")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            
            if (mapIntent.resolveActivity(holder.itemView.context.packageManager) != null) {
                holder.itemView.context.startActivity(mapIntent)
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$query"))
                holder.itemView.context.startActivity(browserIntent)
            }
        }
    }

    override fun getItemCount() = hotels.size
}
