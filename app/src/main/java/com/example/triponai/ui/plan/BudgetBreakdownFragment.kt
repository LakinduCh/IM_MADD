package com.example.triponai.ui.plan

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.remote.BudgetItem
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentBudgetBreakdownBinding
import com.example.triponai.ui.plan.adapters.BudgetAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat
import java.util.Locale

class BudgetBreakdownFragment : Fragment() {

    private var _binding: FragmentBudgetBreakdownBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripPlanViewModel by activityViewModels {
        val db = AppDatabase.getDatabase(requireContext())
        TripPlanViewModelFactory(TripRepository(
            db.tripDao(),
            db.itineraryDao(),
            db.favoriteDao(),
            db.historyDao()
        ))
    }

    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBreakdownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.btnAddBudgetCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.btnSeePacking.setOnClickListener {
            findNavController().navigate(R.id.action_budgetBreakdownFragment_to_packingChecklistFragment)
        }
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter(
            emptyList(),
            onAmountChanged = { name, amount ->
                viewModel.updateBudgetItem(name, amount)
            },
            onRemoveClick = { item ->
                viewModel.removeBudgetItem(item.name)
            }
        )
        binding.rvBudgetItems.layoutManager = LinearLayoutManager(context)
        binding.rvBudgetItems.adapter = budgetAdapter
    }

    private fun observeViewModel() {
        viewModel.tripPlanInput.observe(viewLifecycleOwner) { input ->
            val format = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
            binding.tvUserBudget.text = "Planned: ${format.format(input.budget)}"
            updateBudgetStatus()
        }

        viewModel.geminiResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                budgetAdapter.updateItems(response.budgetBreakdown.items)
                val format = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
                binding.tvTotalEstimate.text = format.format(response.budgetBreakdown.totalEstimatedCost)
                updateBudgetStatus()
            }
        }
    }

    private fun updateBudgetStatus() {
        val input = viewModel.tripPlanInput.value ?: return
        val response = viewModel.geminiResponse.value ?: return
        
        val planned = input.budget
        val estimated = response.budgetBreakdown.totalEstimatedCost
        
        if (estimated > planned) {
            binding.tvBudgetWarning.text = "Your estimated trip cost is above your planned budget."
            binding.tvBudgetWarning.setTextColor(resources.getColor(R.color.tripon_green, null))
        } else {
            binding.tvBudgetWarning.text = "Your estimated trip cost is within your planned budget."
            binding.tvBudgetWarning.setTextColor(resources.getColor(R.color.tripon_green, null))
        }
    }

    private fun showAddCategoryDialog() {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(64, 20, 64, 20)

        val etName = EditText(requireContext())
        etName.hint = "Category Name"
        layout.addView(etName)

        val etAmount = EditText(requireContext())
        etAmount.hint = "Amount (LKR)"
        etAmount.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(etAmount)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Budget Category")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                if (name.isNotEmpty()) {
                    viewModel.addBudgetItem(BudgetItem(name, amount))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
