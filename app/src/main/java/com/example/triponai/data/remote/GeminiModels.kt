package com.example.triponai.data.remote

data class GeminiTripResponse(
    val tripOverview: String,
    var itinerary: List<DayItinerary>,
    var budgetBreakdown: BudgetBreakdown,
    var suggestedAttractions: List<Attraction>,
    var suggestedHotels: List<Hotel>,
    var packingChecklist: List<String>,
    val travelTips: List<String>
)

data class DayItinerary(
    val dayNumber: Int,
    val dayTitle: String,
    var activities: List<ActivityItem>
)

data class ActivityItem(
    val time: String,
    val activity: String,
    val description: String,
    val estimatedCost: String? = null
)

data class BudgetBreakdown(
    var items: List<BudgetItem>,
    var totalEstimatedCost: Double
)

data class BudgetItem(
    val name: String,
    var amount: Double
)

data class Attraction(
    val name: String,
    val description: String,
    val location: String
)

data class Hotel(
    val name: String,
    val description: String,
    val starRating: String,
    val location: String? = null,
    var isSelected: Boolean = false
)

data class PackingItem(
    val name: String,
    var isChecked: Boolean = false
)
