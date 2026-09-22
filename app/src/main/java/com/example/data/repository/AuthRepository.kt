package com.example.data.repository

import com.example.data.local.dao.UserDao
import com.example.data.local.entity.UserEntity
import com.example.util.SecurityUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val userDao: UserDao) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    suspend fun registerUser(
        fullName: String,
        mobileNumber: String,
        email: String,
        password: String,
        state: String,
        district: String,
        address: String,
        aadhaarLast4: String
    ): Result<UserEntity> {
        return try {
            // Check if user already exists
            val existing = userDao.getUserByMobileOrEmail(mobileNumber.trim(), email.trim())
            if (existing != null) {
                return Result.failure(Exception("An account with this mobile or email already exists."))
            }

            val user = UserEntity(
                fullName = fullName.trim(),
                mobileNumber = mobileNumber.trim(),
                email = email.trim().lowercase(),
                passwordHash = SecurityUtil.hashPassword(password),
                state = state.trim(),
                district = district.trim(),
                address = address.trim(),
                aadhaarLast4 = aadhaarLast4.trim(),
                isVerified = true
            )
            val id = userDao.insertUser(user)
            val createdUser = user.copy(id = id)
            _currentUser.value = createdUser
            Result.success(createdUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(identifier: String, password: String): Result<UserEntity> {
        return try {
            val cleanId = identifier.trim()
            val user = if (cleanId.contains("@")) {
                userDao.getUserByEmail(cleanId.lowercase())
            } else {
                userDao.getUserByMobile(cleanId)
            } ?: return Result.failure(Exception("No citizen account found with this mobile or email."))

            if (!SecurityUtil.verifyPassword(password, user.passwordHash)) {
                return Result.failure(Exception("Incorrect password. Please try again."))
            }

            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(identifier: String, newPassword: String): Result<Boolean> {
        return try {
            val cleanId = identifier.trim()
            val user = if (cleanId.contains("@")) {
                userDao.getUserByEmail(cleanId.lowercase())
            } else {
                userDao.getUserByMobile(cleanId)
            } ?: return Result.failure(Exception("No account found with this identifier."))

            val updatedUser = user.copy(passwordHash = SecurityUtil.hashPassword(newPassword))
            userDao.updateUser(updatedUser)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun setCurrentUserDirectly(user: UserEntity) {
        _currentUser.value = user
    }
}
