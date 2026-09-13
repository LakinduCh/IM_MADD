package com.example.triponai.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.triponai.data.database.AppDatabase
import com.example.triponai.data.entity.UserEntity
import com.example.triponai.data.repository.UserRepository
import com.example.triponai.databinding.FragmentProfileDetailsBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileDetailsFragment : Fragment() {

    private var _binding: FragmentProfileDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        UserViewModelFactory(UserRepository(database.userDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.getUser(currentUser.uid).observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.etName.setText(user.name)
                    binding.etPhone.setText(user.phone)
                }
            }
        }

        binding.btnSave.setOnClickListener {
            if (currentUser != null) {
                saveProfile(currentUser.uid, currentUser.email ?: "")
            }
        }
    }

    private fun saveProfile(uid: String, email: String) {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val user = UserEntity(
            uid = uid,
            name = name,
            email = email,
            phone = phone
        )
        viewModel.saveUser(user)
        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
