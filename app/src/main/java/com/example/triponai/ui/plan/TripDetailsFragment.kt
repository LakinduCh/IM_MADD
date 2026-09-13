package com.example.triponai.ui.plan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentTripDetailsBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripDetailsFragment : Fragment() {

    private var _binding: FragmentTripDetailsBinding? = null
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

    private var selectedDateTimestamp: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnReview.setOnClickListener {
            validateAndContinue()
        }

        setupBudgetWarningLogic()
    }

    private fun showDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Trip Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDateTimestamp = selection
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            binding.etDate.setText(sdf.format(Date(selection)))
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun setupBudgetWarningLogic() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateBudgetWarning()
            }
        }

        binding.etDays.addTextChangedListener(textWatcher)
        binding.etTravelers.addTextChangedListener(textWatcher)
        binding.etBudget.addTextChangedListener(textWatcher)
    }

    private fun calculateBudgetWarning() {
        val days = binding.etDays.text.toString().toIntOrNull() ?: 0
        val travelers = binding.etTravelers.text.toString().toIntOrNull() ?: 0
        val budget = binding.etBudget.text.toString().toDoubleOrNull() ?: 0.0

        if (days > 0 && travelers > 0 && budget > 0) {
            // Heuristic: Min ~5000 LKR per person per day for a basic local trip
            val minReasonable = days * travelers * 5000.0
            val flexBudget = days * travelers * 15000.0

            when {
                budget < minReasonable -> {
                    binding.tvBudgetWarning.visibility = View.VISIBLE
                    binding.tvBudgetWarning.text = "Your budget may be too low for this trip. Consider increasing it for a better experience."
                    binding.tvBudgetWarning.setTextColor(resources.getColor(R.color.tripon_green, null)) // Or a warning color if available
                }
                budget > flexBudget -> {
                    binding.tvBudgetWarning.visibility = View.VISIBLE
                    binding.tvBudgetWarning.text = "Your budget gives you more flexibility for luxury accommodation and premium activities."
                    binding.tvBudgetWarning.setTextColor(resources.getColor(R.color.tripon_green, null))
                }
                else -> {
                    binding.tvBudgetWarning.visibility = View.GONE
                }
            }
        } else {
            binding.tvBudgetWarning.visibility = View.GONE
        }
    }

    private fun validateAndContinue() {
        val destination = binding.etDestination.text.toString().trim()
        val daysStr = binding.etDays.text.toString().trim()
        val travelersStr = binding.etTravelers.text.toString().trim()
        val budgetStr = binding.etBudget.text.toString().trim()

        if (destination.isEmpty()) {
            binding.destinationInputLayout.error = "Please enter a destination"
            return
        }
        
        if (selectedDateTimestamp == null) {
            binding.dateInputLayout.error = "Please select a date"
            return
        } else {
            binding.dateInputLayout.error = null
        }

        val days = daysStr.toIntOrNull() ?: 0
        if (days <= 0) {
            binding.daysInputLayout.error = "Invalid"
            return
        }

        val travelers = travelersStr.toIntOrNull() ?: 0
        if (travelers <= 0) {
            binding.travelersInputLayout.error = "Invalid"
            return
        }

        val budget = budgetStr.toDoubleOrNull() ?: 0.0
        if (budget <= 0) {
            binding.budgetInputLayout.error = "Invalid"
            return
        }

        viewModel.updateTripDetails(destination, days, travelers, budget, selectedDateTimestamp)
        findNavController().navigate(R.id.action_tripDetailsFragment_to_finishTripPlanFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
