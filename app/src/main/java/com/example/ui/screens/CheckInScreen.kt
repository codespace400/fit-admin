package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.repository.CheckInResult
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatusBadge
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    viewModel: FitAdminViewModel,
    onNavigateBack: () -> Unit,
    onRenewMember: (MemberEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()
    val checkInsToday by viewModel.checkInsToday.collectAsStateWithLifecycle()
    val checkInResult by viewModel.checkInResult.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isCheckInProcessing.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    var selectedMember by remember { mutableStateOf<MemberEntity?>(null) }
    var simIndex by remember { mutableIntStateOf(0) }

    val filteredMembers = remember(query, allMembers) {
        if (query.isBlank()) emptyList()
        else allMembers.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.phone.contains(query) ||
            it.planName.contains(query, ignoreCase = true)
        }.take(4)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Check-In Terminal",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = CharcoalDark
                            )
                        )
                        Text(
                            text = "Front Desk Access Control & Scan",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CharcoalDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Scanner & Search Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SEARCH OR SCAN MEMBER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalDark
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            // Keyfob scan simulator button
                            Button(
                                onClick = {
                                    if (allMembers.isNotEmpty()) {
                                        val m = allMembers[simIndex % allMembers.size]
                                        simIndex++
                                        selectedMember = m
                                        query = m.name
                                        viewModel.clearCheckInResult()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CharcoalDark,
                                    contentColor = ElectricLime
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("simulate_rfid_scan_button")
                            ) {
                                Icon(Icons.Default.Nfc, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SIMULATE SCAN", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black))
                            }
                        }

                        OutlinedTextField(
                            value = query,
                            onValueChange = {
                                query = it
                                if (selectedMember != null && it != selectedMember?.name) {
                                    selectedMember = null
                                    viewModel.clearCheckInResult()
                                }
                            },
                            placeholder = { Text("Type member name or phone...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = CharcoalDark)
                            },
                            trailingIcon = {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = {
                                        query = ""
                                        selectedMember = null
                                        viewModel.clearCheckInResult()
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CharcoalDark,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkin_search_input")
                        )

                        // Dropdown autocomplete search suggestions
                        if (selectedMember == null && filteredMembers.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF9FAFB))
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                            ) {
                                filteredMembers.forEach { member ->
                                    val now = System.currentTimeMillis()
                                    val isExpired = member.isExpired(now)
                                    val daysLeft = member.daysUntilExpiry(now)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedMember = member
                                                query = member.name
                                                viewModel.clearCheckInResult()
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        MemberAvatar(initials = member.avatarInitials, colorHex = member.avatarColorHex, sizeDp = 32)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(member.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                            Text("${member.planName} · ${member.phone}", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                                        }
                                        StatusBadge(isExpired = isExpired, daysUntilExpiry = daysLeft)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Active Member Card / Check-In Action Section
            if (selectedMember != null) {
                val member = selectedMember!!
                val now = System.currentTimeMillis()
                val isExpired = member.isExpired(now)
                val daysLeft = member.daysUntilExpiry(now)

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpired) AlertRedContainer else Color(0xFFF0FDF4)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (isExpired) AlertRed else ElectricLime
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MemberAvatar(
                                initials = member.avatarInitials,
                                colorHex = member.avatarColorHex,
                                sizeDp = 64
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CharcoalDark
                                )
                            )

                            Text(
                                text = "${member.planName} · Phone: ${member.phone}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF4B5563))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            StatusBadge(isExpired = isExpired, daysUntilExpiry = daysLeft)

                            Spacer(modifier = Modifier.height(16.dp))

                            if (isExpired) {
                                // Block with warning
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AlertRed,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Block,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "⛔ ACCESS DENIED: MEMBERSHIP EXPIRED",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            )
                                            Text(
                                                text = "Expired on ${formatAppDate(member.expiryDate)}. Turnstile entry is blocked.",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { onRenewMember(member) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CharcoalDark,
                                        contentColor = ElectricLime
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("checkin_renew_button")
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "RENEW MEMBERSHIP NOW",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                                    )
                                }
                            } else {
                                // Valid Active Member: One-tap Check-In Button
                                Button(
                                    onClick = { viewModel.processCheckIn(member) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ElectricLime,
                                        contentColor = CharcoalDark
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .testTag("one_tap_checkin_button")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "APPROVE ENTRY (ONE-TAP CHECK-IN)",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Animated Result Feedback Overlay Card
            if (checkInResult != null) {
                item {
                    val res = checkInResult!!
                    val isSuccess = res is CheckInResult.Success

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(250)) + scaleIn(tween(250)),
                        exit = fadeOut(tween(200)) + scaleOut(tween(200))
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSuccess) CharcoalDark else AlertRed
                            )
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
                                        .clip(CircleShape)
                                        .background(if (isSuccess) ElectricLime else Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Error,
                                        contentDescription = null,
                                        tint = if (isSuccess) CharcoalDark else AlertRed,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isSuccess) "ACCESS GRANTED" else "ENTRY REJECTED",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = if (isSuccess) ElectricLime else Color.White
                                        )
                                    )
                                    Text(
                                        text = when (res) {
                                            is CheckInResult.Success -> res.message
                                            is CheckInResult.Expired -> res.message
                                            is CheckInResult.NotFound -> "Member not found"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                                    )
                                }

                                IconButton(onClick = { viewModel.clearCheckInResult() }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Dismiss", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Today's Check-ins Log Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAY'S FRONT-DESK LOG",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    )
                    Text(
                        text = "${checkInsToday.size} Entries",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            }

            items(checkInsToday, key = { it.id }) { checkIn ->
                CheckInFeedItem(checkIn = checkIn)
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
