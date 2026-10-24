package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CheckInDao
import com.example.data.dao.MemberDao
import com.example.data.dao.PaymentDao
import com.example.data.dao.PlanDao
import com.example.data.entity.CheckInEntity
import com.example.data.entity.MemberEntity
import com.example.data.entity.PaymentEntity
import com.example.data.entity.PlanEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MemberEntity::class,
        PlanEntity::class,
        PaymentEntity::class,
        CheckInEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun planDao(): PlanDao
    abstract fun paymentDao(): PaymentDao
    abstract fun checkInDao(): CheckInDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitadmin_database.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val now = System.currentTimeMillis()
            val dayMs = 24L * 60 * 60 * 1000

            // 1. Seed Plans
            val plans = listOf(
                PlanEntity(
                    id = 1,
                    name = "Pro Monthly",
                    durationDays = 30,
                    price = 59.99,
                    description = "Full gym floor, cardio zone & locker rooms",
                    colorHex = "#84CC16"
                ),
                PlanEntity(
                    id = 2,
                    name = "Gold Quarterly",
                    durationDays = 90,
                    price = 159.99,
                    description = "Gym + Sauna, Steam Room & 1 PT Session",
                    colorHex = "#38BDF8"
                ),
                PlanEntity(
                    id = 3,
                    name = "VIP Annual Elite",
                    durationDays = 365,
                    price = 499.99,
                    description = "All-Access 24/7, Towel Service, Unlimited Sauna & Guest Pass",
                    colorHex = "#F59E0B"
                ),
                PlanEntity(
                    id = 4,
                    name = "Student / Off-Peak",
                    durationDays = 30,
                    price = 39.99,
                    description = "Weekdays 6AM - 4PM access with valid ID",
                    colorHex = "#A855F7"
                ),
                PlanEntity(
                    id = 5,
                    name = "Day Pass",
                    durationDays = 1,
                    price = 15.00,
                    description = "Single-day drop in pass for visitors",
                    colorHex = "#9CA3AF"
                )
            )
            database.planDao().insertAll(plans)

            // 2. Seed Members
            val members = listOf(
                MemberEntity(
                    id = 1,
                    name = "Marcus Rodriguez",
                    phone = "+1 (555) 234-8901",
                    email = "marcus.r@example.com",
                    avatarInitials = "MR",
                    avatarColorHex = "#84CC16",
                    planId = 3,
                    planName = "VIP Annual Elite",
                    startDate = now - (90 * dayMs),
                    expiryDate = now + (275 * dayMs),
                    assignedTrainer = "Coach Leo",
                    notes = "Prep for powerlifting meet in November. Prefers morning workouts.",
                    emergencyContact = "Elena Rodriguez: +1 (555) 234-8902",
                    lastCheckInDate = now - (2 * 3600 * 1000),
                    totalCheckIns = 48
                ),
                MemberEntity(
                    id = 2,
                    name = "Elena Rostova",
                    phone = "+1 (555) 345-6789",
                    email = "elena.rostova@example.com",
                    avatarInitials = "ER",
                    avatarColorHex = "#F59E0B",
                    planId = 1,
                    planName = "Pro Monthly",
                    startDate = now - (27 * dayMs),
                    expiryDate = now + (3 * dayMs), // Expiring in 3 days!
                    assignedTrainer = "Coach Sarah",
                    notes = "Renewing on corporate reimbursement. Send reminder.",
                    emergencyContact = "Dmitri Rostova: +1 (555) 345-6780",
                    lastCheckInDate = now - (26 * 3600 * 1000),
                    totalCheckIns = 18
                ),
                MemberEntity(
                    id = 3,
                    name = "Devon Washington",
                    phone = "+1 (555) 456-7890",
                    email = "devon.w@example.com",
                    avatarInitials = "DW",
                    avatarColorHex = "#38BDF8",
                    planId = 2,
                    planName = "Gold Quarterly",
                    startDate = now - (85 * dayMs),
                    expiryDate = now + (5 * dayMs), // Expiring in 5 days!
                    assignedTrainer = null,
                    notes = "Card on file expired, reminder sent.",
                    emergencyContact = "Tanya Washington: +1 (555) 456-7899",
                    lastCheckInDate = now - (18 * 3600 * 1000),
                    totalCheckIns = 34
                ),
                MemberEntity(
                    id = 4,
                    name = "Chloe Dupont",
                    phone = "+1 (555) 567-8901",
                    email = "chloe.dupont@example.com",
                    avatarInitials = "CD",
                    avatarColorHex = "#EF4444",
                    planId = 1,
                    planName = "Pro Monthly",
                    startDate = now - (36 * dayMs),
                    expiryDate = now - (6 * dayMs), // Expired 6 days ago!
                    assignedTrainer = "Coach Leo",
                    notes = "Attempted check-in while expired. Advised to renew.",
                    emergencyContact = "Paul Dupont: +1 (555) 567-8905",
                    lastCheckInDate = now - (4 * 3600 * 1000),
                    totalCheckIns = 14
                ),
                MemberEntity(
                    id = 5,
                    name = "Aiden Vance",
                    phone = "+1 (555) 678-9012",
                    email = "aiden.vance@example.com",
                    avatarInitials = "AV",
                    avatarColorHex = "#84CC16",
                    planId = 2,
                    planName = "Gold Quarterly",
                    startDate = now - (45 * dayMs),
                    expiryDate = now + (45 * dayMs),
                    assignedTrainer = "Coach Sarah",
                    notes = "CrossFit athlete. Uses recovery sauna regularly.",
                    emergencyContact = "Claire Vance: +1 (555) 678-9019",
                    lastCheckInDate = now - (3 * 3600 * 1000),
                    totalCheckIns = 29
                ),
                MemberEntity(
                    id = 6,
                    name = "Maya Lin",
                    phone = "+1 (555) 789-0123",
                    email = "maya.lin@example.com",
                    avatarInitials = "ML",
                    avatarColorHex = "#A855F7",
                    planId = 4,
                    planName = "Student / Off-Peak",
                    startDate = now - (29 * dayMs),
                    expiryDate = now + (1 * dayMs), // Expiring tomorrow!
                    assignedTrainer = null,
                    notes = "Student discount verified at front desk.",
                    emergencyContact = "David Lin: +1 (555) 789-0129",
                    lastCheckInDate = now - (20 * 3600 * 1000),
                    totalCheckIns = 21
                ),
                MemberEntity(
                    id = 7,
                    name = "Jordan King",
                    phone = "+1 (555) 890-1234",
                    email = "jordan.k@example.com",
                    avatarInitials = "JK",
                    avatarColorHex = "#EF4444",
                    planId = 5,
                    planName = "Day Pass",
                    startDate = now - (12 * dayMs),
                    expiryDate = now - (11 * dayMs), // Expired!
                    assignedTrainer = null,
                    notes = "Guest drop in. Interested in monthly membership.",
                    emergencyContact = "Sarah King: +1 (555) 890-1230",
                    lastCheckInDate = now - (11 * dayMs),
                    totalCheckIns = 2
                ),
                MemberEntity(
                    id = 8,
                    name = "Sophia Morales",
                    phone = "+1 (555) 901-2345",
                    email = "sophia.m@example.com",
                    avatarInitials = "SM",
                    avatarColorHex = "#84CC16",
                    planId = 3,
                    planName = "VIP Annual Elite",
                    startDate = now - (100 * dayMs),
                    expiryDate = now + (265 * dayMs),
                    assignedTrainer = "Coach Leo",
                    notes = "Gym ambassador. Attends 6AM HIIT classes.",
                    emergencyContact = "Carlos Morales: +1 (555) 901-2340",
                    lastCheckInDate = now - (1 * 3600 * 1000),
                    totalCheckIns = 72
                )
            )
            database.memberDao().insertAll(members)

            // 3. Seed Payments
            val payments = listOf(
                PaymentEntity(
                    id = 1,
                    memberId = 1,
                    memberName = "Marcus Rodriguez",
                    planId = 3,
                    planName = "VIP Annual Elite",
                    amount = 499.99,
                    paymentDate = now - (15 * dayMs),
                    paymentMethod = "Credit Card",
                    extendedDays = 365,
                    receiptNumber = "REC-89011"
                ),
                PaymentEntity(
                    id = 2,
                    memberId = 2,
                    memberName = "Elena Rostova",
                    planId = 1,
                    planName = "Pro Monthly",
                    amount = 59.99,
                    paymentDate = now - (27 * dayMs),
                    paymentMethod = "POS Terminal",
                    extendedDays = 30,
                    receiptNumber = "REC-89012"
                ),
                PaymentEntity(
                    id = 3,
                    memberId = 3,
                    memberName = "Devon Washington",
                    planId = 2,
                    planName = "Gold Quarterly",
                    amount = 159.99,
                    paymentDate = now - (5 * dayMs),
                    paymentMethod = "Bank Transfer",
                    extendedDays = 90,
                    receiptNumber = "REC-89013"
                ),
                PaymentEntity(
                    id = 4,
                    memberId = 5,
                    memberName = "Aiden Vance",
                    planId = 2,
                    planName = "Gold Quarterly",
                    amount = 159.99,
                    paymentDate = now - (10 * dayMs),
                    paymentMethod = "Credit Card",
                    extendedDays = 90,
                    receiptNumber = "REC-89014"
                ),
                PaymentEntity(
                    id = 5,
                    memberId = 6,
                    memberName = "Maya Lin",
                    planId = 4,
                    planName = "Student / Off-Peak",
                    amount = 39.99,
                    paymentDate = now - (29 * dayMs),
                    paymentMethod = "Mobile Pay",
                    extendedDays = 30,
                    receiptNumber = "REC-89015"
                ),
                PaymentEntity(
                    id = 6,
                    memberId = 8,
                    memberName = "Sophia Morales",
                    planId = 3,
                    planName = "VIP Annual Elite",
                    amount = 499.99,
                    paymentDate = now - (8 * dayMs),
                    paymentMethod = "Credit Card",
                    extendedDays = 365,
                    receiptNumber = "REC-89016"
                )
            )
            database.paymentDao().insertAll(payments)

            // 4. Seed CheckIns
            val checkIns = listOf(
                CheckInEntity(
                    id = 1,
                    memberId = 8,
                    memberName = "Sophia Morales",
                    planName = "VIP Annual Elite",
                    checkInTime = now - (1 * 3600 * 1000),
                    isAllowed = true,
                    statusReason = "Access Granted - VIP Active",
                    deskStaff = "Front Desk Alex"
                ),
                CheckInEntity(
                    id = 2,
                    memberId = 1,
                    memberName = "Marcus Rodriguez",
                    planName = "VIP Annual Elite",
                    checkInTime = now - (2 * 3600 * 1000),
                    isAllowed = true,
                    statusReason = "Access Granted - VIP Active",
                    deskStaff = "Front Desk Alex"
                ),
                CheckInEntity(
                    id = 3,
                    memberId = 5,
                    memberName = "Aiden Vance",
                    planName = "Gold Quarterly",
                    checkInTime = now - (3 * 3600 * 1000),
                    isAllowed = true,
                    statusReason = "Access Granted - Active Member",
                    deskStaff = "Front Desk Alex"
                ),
                CheckInEntity(
                    id = 4,
                    memberId = 4,
                    memberName = "Chloe Dupont",
                    planName = "Pro Monthly",
                    checkInTime = now - (4 * 3600 * 1000),
                    isAllowed = false,
                    statusReason = "Access Denied - Expired 6 Days Ago",
                    deskStaff = "Front Desk Alex"
                ),
                CheckInEntity(
                    id = 5,
                    memberId = 2,
                    memberName = "Elena Rostova",
                    planName = "Pro Monthly",
                    checkInTime = now - (26 * 3600 * 1000),
                    isAllowed = true,
                    statusReason = "Access Granted - 3 Days Left",
                    deskStaff = "Sarah Connor (Manager)"
                )
            )
            database.checkInDao().insertAll(checkIns)
        }
    }
}
