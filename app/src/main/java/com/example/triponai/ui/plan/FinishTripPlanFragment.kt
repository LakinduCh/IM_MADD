package com.example.triponai.ui.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentFinishTripPlanBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinishTripPlanFragment : Fragment() {

    private var _binding: FragmentFinishTripPlanBinding? = null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishTripPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Requirement: Clear generated AI result when returning to this screen
        viewModel.clearGeminiResponse()

        viewModel.tripPlanInput.observe(viewLifecycleOwner) { input ->
            binding.tvSummaryDestination.text = "Destination: ${input.destination}"
            binding.tvSummaryType.text = "Trip Type: ${input.tripType}"
            binding.tvSummaryGroup.text = "Group: ${input.travelGroup}"
            binding.tvSummaryDuration.text = "Duration: ${input.numberOfDays} Days"
            binding.tvSummaryTravelers.text = "Travelers: ${input.numberOfTravelers}"
            
            input.tripDate?.let { timestamp ->
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                binding.tvSummaryDate.text = "Date: ${sdf.format(Date(timestamp))}"
            } ?: run {
                binding.tvSummaryDate.text = "Date: Not selected"
            }
            
            val format = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
            binding.tvSummaryBudget.text = "Budget: ${format.format(input.budget)}"
        }

        binding.btnGenerate.setOnClickListener {
            findNavController().navigate(R.id.action_finishTripPlanFragment_to_aiGeneratingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
