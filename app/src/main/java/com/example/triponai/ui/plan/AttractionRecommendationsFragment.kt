package com.example.triponai.ui.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.remote.Attraction
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentAttractionRecommendationsBinding
import com.example.triponai.ui.plan.adapters.AttractionAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AttractionRecommendationsFragment : Fragment() {

    private var _binding: FragmentAttractionRecommendationsBinding? = null
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
        _binding = FragmentAttractionRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAttractions.layoutManager = LinearLayoutManager(context)

        viewModel.geminiResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                binding.rvAttractions.adapter = AttractionAdapter(
                    response.suggestedAttractions,
                    onRemoveClick = { attraction -> viewModel.removeAttraction(attraction) }
                )
            }
        }

        binding.btnAddAttraction.setOnClickListener {
            showAddAttractionDialog()
        }

        binding.btnSeeBudget.setOnClickListener {
            findNavController().navigate(R.id.action_attractionRecommendationsFragment_to_budgetBreakdownFragment)
        }
    }

    private fun showAddAttractionDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_attraction, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_attraction_name)
        val etLocation = dialogView.findViewById<EditText>(R.id.et_attraction_location)
        val etDesc = dialogView.findViewById<EditText>(R.id.et_attraction_desc)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Custom Attraction")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val location = etLocation.text.toString().trim()
                val desc = etDesc.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.addAttraction(Attraction(name, desc, location))
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
