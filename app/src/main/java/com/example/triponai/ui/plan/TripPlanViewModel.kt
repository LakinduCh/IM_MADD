package com.example.triponai.ui.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triponai.data.entity.ItineraryEntity
import com.example.triponai.data.entity.TripEntity
import com.example.triponai.data.remote.*
import com.example.triponai.data.repository.GeminiRepository
import com.example.triponai.data.repository.TripRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

data class TripPlanInput(
    var tripType: String = "",
    var travelGroup: String = "",
    var destination: String = "",
    var numberOfDays: Int = 0,
    var numberOfTravelers: Int = 0,
    var budget: Double = 0.0,
    var tripDate: Long? = null
)

class TripPlanViewModel(private val tripRepository: TripRepository) : ViewModel() {

    private val geminiRepository = GeminiRepository()
    private val gson = Gson()

    private val _tripPlanInput = MutableLiveData(TripPlanInput())
    val tripPlanInput: LiveData<TripPlanInput> = _tripPlanInput

    private val _geminiResponse = MutableLiveData<GeminiTripResponse?>()
    val geminiResponse: LiveData<GeminiTripResponse?> = _geminiResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun updateTripType(type: String) {
        _tripPlanInput.value = _tripPlanInput.value?.copy(tripType = type)
    }

    fun updateTravelGroup(group: String) {
        _tripPlanInput.value = _tripPlanInput.value?.copy(travelGroup = group)
    }

    fun updateTripDetails(destination: String, days: Int, travelers: Int, budget: Double, date: Long?) {
        _tripPlanInput.value = _tripPlanInput.value?.copy(
            destination = destination,
            numberOfDays = days,
            numberOfTravelers = travelers,
            budget = budget,
            tripDate = date
        )
    }

    fun clearGeminiResponse() {
        _geminiResponse.value = null
        _error.value = null
    }

    fun generateItinerary() {
        val input = _tripPlanInput.value ?: return
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = geminiRepository.generateTripPlan(input)
            result.onSuccess { response ->
                if (response.suggestedHotels.isNotEmpty()) {
                    response.suggestedHotels[0].isSelected = true
                }
                _geminiResponse.value = response
            }.onFailure {
                _error.value = it.message ?: "Failed to generate itinerary"
            }
            _isLoading.value = false
        }
    }

    // Itinerary Customization
    fun removeActivity(dayNumber: Int, activity: ActivityItem) {
        val current = _geminiResponse.value ?: return
        val updatedItinerary = current.itinerary.map { day ->
            if (day.dayNumber == dayNumber) {
                day.copy(activities = day.activities.filter { it != activity })
            } else day
        }
        _geminiResponse.value = current.copy(itinerary = updatedItinerary)
    }

    fun addActivity(dayNumber: Int, activity: ActivityItem) {
        val current = _geminiResponse.value ?: return
        val updatedItinerary = current.itinerary.map { day ->
            if (day.dayNumber == dayNumber) {
                day.copy(activities = day.activities + activity)
            } else day
        }
        _geminiResponse.value = current.copy(itinerary = updatedItinerary)
    }

    // Hotel Customization
    fun selectHotel(hotel: Hotel) {
        val current = _geminiResponse.value ?: return
        val updatedHotels = current.suggestedHotels.map {
            it.copy(isSelected = it.name == hotel.name)
        }
        _geminiResponse.value = current.copy(suggestedHotels = updatedHotels)
    }

    fun removeHotel(hotel: Hotel) {
        val current = _geminiResponse.value ?: return
        _geminiResponse.value = current.copy(suggestedHotels = current.suggestedHotels.filter { it.name != hotel.name })
    }

    fun addHotel(hotel: Hotel) {
        val current = _geminiResponse.value ?: return
        _geminiResponse.value = current.copy(suggestedHotels = current.suggestedHotels + hotel.copy(isSelected = true))
        selectHotel(hotel)
    }

    // Attraction Customization
    fun removeAttraction(attraction: Attraction) {
        val current = _geminiResponse.value ?: return
        _geminiResponse.value = current.copy(suggestedAttractions = current.suggestedAttractions.filter { it.name != attraction.name })
    }

    fun addAttraction(attraction: Attraction) {
        val current = _geminiResponse.value ?: return
        _geminiResponse.value = current.copy(suggestedAttractions = current.suggestedAttractions + attraction)
    }

    // Packing Customization
    fun removePackingItem(itemName: String) {
        val current = _geminiResponse.value ?: return
        _geminiResponse.value = current.copy(packingChecklist = current.packingChecklist.filter { it != itemName })
    }

    fun addPackingItem(itemName: String) {
        val current = _geminiResponse.value ?: return
        _geminiResponse.value = current.copy(packingChecklist = current.packingChecklist + itemName)
    }

    // Budget Customization
    fun updateBudgetItem(itemName: String, newAmount: Double) {
        val current = _geminiResponse.value ?: return
        val updatedItems = current.budgetBreakdown.items.map {
            if (it.name == itemName) it.copy(amount = newAmount) else it
        }
        val newTotal = updatedItems.sumOf { it.amount }
        _geminiResponse.value = current.copy(
            budgetBreakdown = current.budgetBreakdown.copy(items = updatedItems, totalEstimatedCost = newTotal)
        )
    }

    fun addBudgetItem(item: BudgetItem) {
        val current = _geminiResponse.value ?: return
        val updatedItems = current.budgetBreakdown.items + item
        val newTotal = updatedItems.sumOf { it.amount }
        _geminiResponse.value = current.copy(
            budgetBreakdown = current.budgetBreakdown.copy(items = updatedItems, totalEstimatedCost = newTotal)
        )
    }

    fun removeBudgetItem(itemName: String) {
        val current = _geminiResponse.value ?: return
        val updatedItems = current.budgetBreakdown.items.filter { it.name != itemName }
        val newTotal = updatedItems.sumOf { it.amount }
        _geminiResponse.value = current.copy(
            budgetBreakdown = current.budgetBreakdown.copy(items = updatedItems, totalEstimatedCost = newTotal)
        )
    }

    fun saveTripToRoom(userUid: String, onSuccess: () -> Unit) {
        val input = _tripPlanInput.value ?: return
        val response = _geminiResponse.value ?: return
        
        viewModelScope.launch {
            val trip = TripEntity(
                userUid = userUid,
                destination = input.destination,
                startDate = input.tripDate ?: System.currentTimeMillis(),
                endDate = (input.tripDate ?: System.currentTimeMillis()) + (input.numberOfDays.toLong() * 24 * 60 * 60 * 1000),
                tripType = input.tripType,
                travelGroup = input.travelGroup,
                budget = input.budget,
                travelers = input.numberOfTravelers,
                aiResponseJson = gson.toJson(response)
            )
            
            val tripId = tripRepository.saveTrip(trip)
            
            response.itinerary.forEach { day ->
                val itinerary = ItineraryEntity(
                    tripId = tripId,
                    dayNumber = day.dayNumber,
                    activities = gson.toJson(day.activities),
                    hotelInfo = response.suggestedHotels.find { it.isSelected }?.name
                )
                tripRepository.saveItinerary(itinerary)
            }
            
            onSuccess()
        }
    }
    
    fun resetPlan() {
        _tripPlanInput.value = TripPlanInput()
        _geminiResponse.value = null
        _error.value = null
    }
}
