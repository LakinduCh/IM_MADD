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
import com.example.triponai.data.remote.Hotel
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentHotelSuggestionsBinding
import com.example.triponai.ui.plan.adapters.HotelAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HotelSuggestionsFragment : Fragment() {

    private var _binding: FragmentHotelSuggestionsBinding? = null
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
        _binding = FragmentHotelSuggestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvHotels.layoutManager = LinearLayoutManager(context)

        viewModel.geminiResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                binding.rvHotels.adapter = HotelAdapter(
                    response.suggestedHotels,
                    onHotelSelected = { hotel -> viewModel.selectHotel(hotel) },
                    onRemoveClick = { hotel -> viewModel.removeHotel(hotel) }
                )
            }
        }

        binding.btnAddHotel.setOnClickListener {
            showAddHotelDialog()
        }

        binding.btnSeeAttractions.setOnClickListener {
            findNavController().navigate(R.id.action_hotelSuggestionsFragment_to_attractionRecommendationsFragment)
        }
    }

    private fun showAddHotelDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_hotel, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_hotel_name)
        val etLocation = dialogView.findViewById<EditText>(R.id.et_hotel_location)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Custom Hotel")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val location = etLocation.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.addHotel(Hotel(name, "User added hotel", "N/A", location = location))
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
