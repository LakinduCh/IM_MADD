package com.example.triponai.data.repository

import com.example.triponai.data.dao.UserDao
import com.example.triponai.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    fun getUser(uid: String): Flow<UserEntity?> = userDao.getUser(uid)

    suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun deleteUser(uid: String) {
        userDao.deleteUser(uid)
    }
}
