package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.entity.CheckInEntity
import com.example.data.entity.MemberEntity
import com.example.data.entity.PaymentEntity
import com.example.data.entity.PlanEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

sealed class CheckInResult {
    data class Success(
        val member: MemberEntity,
        val message: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : CheckInResult()

    data class Expired(
        val member: MemberEntity,
        val message: String,
        val expiredDaysAgo: Long
    ) : CheckInResult()

    data class NotFound(val query: String) : CheckInResult()
}

class FitAdminRepository(private val database: AppDatabase) {

    private val memberDao = database.memberDao()
    private val planDao = database.planDao()
    private val paymentDao = database.paymentDao()
    private val checkInDao = database.checkInDao()

    suspend fun ensureDatabaseSeeded() = withContext(Dispatchers.IO) {
        if (memberDao.getMembersCount() == 0) {
            AppDatabase.populateInitialData(database)
        }
    }

    // Members
    val allMembers: Flow<List<MemberEntity>> = memberDao.getAllMembers()

    fun getMemberById(id: Long): Flow<MemberEntity?> = memberDao.getMemberById(id)

    suspend fun getMemberByIdDirect(id: Long): MemberEntity? = withContext(Dispatchers.IO) {
        memberDao.getMemberByIdDirect(id)
    }

    fun getActiveMembers(): Flow<List<MemberEntity>> =
        memberDao.getActiveMembers(System.currentTimeMillis())

    fun getExpiredMembers(): Flow<List<MemberEntity>> =
        memberDao.getExpiredMembers(System.currentTimeMillis())

    fun getMembersExpiringSoon(): Flow<List<MemberEntity>> {
        val now = System.currentTimeMillis()
        val sevenDaysLater = now + (7L * 24 * 60 * 60 * 1000)
        return memberDao.getMembersExpiringSoon(now, sevenDaysLater)
    }

    suspend fun insertMember(member: MemberEntity): Long = withContext(Dispatchers.IO) {
        memberDao.insertMember(member)
    }

    suspend fun updateMember(member: MemberEntity) = withContext(Dispatchers.IO) {
        memberDao.updateMember(member)
    }

    suspend fun deleteMember(member: MemberEntity) = withContext(Dispatchers.IO) {
        memberDao.deleteMember(member)
    }

    suspend fun deleteMemberById(id: Long) = withContext(Dispatchers.IO) {
        memberDao.deleteMemberById(id)
    }

    // Plans
    val allPlans: Flow<List<PlanEntity>> = planDao.getAllPlans()

    suspend fun getPlanById(id: Long): PlanEntity? = withContext(Dispatchers.IO) {
        planDao.getPlanById(id)
    }

    suspend fun insertPlan(plan: PlanEntity): Long = withContext(Dispatchers.IO) {
        planDao.insertPlan(plan)
    }

    suspend fun updatePlan(plan: PlanEntity) = withContext(Dispatchers.IO) {
        planDao.updatePlan(plan)
    }

    suspend fun deletePlan(plan: PlanEntity) = withContext(Dispatchers.IO) {
        planDao.deletePlan(plan)
    }

    // Payments
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()

    fun getPaymentsForMember(memberId: Long): Flow<List<PaymentEntity>> =
        paymentDao.getPaymentsForMember(memberId)

    fun getRevenueThisMonth(): Flow<Double?> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return paymentDao.getTotalRevenueThisMonth(cal.timeInMillis)
    }

    suspend fun recordPayment(
        memberId: Long,
        memberName: String,
        planId: Long,
        planName: String,
        amount: Double,
        paymentDate: Long,
        paymentMethod: String,
        extendedDays: Int,
        notes: String = "",
        autoExtendExpiry: Boolean = true
    ): Long = withContext(Dispatchers.IO) {
        val receiptNumber = "REC-${System.currentTimeMillis() % 1000000}"
        val payment = PaymentEntity(
            memberId = memberId,
            memberName = memberName,
            planId = planId,
            planName = planName,
            amount = amount,
            paymentDate = paymentDate,
            paymentMethod = paymentMethod,
            extendedDays = extendedDays,
            notes = notes,
            receiptNumber = receiptNumber
        )
        val paymentId = paymentDao.insertPayment(payment)

        if (autoExtendExpiry) {
            val member = memberDao.getMemberByIdDirect(memberId)
            if (member != null) {
                val now = System.currentTimeMillis()
                // If member was expired, extend from now. Otherwise extend from current expiry date.
                val baseTime = if (member.expiryDate < now) now else member.expiryDate
                val newExpiry = baseTime + (extendedDays.toLong() * 24 * 60 * 60 * 1000)
                memberDao.updateMemberExpiry(memberId, newExpiry)
            }
        }

        paymentId
    }

    // Check-Ins
    val allCheckIns: Flow<List<CheckInEntity>> = checkInDao.getAllCheckIns()

    fun getCheckInsForMember(memberId: Long): Flow<List<CheckInEntity>> =
        checkInDao.getCheckInsForMember(memberId)

    fun getCheckInsToday(): Flow<List<CheckInEntity>> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return checkInDao.getCheckInsToday(cal.timeInMillis)
    }

    fun getSuccessfulCheckInsTodayCount(): Flow<Int> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return checkInDao.getSuccessfulCheckInsTodayCount(cal.timeInMillis)
    }

    suspend fun performCheckIn(
        member: MemberEntity,
        deskStaff: String = "Front Desk Admin"
    ): CheckInResult = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val isExpired = member.isExpired(now)

        if (isExpired) {
            val daysAgo = Math.max(1, (now - member.expiryDate) / (1000L * 60 * 60 * 24))
            val log = CheckInEntity(
                memberId = member.id,
                memberName = member.name,
                planName = member.planName,
                checkInTime = now,
                isAllowed = false,
                statusReason = "Access Denied: Expired $daysAgo day(s) ago",
                deskStaff = deskStaff
            )
            checkInDao.insertCheckIn(log)
            CheckInResult.Expired(
                member = member,
                message = "Membership Expired $daysAgo day(s) ago. Renewal required.",
                expiredDaysAgo = daysAgo
            )
        } else {
            val log = CheckInEntity(
                memberId = member.id,
                memberName = member.name,
                planName = member.planName,
                checkInTime = now,
                isAllowed = true,
                statusReason = "Access Granted",
                deskStaff = deskStaff
            )
            checkInDao.insertCheckIn(log)
            memberDao.recordCheckIn(member.id, now)
            val daysLeft = member.daysUntilExpiry(now)
            CheckInResult.Success(
                member = member.copy(
                    lastCheckInDate = now,
                    totalCheckIns = member.totalCheckIns + 1
                ),
                message = "Access Granted! Welcome, ${member.name} ($daysLeft days remaining)",
                timestamp = now
            )
        }
    }
}
