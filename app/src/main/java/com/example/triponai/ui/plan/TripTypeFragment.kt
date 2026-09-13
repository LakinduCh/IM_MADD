package com.example.triponai.ui.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentTripTypeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TripTypeFragment : Fragment() {

    private var _binding: FragmentTripTypeBinding? = null
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
    private var selectedType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackPressHandler()
        setupMenuHandler()

        val cardIds = listOf(
            R.id.card_adventure to "Adventure",
            R.id.card_nature to "Nature",
            R.id.card_relaxation to "Relaxation",
            R.id.card_cultural to "Cultural",
            R.id.card_food to "Food Exploration",
            R.id.card_beach to "Beach Holiday",
            R.id.card_shopping to "Shopping"
        )
        
        val iconMap = mapOf(
            "Adventure" to R.drawable.ic_adventure,
            "Nature" to R.drawable.ic_nature,
            "Relaxation" to R.drawable.ic_relaxation,
            "Cultural" to R.drawable.ic_cultural,
            "Food Exploration" to R.drawable.ic_food,
            "Beach Holiday" to R.drawable.ic_beach,
            "Shopping" to R.drawable.ic_shopping
        )

        cardIds.forEach { (id, label) ->
            val cardRoot = binding.root.findViewById<View>(id)
            cardRoot.findViewById<TextView>(R.id.tv_label).text = label
            iconMap[label]?.let { cardRoot.findViewById<ImageView>(R.id.iv_icon).setImageResource(it) }

            cardRoot.setOnClickListener {
                selectedType = label
                updateSelectionUI()
                binding.btnContinue.isEnabled = true
            }
        }

        binding.btnContinue.setOnClickListener {
            selectedType?.let {
                viewModel.updateTripType(it)
                findNavController().navigate(R.id.action_tripTypeFragment_to_travelGroupFragment)
            }
        }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showCancelConfirmationDialog()
            }
        })
    }
    
    private fun setupMenuHandler() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == android.R.id.home) {
                    showCancelConfirmationDialog()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showCancelConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_cancel_trip_title)
            .setMessage(R.string.dialog_cancel_trip_msg)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.resetPlan()
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.dialog_continue) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateSelectionUI() {
        val cardIds = listOf(
            R.id.card_adventure, R.id.card_nature, R.id.card_relaxation,
            R.id.card_cultural, R.id.card_food, R.id.card_beach, R.id.card_shopping
        )
        
        cardIds.forEach { id ->
            val cardRoot = binding.root.findViewById<View>(id)
            val container = cardRoot.findViewById<View>(R.id.layout_container)
            val label = cardRoot.findViewById<TextView>(R.id.tv_label).text.toString()
            
            if (label == selectedType) {
                container.setBackgroundResource(R.drawable.bg_selection_card_selected)
            } else {
                container.setBackgroundResource(R.drawable.bg_selection_card)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
