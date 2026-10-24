package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gym_members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String,
    val avatarInitials: String = "",
    val avatarColorHex: String = "#84CC16",
    val photoUrl: String? = null,
    val planId: Long,
    val planName: String,
    val startDate: Long,
    val expiryDate: Long,
    val assignedTrainer: String? = null,
    val notes: String = "",
    val emergencyContact: String = "",
    val lastCheckInDate: Long? = null,
    val totalCheckIns: Int = 0
) {
    fun isExpired(currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        return expiryDate < currentTimeMs
    }

    fun daysUntilExpiry(currentTimeMs: Long = System.currentTimeMillis()): Long {
        val diffMs = expiryDate - currentTimeMs
        return (diffMs / (1000L * 60 * 60 * 24))
    }
}
