package com.example.triponai.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.entity.TripEntity
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentMyTripsBinding
import com.example.triponai.ui.home.adapters.TripAdapter
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MyTripsFragment : Fragment() {

    private var _binding: FragmentMyTripsBinding? = null
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
        _binding = FragmentMyTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUpcomingTrips.layoutManager = LinearLayoutManager(context)
        binding.rvPreviousTrips.layoutManager = LinearLayoutManager(context)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.getUpcomingTrips(currentUser.uid).observe(viewLifecycleOwner) { allTrips ->
                if (allTrips == null) return@observe

                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                today.set(Calendar.MILLISECOND, 0)
                val todayStart = today.timeInMillis

                val upcoming = mutableListOf<TripEntity>()
                val previous = mutableListOf<TripEntity>()

                for (trip in allTrips) {
                    if (trip.startDate >= todayStart) {
                        upcoming.add(trip)
                    } else {
                        previous.add(trip)
                    }
                }

                // Setup Upcoming
                if (upcoming.isEmpty()) {
                    binding.rvUpcomingTrips.visibility = View.GONE
                    binding.tvNoUpcoming.visibility = View.VISIBLE
                } else {
                    binding.rvUpcomingTrips.visibility = View.VISIBLE
                    binding.tvNoUpcoming.visibility = View.GONE
                    binding.rvUpcomingTrips.adapter = TripAdapter(upcoming) { trip ->
                        openTripDetail(trip.id)
                    }
                }

                // Setup Previous
                if (previous.isEmpty()) {
                    binding.rvPreviousTrips.visibility = View.GONE
                    binding.tvNoPrevious.visibility = View.VISIBLE
                } else {
                    binding.rvPreviousTrips.visibility = View.VISIBLE
                    binding.tvNoPrevious.visibility = View.GONE
                    binding.rvPreviousTrips.adapter = TripAdapter(previous) { trip ->
                        openTripDetail(trip.id)
                    }
                }
            }
        }
    }

    private fun openTripDetail(tripId: Long) {
        val bundle = Bundle()
        bundle.putLong("tripId", tripId)
        findNavController().navigate(R.id.action_myTripsFragment_to_previousTripDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
