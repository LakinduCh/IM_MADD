package com.example.triponai.data.repository

import android.util.Log
import com.example.triponai.BuildConfig
import com.example.triponai.data.remote.GeminiTripResponse
import com.example.triponai.ui.plan.TripPlanInput
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeminiRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3.6-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        },
        requestOptions = RequestOptions(apiVersion = "v1beta")
    )

    private val gson = Gson()

    suspend fun generateTripPlan(input: TripPlanInput): Result<GeminiTripResponse> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(Exception("API Key is missing in local.properties"))
        }

        val dateStr = input.tripDate?.let {
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it))
        } ?: "unspecified date"

        val prompt = """
            You are a Sri Lankan local travel expert. Create a detailed, personalized Sri Lankan travel itinerary.
            Strictly focus on local Sri Lankan travel. Do not include international flights.
            
            Trip Details:
            - Destination: ${input.destination}, Sri Lanka
            - Start Date: $dateStr
            - Type: ${input.tripType}
            - Group: ${input.travelGroup}
            - Duration: ${input.numberOfDays} days
            - Travelers: ${input.numberOfTravelers}
            - Total Budget: LKR ${input.budget}

            Return ONLY a valid JSON object strictly following this structure:
            {
                "tripOverview": "A friendly summary of the trip in Sri Lanka.",
                "itinerary": [
                    {
                        "dayNumber": 1,
                        "dayTitle": "Arrival and Exploring Galle Fort",
                        "activities": [
                            { "time": "09:00 AM", "activity": "Walk through Galle Fort", "description": "Explore the historic colonial fort." }
                        ]
                    }
                ],
                "budgetBreakdown": { 
                    "items": [
                        { "name": "Accommodation", "amount": 15000.0 },
                        { "name": "Food", "amount": 10000.0 },
                        { "name": "Transport", "amount": 5000.0 },
                        { "name": "Activities", "amount": 10000.0 },
                        { "name": "Other", "amount": 5000.0 }
                    ],
                    "totalEstimatedCost": 45000.0 
                },
                "suggestedAttractions": [ { "name": "Place Name", "description": "Why visit", "location": "Area" } ],
                "suggestedHotels": [ { "name": "Hotel Name", "description": "Atmosphere", "starRating": "4 Stars" } ],
                "packingChecklist": ["Sunscreen", "Umbrella", "Cotton clothes"],
                "travelTips": ["Use PickMe or Uber for tuk-tuks in cities", "Carry a reusable water bottle"]
            }
        """.trimIndent()

        return@withContext try {
            val response = generativeModel.generateContent(prompt)
            val text = response.text
            
            if (text.isNullOrEmpty()) {
                Result.failure(Exception("AI returned empty content. This may be a safety filter block."))
            } else {
                val result = gson.fromJson(text.trim(), GeminiTripResponse::class.java)
                if (result == null) Result.failure(Exception("Failed to parse JSON"))
                else Result.success(result)
            }
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error: ${e.message}", e)
            val msg = e.toString()
            val userFriendlyError = when {
                msg.contains("404") -> "API Model not found. Check if Generative Language API is enabled."
                msg.contains("403") -> "Access Denied. Check your API key and restrictions."
                else -> e.message ?: "Unknown API error"
            }
            Result.failure(Exception(userFriendlyError))
        }
    }
}
