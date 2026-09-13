package com.example.triponai.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.triponai.data.entity.TripEntity
import com.example.triponai.data.repository.TripRepository

class HomeViewModel(private val repository: TripRepository) : ViewModel() {

    fun getUpcomingTrips(userUid: String): LiveData<List<TripEntity>> {
        return repository.getTrips(userUid).asLiveData()
    }

    suspend fun getTripById(tripId: Long): TripEntity? {
        return repository.getTripById(tripId)
    }
}
