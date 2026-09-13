package com.example.triponai.ui.plan.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.triponai.data.remote.Attraction
import com.example.triponai.databinding.ItemAttractionBinding

class AttractionAdapter(
    private val attractions: List<Attraction>,
    private val onRemoveClick: (Attraction) -> Unit
) : RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder>() {

    class AttractionViewHolder(val binding: ItemAttractionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val binding = ItemAttractionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttractionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        holder.binding.tvAttractionName.text = attraction.name
        holder.binding.tvAttractionDesc.text = attraction.description
        holder.binding.tvAttractionLocation.text = attraction.location

        holder.binding.btnRemoveAttraction.setOnClickListener {
            onRemoveClick(attraction)
        }

        holder.binding.btnMapAttraction.setOnClickListener {
            val query = Uri.encode("${attraction.name}, ${attraction.location}, Sri Lanka")
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

    override fun getItemCount() = attractions.size
}
