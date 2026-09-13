package com.example.triponai.ui.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.remote.ActivityItem
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentAiTripPlanBinding
import com.example.triponai.ui.plan.adapters.ItineraryAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class AITripPlanFragment : Fragment() {

    private var _binding: FragmentAiTripPlanBinding? = null
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
        _binding = FragmentAiTripPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvItinerary.layoutManager = LinearLayoutManager(context)

        viewModel.geminiResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                binding.tvOverviewText.text = response.tripOverview
                binding.tvDestination.text = viewModel.tripPlanInput.value?.destination
                binding.rvItinerary.adapter = ItineraryAdapter(
                    response.itinerary,
                    onAddActivity = { dayNumber -> showAddActivityDialog(dayNumber) },
                    onRemoveActivity = { dayNumber, activity -> viewModel.removeActivity(dayNumber, activity) }
                )
            }
        }

        binding.btnSaveTrip.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                viewModel.saveTripToRoom(user.uid) {
                    Toast.makeText(context, "Itinerary saved to My Trips!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please login to save", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSeeHotels.setOnClickListener {
            findNavController().navigate(R.id.action_aiTripPlanFragment_to_hotelSuggestionsFragment)
        }
    }

    private fun showAddActivityDialog(dayNumber: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_activity, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_activity_name)
        val etTime = dialogView.findViewById<EditText>(R.id.et_activity_time)
        val etDesc = dialogView.findViewById<EditText>(R.id.et_activity_desc)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Activity for Day $dayNumber")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val time = etTime.text.toString().trim()
                val desc = etDesc.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.addActivity(dayNumber, ActivityItem(time, name, desc))
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
