package com.example.triponai.ui.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.repository.TripRepository
import com.example.triponai.databinding.FragmentTravelGroupBinding

class TravelGroupFragment : Fragment() {

    private var _binding: FragmentTravelGroupBinding? = null
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
    private var selectedGroup: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTravelGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardIds = listOf(
            R.id.card_solo to "Solo",
            R.id.card_couple to "Couple",
            R.id.card_family to "Family",
            R.id.card_friends to "Friends",
            R.id.card_business to "Business"
        )
        
        val iconMap = mapOf(
            "Solo" to R.drawable.ic_solo,
            "Couple" to R.drawable.ic_group,
            "Family" to R.drawable.ic_family,
            "Friends" to R.drawable.ic_group,
            "Business" to R.drawable.ic_business
        )

        cardIds.forEach { (id, label) ->
            val cardRoot = binding.root.findViewById<View>(id)
            cardRoot.findViewById<TextView>(R.id.tv_label).text = label
            iconMap[label]?.let { cardRoot.findViewById<ImageView>(R.id.iv_icon).setImageResource(it) }

            cardRoot.setOnClickListener {
                selectedGroup = label
                updateSelectionUI()
                binding.btnContinue.isEnabled = true
            }
        }

        binding.btnContinue.setOnClickListener {
            selectedGroup?.let {
                viewModel.updateTravelGroup(it)
                findNavController().navigate(R.id.action_travelGroupFragment_to_tripDetailsFragment)
            }
        }
    }

    private fun updateSelectionUI() {
        val cardIds = listOf(
            R.id.card_solo, R.id.card_couple, R.id.card_family,
            R.id.card_friends, R.id.card_business
        )
        
        cardIds.forEach { id ->
            val cardRoot = binding.root.findViewById<View>(id)
            val container = cardRoot.findViewById<View>(R.id.layout_container)
            val label = cardRoot.findViewById<TextView>(R.id.tv_label).text.toString()
            
            if (label == selectedGroup) {
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
