package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE memberId = :memberId ORDER BY paymentDate DESC")
    fun getPaymentsForMember(memberId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE paymentDate >= :startOfMonth ORDER BY paymentDate DESC")
    fun getPaymentsThisMonth(startOfMonth: Long): Flow<List<PaymentEntity>>

    @Query("SELECT SUM(amount) FROM payments WHERE paymentDate >= :startOfMonth")
    fun getTotalRevenueThisMonth(startOfMonth: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(payments: List<PaymentEntity>)

    @Query("SELECT COUNT(*) FROM payments")
    suspend fun getPaymentsCount(): Int
}
