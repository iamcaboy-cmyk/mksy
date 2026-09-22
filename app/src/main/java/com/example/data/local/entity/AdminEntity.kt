package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "admins",
    indices = [Index(value = ["username"], unique = true)]
)
data class AdminEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String, // e.g. admin@jansahayata.gov.in
    val fullName: String,
    val role: String, // ADMIN, VERIFYING_OFFICER, DISTRICT_NODAL_OFFICER
    val passwordHash: String,
    val department: String,
    val district: String = "All",
    val isActive: Boolean = true
)
