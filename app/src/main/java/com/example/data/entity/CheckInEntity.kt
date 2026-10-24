package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_ins")
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val planName: String,
    val checkInTime: Long,
    val isAllowed: Boolean = true,
    val statusReason: String = "Access Granted",
    val deskStaff: String = "Front Desk Admin"
)
