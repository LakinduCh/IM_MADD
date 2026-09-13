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
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentHomeBinding
import com.example.triponai.ui.home.adapters.TripAdapter
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.rvUpcomingTrips.layoutManager = LinearLayoutManager(context)

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

                val upcoming = allTrips.filter { it.startDate >= todayStart }

                if (upcoming.isEmpty()) {
                    binding.layoutEmptyTrips.visibility = View.VISIBLE
                    binding.rvUpcomingTrips.visibility = View.GONE
                } else {
                    binding.layoutEmptyTrips.visibility = View.GONE
                    binding.rvUpcomingTrips.visibility = View.VISIBLE
                    // Show top 3 nearest upcoming trips on Home
                    binding.rvUpcomingTrips.adapter = TripAdapter(upcoming.take(3)) { trip ->
                        val bundle = Bundle()
                        bundle.putLong("tripId", trip.id)
                        findNavController().navigate(R.id.action_homeFragment_to_previousTripDetailFragment, bundle)
                    }
                }
            }
        }

        binding.btnStartPlanning.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_tripTypeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
