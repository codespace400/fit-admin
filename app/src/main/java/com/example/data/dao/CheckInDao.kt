package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.CheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins ORDER BY checkInTime DESC")
    fun getAllCheckIns(): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins WHERE memberId = :memberId ORDER BY checkInTime DESC")
    fun getCheckInsForMember(memberId: Long): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins WHERE checkInTime >= :startOfDay ORDER BY checkInTime DESC")
    fun getCheckInsToday(startOfDay: Long): Flow<List<CheckInEntity>>

    @Query("SELECT COUNT(*) FROM check_ins WHERE checkInTime >= :startOfDay AND isAllowed = 1")
    fun getSuccessfulCheckInsTodayCount(startOfDay: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(checkIns: List<CheckInEntity>)

    @Query("SELECT COUNT(*) FROM check_ins")
    suspend fun getCheckInsCount(): Int
}
