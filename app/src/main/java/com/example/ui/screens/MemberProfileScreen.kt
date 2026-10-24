package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.MemberEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatAppDate
import com.example.ui.components.formatAppDateTime
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberProfileScreen(
    viewModel: FitAdminViewModel,
    memberId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onRenewPayment: (MemberEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val selectedMember by viewModel.selectedMember.collectAsStateWithLifecycle()
    val memberPayments by viewModel.selectedMemberPayments.collectAsStateWithLifecycle()
    val memberCheckIns by viewModel.selectedMemberCheckIns.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val member = selectedMember

    if (showDeleteDialog && member != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Member?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove ${member.name} from the gym registry? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteMember(member.id, onDone = onNavigateBack)
                    }
                ) {
                    Text("Delete", color = AlertRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Member Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (member != null) {
                        IconButton(
                            onClick = { onNavigateToEdit(member.id) },
                            modifier = Modifier.testTag("edit_member_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Member", tint = ElectricLime)
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.testTag("delete_member_button")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF87171))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CharcoalDark)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (member == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading member profile...", color = Color.Gray)
            }
        } else {
            val now = System.currentTimeMillis()
            val isExpired = member.isExpired(now)
            val daysLeft = member.daysUntilExpiry(now)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // 1. Header Hero Banner
                item {
                    Surface(
                        color = CharcoalDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MemberAvatar(
                                    initials = member.avatarInitials,
                                    colorHex = member.avatarColorHex,
                                    sizeDp = 72
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = member.name,
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = CharcoalSurface
                                        ) {
                                            Text(
                                                text = "ID #${member.id.toString().padStart(4, '0')}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElectricLime
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        StatusBadge(isExpired = isExpired, daysUntilExpiry = daysLeft)
                                    }

                                    if (member.assignedTrainer != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Trainer: ${member.assignedTrainer}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFD1D5DB),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Expiry Countdown & Progress Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpired) AlertRedContainer else SurfaceWhite
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "MEMBERSHIP STATUS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isExpired) AlertRedDark else Color(0xFF4B5563)
                                        )
                                    )
                                    Text(
                                        text = member.planName,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = CharcoalDark
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isExpired) AlertRed else ElectricLime
                                ) {
                                    Text(
                                        text = if (isExpired) "EXPIRED" else "${daysLeft} DAYS LEFT",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = if (isExpired) Color.White else CharcoalDark
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Progress Bar
                            val totalPlanDuration = Math.max(1L, member.expiryDate - member.startDate)
                            val elapsed = Math.min(totalPlanDuration, Math.max(0L, now - member.startDate))
                            val progress = (elapsed.toFloat() / totalPlanDuration.toFloat()).coerceIn(0f, 1f)

                            LinearProgressIndicator(
                                progress = { if (isExpired) 1f else (1f - progress) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (isExpired) AlertRed else ElectricLime,
                                trackColor = Color(0xFFE5E7EB)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Started: ${formatAppDate(member.startDate)}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4B5563))
                                )
                                Text(
                                    text = "Expires: ${formatAppDate(member.expiryDate)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isExpired) AlertRedDark else CharcoalDark
                                    )
                                )
                            }
                        }
                    }
                }

                // 3. Primary Action Buttons: "Renew Membership" and "Check In"
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onRenewPayment(member) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CharcoalDark,
                                contentColor = ElectricLime
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp)
                                .testTag("renew_membership_button")
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RENEW MEMBERSHIP",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                            )
                        }

                        Button(
                            onClick = { viewModel.processCheckIn(member) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isExpired) AlertRed else ElectricLime,
                                contentColor = if (isExpired) Color.White else CharcoalDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("profile_checkin_button")
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CHECK IN",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. Tab Navigation (Details, Payments, Check-Ins)
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SurfaceWhite,
                        contentColor = CharcoalDark
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Details", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Payments (${memberPayments.size})", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Check-Ins (${memberCheckIns.size})", fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                // Tab Contents
                when (selectedTab) {
                    0 -> {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DetailRowCard(
                                    icon = Icons.Default.Call,
                                    label = "Phone Number",
                                    value = member.phone
                                )
                                DetailRowCard(
                                    icon = Icons.Default.Email,
                                    label = "Email Address",
                                    value = member.email.ifBlank { "Not provided" }
                                )
                                DetailRowCard(
                                    icon = Icons.Default.Security,
                                    label = "Emergency Contact",
                                    value = member.emergencyContact.ifBlank { "None on file" }
                                )
                                DetailRowCard(
                                    icon = Icons.Default.Person,
                                    label = "Assigned Trainer",
                                    value = member.assignedTrainer ?: "None (General Access)"
                                )
                                DetailRowCard(
                                    icon = Icons.Default.FitnessCenter,
                                    label = "Total Lifetime Check-Ins",
                                    value = "${member.totalCheckIns} workouts completed"
                                )
                                if (member.notes.isNotBlank()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text(
                                                text = "ADMIN NOTES",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Gray)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = member.notes,
                                                style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalDark)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        if (memberPayments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No payment records on file for this member.", color = Color.Gray)
                                }
                            }
                        } else {
                            items(memberPayments) { payment ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    border = CardDefaults.outlinedCardBorder()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(ElectricLimeContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = ElectricLimeDark, modifier = Modifier.size(18.dp))
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = payment.planName,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalDark)
                                            )
                                            Text(
                                                text = "${formatAppDate(payment.paymentDate)} · ${payment.paymentMethod} · ${payment.receiptNumber}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                            )
                                        }

                                        Text(
                                            text = "$${payment.amount}",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = CharcoalDark)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        if (memberCheckIns.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No check-in entries recorded yet.", color = Color.Gray)
                                }
                            }
                        } else {
                            items(memberCheckIns) { checkIn ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
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
                                                text = formatAppDateTime(checkIn.checkInTime),
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = CharcoalDark)
                                            )
                                            Text(
                                                text = "${checkIn.statusReason} · Operator: ${checkIn.deskStaff}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun DetailRowCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CharcoalDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CharcoalDark)
                )
            }
        }
    }
}
