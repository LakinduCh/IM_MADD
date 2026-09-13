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
import com.example.triponai.data.remote.PackingItem
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentPackingChecklistBinding
import com.example.triponai.ui.plan.adapters.PackingAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class PackingChecklistFragment : Fragment() {

    private var _binding: FragmentPackingChecklistBinding? = null
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
        _binding = FragmentPackingChecklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPacking.layoutManager = LinearLayoutManager(context)

        viewModel.geminiResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                // Map String list from API to PackingItem list for UI tracking
                val items = response.packingChecklist.map { PackingItem(name = it) }
                binding.rvPacking.adapter = PackingAdapter(
                    items,
                    onRemoveClick = { item -> viewModel.removePackingItem(item.name) }
                )
            }
        }

        binding.btnAddItem.setOnClickListener {
            showAddItemDialog()
        }

        binding.btnSaveTripFinal.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                binding.btnSaveTripFinal.isEnabled = false
                viewModel.saveTripToRoom(user.uid) {
                    Toast.makeText(context, "Trip saved successfully!", Toast.LENGTH_SHORT).show()
                    viewModel.resetPlan()
                    findNavController().navigate(R.id.homeFragment)
                }
            } else {
                Toast.makeText(context, "Please login to save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddItemDialog() {
        val etItem = EditText(requireContext())
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Packing Item")
            .setView(etItem)
            .setPositiveButton("Add") { _, _ ->
                val name = etItem.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.addPackingItem(name) // This still needs String if I changed VM
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
