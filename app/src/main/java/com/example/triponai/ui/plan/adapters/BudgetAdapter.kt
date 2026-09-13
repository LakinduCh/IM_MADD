package com.example.triponai.ui.plan.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.triponai.data.remote.BudgetItem
import com.example.triponai.databinding.ItemBudgetBinding

class BudgetAdapter(
    private var items: List<BudgetItem>,
    private val onAmountChanged: (String, Double) -> Unit,
    private val onRemoveClick: (BudgetItem) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(val binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvBudgetName.text = item.name
        
        // Remove existing listener before setting text
        holder.binding.etBudgetAmount.tag = null
        holder.binding.etBudgetAmount.setText(item.amount.toString())
        holder.binding.etBudgetAmount.tag = item.name

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (holder.binding.etBudgetAmount.tag == item.name) {
                    val newAmount = s.toString().toDoubleOrNull() ?: 0.0
                    onAmountChanged(item.name, newAmount)
                }
            }
        }
        
        holder.binding.etBudgetAmount.addTextChangedListener(textWatcher)
        holder.binding.etBudgetAmount.tag = item.name

        holder.binding.btnRemoveBudget.setOnClickListener {
            onRemoveClick(item)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<BudgetItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
