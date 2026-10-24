package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.CheckInEntity
import com.example.ui.components.StatCard
import com.example.ui.components.formatAppDate
import com.example.ui.components.formatAppTime
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.AlertRedDark
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElectricLimeContainer
import com.example.ui.theme.ElectricLimeDark
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.FitAdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: FitAdminViewModel,
    onNavigateToCheckIn: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onNavigateToAddMember: () -> Unit,
    onNavigateToRecordPayment: () -> Unit,
    onNavigateToExpiring: () -> Unit,
    onNavigateToPlans: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val checkInsToday by viewModel.checkInsToday.collectAsStateWithLifecycle()
    val currentStaff by viewModel.currentStaff.collectAsStateWithLifecycle()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    val todayDateFormatted = formatAppDate(System.currentTimeMillis())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Gym Desk Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalSurface)
                            .border(1.5.dp, ElectricLime, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = ElectricLime,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ElectricLime)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SHIFT ACTIVE · $todayDateFormatted",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = ElectricLime
                                )
                            )
                        }
                        Text(
                            text = currentStaff,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1
                        )
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Switch shift / Logout",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }

        // 2. Prominent Quick Check-In Banner Button
        item {
            Card(
                onClick = onNavigateToCheckIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_checkin_button"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ElectricLime)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(CharcoalDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Check In",
                            tint = ElectricLime,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "QUICK MEMBER CHECK-IN",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = CharcoalDark,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "Search member or scan keyfob RFID / QR",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = CharcoalDark.copy(alpha = 0.8f)
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CharcoalDark
                    ) {
                        Text(
                            text = "OPEN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = ElectricLime
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 3. Stat Cards 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Active Members",
                        value = "${stats.activeMembersCount}",
                        subtitle = "Valid gym passes",
                        icon = Icons.Default.CardMembership,
                        iconColor = ElectricLime,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_active_members"),
                        onClick = onNavigateToMembers
                    )

                    StatCard(
                        title = "Expiring This Week",
                        value = "${stats.expiringThisWeekCount}",
                        subtitle = "Needs follow up",
                        icon = Icons.Default.NotificationsActive,
                        iconColor = WarningAmber,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_expiring_members"),
                        onClick = onNavigateToExpiring
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Revenue (This Month)",
                        value = currencyFormat.format(stats.revenueThisMonth),
                        subtitle = "Dues & renewals",
                        icon = Icons.Default.AttachMoney,
                        iconColor = Color(0xFF10B981),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_revenue"),
                        onClick = onNavigateToRecordPayment
                    )

                    StatCard(
                        title = "Check-Ins Today",
                        value = "${stats.checkInsTodayCount}",
                        subtitle = "Live floor count",
                        icon = Icons.Default.FitnessCenter,
                        iconColor = Color(0xFF0EA5E9),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_checkins_today"),
                        onClick = onNavigateToCheckIn
                    )
                }
            }
        }

        // 4. Quick Desk Actions Row
        item {
            Text(
                text = "FRONT-DESK ACTIONS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    label = "+ Add Member",
                    onClick = onNavigateToAddMember,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_add_member")
                )
                QuickActionButton(
                    icon = Icons.Default.Payment,
                    label = "Record Pay",
                    onClick = onNavigateToRecordPayment,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_record_payment")
                )
                QuickActionButton(
                    icon = Icons.Default.EventNote,
                    label = "Plans Setup",
                    onClick = onNavigateToPlans,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_plans_setup")
                )
            }
        }

        // 5. Today's Front Desk Activity Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S ENTRY LOG",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Text(
                    text = "${checkInsToday.size} recorded",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // 6. Check-ins items list
        if (checkInsToday.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No check-ins yet today. Tap 'Quick Check-In' above to scan first member.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                }
            }
        } else {
            items(checkInsToday.take(6)) { checkIn ->
                CheckInFeedItem(checkIn = checkIn)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CharcoalDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = CharcoalDark
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun CheckInFeedItem(checkIn: CheckInEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (checkIn.isAllowed) ElectricLime else AlertRed)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = checkIn.memberName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark
                    )
                )
                Text(
                    text = "${checkIn.planName} · ${checkIn.statusReason}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (checkIn.isAllowed) Color(0xFF4B5563) else AlertRedDark
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (checkIn.isAllowed) ElectricLimeContainer else AlertRedContainer
            ) {
                Text(
                    text = formatAppTime(checkIn.checkInTime),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (checkIn.isAllowed) ElectricLimeDark else AlertRedDark
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
