package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.CheckInEntity
import com.example.data.entity.MemberEntity
import com.example.data.entity.PaymentEntity
import com.example.data.entity.PlanEntity
import com.example.data.repository.CheckInResult
import com.example.data.repository.FitAdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MemberFilter {
    ALL,
    ACTIVE,
    EXPIRING_SOON,
    EXPIRED
}

enum class MemberSort {
    EXPIRY_DATE,
    NAME_ASC,
    RECENT_CHECKIN
}

data class DashboardStats(
    val activeMembersCount: Int = 0,
    val expiringThisWeekCount: Int = 0,
    val revenueThisMonth: Double = 0.0,
    val checkInsTodayCount: Int = 0
)

class FitAdminViewModel(
    private val repository: FitAdminRepository
) : ViewModel() {

    // Front Desk Authentication State
    private val _isLoggedIn = MutableStateFlow(true) // Pre-authenticated for quick demo review, can be toggled
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentStaff = MutableStateFlow("Alex Rivera (Front Desk #1)")
    val currentStaff: StateFlow<String> = _currentStaff.asStateFlow()

    fun login(staffName: String, pin: String): Boolean {
        if (pin.length >= 4) {
            _currentStaff.value = staffName
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // SnackBar / Alert Notification
    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    fun showMessage(msg: String) {
        _notificationMessage.value = msg
    }

    fun clearMessage() {
        _notificationMessage.value = null
    }

    // Plans
    val allPlans: StateFlow<List<PlanEntity>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Members
    val allMembers: StateFlow<List<MemberEntity>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Member Search & Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(MemberFilter.ALL)
    val selectedFilter: StateFlow<MemberFilter> = _selectedFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(MemberSort.EXPIRY_DATE)
    val selectedSort: StateFlow<MemberSort> = _selectedSort.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: MemberFilter) {
        _selectedFilter.value = filter
    }

    fun setSort(sort: MemberSort) {
        _selectedSort.value = sort
    }

    // Filtered and Sorted Members List
    val filteredMembers: StateFlow<List<MemberEntity>> = combine(
        allMembers,
        _searchQuery,
        _selectedFilter,
        _selectedSort
    ) { members, query, filter, sort ->
        val now = System.currentTimeMillis()
        val sevenDays = now + (7L * 24 * 60 * 60 * 1000)

        val filtered = members.filter { member ->
            val matchesQuery = query.isBlank() ||
                    member.name.contains(query, ignoreCase = true) ||
                    member.phone.contains(query) ||
                    member.planName.contains(query, ignoreCase = true) ||
                    (member.assignedTrainer?.contains(query, ignoreCase = true) == true)

            val matchesFilter = when (filter) {
                MemberFilter.ALL -> true
                MemberFilter.ACTIVE -> !member.isExpired(now)
                MemberFilter.EXPIRING_SOON -> !member.isExpired(now) && member.expiryDate <= sevenDays
                MemberFilter.EXPIRED -> member.isExpired(now)
            }

            matchesQuery && matchesFilter
        }

        when (sort) {
            MemberSort.EXPIRY_DATE -> filtered.sortedBy { it.expiryDate }
            MemberSort.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            MemberSort.RECENT_CHECKIN -> filtered.sortedByDescending { it.lastCheckInDate ?: 0L }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expiring Soon (Within 7 days)
    val membersExpiringSoon: StateFlow<List<MemberEntity>> = repository.getMembersExpiringSoon()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Stats combining flows
    val dashboardStats: StateFlow<DashboardStats> = combine(
        allMembers,
        membersExpiringSoon,
        repository.getRevenueThisMonth(),
        repository.getSuccessfulCheckInsTodayCount()
    ) { members, expiring, revenue, checkInsToday ->
        val now = System.currentTimeMillis()
        val activeCount = members.count { !it.isExpired(now) }
        DashboardStats(
            activeMembersCount = activeCount,
            expiringThisWeekCount = expiring.size,
            revenueThisMonth = revenue ?: 0.0,
            checkInsTodayCount = checkInsToday
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Today's check-ins log
    val checkInsToday: StateFlow<List<CheckInEntity>> = repository.getCheckInsToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Payments
    val allPayments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Member for Profile
    private val _selectedMemberId = MutableStateFlow<Long?>(null)
    val selectedMemberId: StateFlow<Long?> = _selectedMemberId.asStateFlow()

    fun selectMember(id: Long?) {
        _selectedMemberId.value = id
    }

    val selectedMember: StateFlow<MemberEntity?> = _selectedMemberId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else repository.getMemberById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedMemberPayments: StateFlow<List<PaymentEntity>> = _selectedMemberId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getPaymentsForMember(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedMemberCheckIns: StateFlow<List<CheckInEntity>> = _selectedMemberId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getCheckInsForMember(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Check-In Processing
    private val _checkInResult = MutableStateFlow<CheckInResult?>(null)
    val checkInResult: StateFlow<CheckInResult?> = _checkInResult.asStateFlow()

    private val _isCheckInProcessing = MutableStateFlow(false)
    val isCheckInProcessing: StateFlow<Boolean> = _isCheckInProcessing.asStateFlow()

    fun processCheckIn(member: MemberEntity) {
        viewModelScope.launch {
            _isCheckInProcessing.value = true
            val result = repository.performCheckIn(member, deskStaff = _currentStaff.value)
            _checkInResult.value = result
            _isCheckInProcessing.value = false
            when (result) {
                is CheckInResult.Success -> {
                    showMessage("✓ Check-in approved for ${member.name}")
                }
                is CheckInResult.Expired -> {
                    showMessage("⚠️ Check-in denied: ${member.name}'s membership is expired!")
                }
                is CheckInResult.NotFound -> {
                    showMessage("Member not found.")
                }
            }
        }
    }

    fun clearCheckInResult() {
        _checkInResult.value = null
    }

    // Reminders
    private val _remindedMemberIds = MutableStateFlow<Set<Long>>(emptySet())
    val remindedMemberIds: StateFlow<Set<Long>> = _remindedMemberIds.asStateFlow()

    fun sendReminder(member: MemberEntity) {
        val now = System.currentTimeMillis()
        val daysLeft = member.daysUntilExpiry(now)
        val text = if (daysLeft > 0) {
            "Reminder sent to ${member.name} (${member.phone}): Membership expires in $daysLeft day(s)!"
        } else {
            "Renewal notice sent to ${member.name} (${member.phone}): Membership has expired."
        }
        _remindedMemberIds.value = _remindedMemberIds.value + member.id
        showMessage("📱 $text")
    }

    // CRUD: Members
    fun saveMember(
        id: Long = 0,
        name: String,
        phone: String,
        email: String,
        avatarColorHex: String,
        plan: PlanEntity,
        startDate: Long,
        assignedTrainer: String?,
        notes: String,
        emergencyContact: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val initials = name.trim().split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .map { it.first().uppercaseChar() }
                .joinToString("")
                .ifEmpty { "ME" }

            val expiryDate = startDate + (plan.durationDays.toLong() * 24 * 60 * 60 * 1000)

            val member = MemberEntity(
                id = id,
                name = name,
                phone = phone,
                email = email,
                avatarInitials = initials,
                avatarColorHex = avatarColorHex,
                planId = plan.id,
                planName = plan.name,
                startDate = startDate,
                expiryDate = expiryDate,
                assignedTrainer = if (assignedTrainer.isNullOrBlank() || assignedTrainer == "None") null else assignedTrainer,
                notes = notes,
                emergencyContact = emergencyContact
            )

            val memberId = if (id == 0L) {
                repository.insertMember(member)
            } else {
                repository.updateMember(member)
                id
            }

            showMessage(if (id == 0L) "Member '$name' created successfully!" else "Member '$name' updated.")
            onSuccess(memberId)
        }
    }

    fun deleteMember(memberId: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteMemberById(memberId)
            showMessage("Member deleted from registry.")
            onDone()
        }
    }

    // CRUD: Plans
    fun savePlan(
        id: Long = 0,
        name: String,
        durationDays: Int,
        price: Double,
        description: String,
        colorHex: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val plan = PlanEntity(
                id = id,
                name = name,
                durationDays = durationDays,
                price = price,
                description = description,
                colorHex = colorHex
            )
            if (id == 0L) {
                repository.insertPlan(plan)
                showMessage("Plan '$name' added!")
            } else {
                repository.updatePlan(plan)
                showMessage("Plan '$name' updated!")
            }
            onSuccess()
        }
    }

    fun deletePlan(plan: PlanEntity) {
        viewModelScope.launch {
            repository.deletePlan(plan)
            showMessage("Plan '${plan.name}' removed.")
        }
    }

    // Record Payment
    fun recordPayment(
        member: MemberEntity,
        plan: PlanEntity,
        amount: Double,
        paymentDate: Long,
        paymentMethod: String,
        autoExtend: Boolean,
        notes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.recordPayment(
                memberId = member.id,
                memberName = member.name,
                planId = plan.id,
                planName = plan.name,
                amount = amount,
                paymentDate = paymentDate,
                paymentMethod = paymentMethod,
                extendedDays = plan.durationDays,
                notes = notes,
                autoExtendExpiry = autoExtend
            )
            showMessage("Payment of $$amount recorded for ${member.name}. Expiry extended by ${plan.durationDays} days!")
            onSuccess()
        }
    }

    init {
        viewModelScope.launch {
            repository.ensureDatabaseSeeded()
        }
    }

    class Factory(private val repository: FitAdminRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FitAdminViewModel::class.java)) {
                return FitAdminViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
