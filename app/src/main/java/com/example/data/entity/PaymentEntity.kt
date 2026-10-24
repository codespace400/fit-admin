package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val planId: Long,
    val planName: String,
    val amount: Double,
    val paymentDate: Long,
    val paymentMethod: String, // "Credit Card", "Cash", "Bank Transfer", "POS Terminal", "Mobile Pay"
    val extendedDays: Int = 30,
    val notes: String = "",
    val receiptNumber: String = ""
)
