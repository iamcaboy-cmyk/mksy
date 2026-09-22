package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["mobileNumber"], unique = true), Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val mobileNumber: String,
    val email: String,
    val passwordHash: String,
    val state: String,
    val district: String,
    val address: String,
    val aadhaarLast4: String,
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
