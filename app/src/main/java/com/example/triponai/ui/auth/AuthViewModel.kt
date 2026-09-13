package com.example.triponai.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triponai.data.entity.UserEntity
import com.example.triponai.data.repository.AuthRepository
import com.example.triponai.data.repository.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUser: FirebaseUser?
        get() = authRepository.currentUser

    fun login(email: String, password: String): Task<AuthResult> {
        return authRepository.login(email, password)
    }

    fun register(name: String, email: String, phone: String, password: String, onResult: (Task<AuthResult>) -> Unit) {
        authRepository.register(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = task.result?.user
                if (firebaseUser != null) {
                    saveUserToRoom(firebaseUser.uid, name, email, phone)
                }
            }
            onResult(task)
        }
    }

    private fun saveUserToRoom(uid: String, name: String, email: String, phone: String) {
        viewModelScope.launch {
            val user = UserEntity(
                uid = uid,
                name = name,
                email = email,
                phone = phone
            )
            userRepository.saveUser(user)
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
