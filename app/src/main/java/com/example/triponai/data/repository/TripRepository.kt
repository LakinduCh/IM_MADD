package com.example.triponai.data.repository

import com.example.triponai.data.dao.*
import com.example.triponai.data.entity.*
import kotlinx.coroutines.flow.Flow

class TripRepository(
    private val tripDao: TripDao,
    private val itineraryDao: ItineraryDao,
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao
) {
    // Trip operations
    fun getTrips(userUid: String): Flow<List<TripEntity>> = tripDao.getTripsForUser(userUid)
    suspend fun getTripById(tripId: Long): TripEntity? = tripDao.getTripById(tripId)
    suspend fun saveTrip(trip: TripEntity): Long = tripDao.insertTrip(trip)
    suspend fun deleteTrip(trip: TripEntity) = tripDao.deleteTrip(trip)

    // Itinerary operations
    fun getItinerary(tripId: Long): Flow<List<ItineraryEntity>> = itineraryDao.getItineraryForTrip(tripId)
    suspend fun saveItinerary(itinerary: ItineraryEntity) = itineraryDao.insertItinerary(itinerary)

    // Favorite operations
    fun getFavorites(userUid: String): Flow<List<FavoriteEntity>> = favoriteDao.getFavoritesForUser(userUid)
    suspend fun saveFavorite(favorite: FavoriteEntity) = favoriteDao.insertFavorite(favorite)
    suspend fun deleteFavorite(favorite: FavoriteEntity) = favoriteDao.deleteFavorite(favorite)

    // History operations
    fun getHistory(userUid: String): Flow<List<HistoryEntity>> = historyDao.getHistoryForUser(userUid)
    suspend fun saveHistory(history: HistoryEntity) = historyDao.insertHistory(history)
}
