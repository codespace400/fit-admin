package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM gym_members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM gym_members WHERE id = :id")
    fun getMemberById(id: Long): Flow<MemberEntity?>

    @Query("SELECT * FROM gym_members WHERE id = :id")
    suspend fun getMemberByIdDirect(id: Long): MemberEntity?

    @Query("SELECT * FROM gym_members WHERE expiryDate >= :now ORDER BY expiryDate ASC")
    fun getActiveMembers(now: Long = System.currentTimeMillis()): Flow<List<MemberEntity>>

    @Query("SELECT * FROM gym_members WHERE expiryDate < :now ORDER BY expiryDate DESC")
    fun getExpiredMembers(now: Long = System.currentTimeMillis()): Flow<List<MemberEntity>>

    @Query("SELECT * FROM gym_members WHERE expiryDate >= :now AND expiryDate <= :sevenDaysLater ORDER BY expiryDate ASC")
    fun getMembersExpiringSoon(now: Long, sevenDaysLater: Long): Flow<List<MemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    @Query("DELETE FROM gym_members WHERE id = :id")
    suspend fun deleteMemberById(id: Long)

    @Query("UPDATE gym_members SET expiryDate = :newExpiryDate WHERE id = :id")
    suspend fun updateMemberExpiry(id: Long, newExpiryDate: Long)

    @Query("UPDATE gym_members SET lastCheckInDate = :checkInTime, totalCheckIns = totalCheckIns + 1 WHERE id = :id")
    suspend fun recordCheckIn(id: Long, checkInTime: Long)

    @Query("SELECT COUNT(*) FROM gym_members")
    suspend fun getMembersCount(): Int
}
