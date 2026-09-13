package com.example.triponai.ui.profile

import androidx.lifecycle.*
import com.example.triponai.data.entity.UserEntity
import com.example.triponai.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun getUser(uid: String): LiveData<UserEntity?> {
        return repository.getUser(uid).asLiveData()
    }

    fun saveUser(user: UserEntity) {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }
}
