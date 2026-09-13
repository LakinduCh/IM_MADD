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
import com.example.triponai.databinding.FragmentAiGeneratingBinding

class AIGeneratingFragment : Fragment() {

    private var _binding: FragmentAiGeneratingBinding? = null
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
        _binding = FragmentAiGeneratingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        
        // Start generation if not already loading
        if (viewModel.isLoading.value != true && viewModel.geminiResponse.value == null) {
            viewModel.generateItinerary()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.generateItinerary()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.tvLoadingStatus.text = if (isLoading) "Generating your personalized itinerary..." else "Generation Paused"
            binding.btnRetry.visibility = if (!isLoading && viewModel.error.value != null) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.tvLoadingStatus.text = "Generation Error"
                binding.tvLoadingSubtitle.text = error
                binding.progressIndicator.visibility = View.GONE
                binding.btnRetry.visibility = View.VISIBLE
            }
        }

        viewModel.geminiResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                findNavController().navigate(R.id.action_aiGeneratingFragment_to_aiTripPlanFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
