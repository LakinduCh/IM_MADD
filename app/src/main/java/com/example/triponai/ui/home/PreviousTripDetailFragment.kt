package com.example.triponai.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.remote.GeminiTripResponse
import com.example.triponai.data.remote.PackingItem
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentPreviousTripDetailBinding
import com.example.triponai.ui.plan.adapters.AttractionAdapter
import com.example.triponai.ui.plan.adapters.ItineraryAdapter
import com.example.triponai.ui.plan.adapters.PackingAdapter
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class PreviousTripDetailFragment : Fragment() {

    private var _binding: FragmentPreviousTripDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        HomeViewModelFactory(TripRepository(
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
        _binding = FragmentPreviousTripDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tripId = arguments?.getLong("tripId") ?: return
        
        binding.rvSavedItinerary.layoutManager = LinearLayoutManager(context)
        binding.rvSavedAttractions.layoutManager = LinearLayoutManager(context)
        binding.rvSavedPacking.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            val trip = viewModel.getTripById(tripId)
            if (trip != null) {
                binding.tvDestination.text = trip.destination
                
                if (trip.aiResponseJson != null) {
                    try {
                        val response = Gson().fromJson(trip.aiResponseJson, GeminiTripResponse::class.java)
                        
                        binding.tvTripOverview.text = response.tripOverview
                        
                        // Set Itinerary
                        binding.rvSavedItinerary.adapter = ItineraryAdapter(
                            response.itinerary,
                            onAddActivity = {},
                            onRemoveActivity = { _, _ -> }
                        )
                        
                        // Set Hotel
                        val selectedHotel = response.suggestedHotels.find { it.isSelected } ?: response.suggestedHotels.firstOrNull()
                        if (selectedHotel != null) {
                            binding.tvSavedHotelName.text = selectedHotel.name
                            binding.tvSavedHotelDesc.text = selectedHotel.description
                        } else {
                            binding.cardHotel.visibility = View.GONE
                            binding.tvLabelHotel.visibility = View.GONE
                        }
                        
                        // Set Attractions
                        binding.rvSavedAttractions.adapter = AttractionAdapter(
                            response.suggestedAttractions,
                            onRemoveClick = {}
                        )

                        // Set Budget Breakdown
                        val format = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
                        binding.layoutSavedBudgetItems.removeAllViews()
                        
                        // Add Planned Budget first
                        val tvPlanned = TextView(requireContext())
                        tvPlanned.text = "Planned Budget: ${format.format(trip.budget)}"
                        tvPlanned.setPadding(0, 0, 0, 16)
                        tvPlanned.textStyleBold()
                        binding.layoutSavedBudgetItems.addView(tvPlanned)

                        response.budgetBreakdown.items.forEach { item ->
                            val tvItem = TextView(requireContext())
                            tvItem.text = "${item.name}: ${format.format(item.amount)}"
                            tvItem.setPadding(0, 0, 0, 8)
                            binding.layoutSavedBudgetItems.addView(tvItem)
                        }
                        
                        val tvTotal = TextView(requireContext())
                        tvTotal.text = "Estimated Total: ${format.format(response.budgetBreakdown.totalEstimatedCost)}"
                        tvTotal.setPadding(0, 16, 0, 0)
                        tvTotal.textStyleBold()
                        tvTotal.setTextColor(requireContext().getColor(R.color.tripon_green))
                        binding.layoutSavedBudgetItems.addView(tvTotal)
                        
                        // Set Packing
                        binding.rvSavedPacking.adapter = PackingAdapter(
                            response.packingChecklist.map { PackingItem(name = it) },
                            onRemoveClick = {}
                        )
                        
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun TextView.textStyleBold() {
        this.setTypeface(null, Typeface.BOLD)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
