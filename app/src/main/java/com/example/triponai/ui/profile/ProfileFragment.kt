package com.example.triponai.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.triponai.R
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.repository.UserRepository
import com.example.triponai.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: UserViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        UserViewModelFactory(UserRepository(database.userDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.getUser(currentUser.uid).observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.tvName.text = user.name
                    binding.tvEmail.text = user.email
                } else {
                    // Fallback to Firebase data if Room is empty
                    binding.tvName.text = currentUser.displayName ?: "TripON AI User"
                    binding.tvEmail.text = currentUser.email
                }
            }
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileDetailsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
