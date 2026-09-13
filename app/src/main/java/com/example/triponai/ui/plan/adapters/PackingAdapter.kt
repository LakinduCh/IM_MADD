package com.example.triponai.ui.plan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.triponai.data.remote.PackingItem
import com.example.triponai.databinding.ItemPackingBinding

class PackingAdapter(
    private val items: List<PackingItem>,
    private val onRemoveClick: (PackingItem) -> Unit
) : RecyclerView.Adapter<PackingAdapter.PackingViewHolder>() {

    class PackingViewHolder(val binding: ItemPackingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackingViewHolder {
        val binding = ItemPackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PackingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackingViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvPackingItem.text = item.name
        
        holder.binding.checkboxPacking.setOnCheckedChangeListener(null)
        holder.binding.checkboxPacking.isChecked = item.isChecked
        
        holder.binding.checkboxPacking.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
        }

        holder.binding.btnRemovePacking.setOnClickListener {
            onRemoveClick(item)
        }
    }

    override fun getItemCount() = items.size
}
