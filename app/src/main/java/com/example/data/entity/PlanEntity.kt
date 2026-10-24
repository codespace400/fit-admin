package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "membership_plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val durationDays: Int,
    val price: Double,
    val description: String,
    val colorHex: String = "#84CC16",
    val isActive: Boolean = true
)
