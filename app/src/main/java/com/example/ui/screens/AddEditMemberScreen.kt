package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.MemberEntity
import com.example.data.entity.PlanEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.components.formatAppDate
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.SurfaceWhite
import com.example.ui.viewmodel.FitAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemberScreen(
    viewModel: FitAdminViewModel,
    memberIdToEdit: Long? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val allPlans by viewModel.allPlans.collectAsStateWithLifecycle()
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()

    val memberToEdit = remember(memberIdToEdit, allMembers) {
        if (memberIdToEdit != null) allMembers.find { it.id == memberIdToEdit } else null
    }

    var name by remember { mutableStateOf(memberToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(memberToEdit?.phone ?: "") }
    var email by remember { mutableStateOf(memberToEdit?.email ?: "") }
    var emergencyContact by remember { mutableStateOf(memberToEdit?.emergencyContact ?: "") }
    var notes by remember { mutableStateOf(memberToEdit?.notes ?: "") }
    var assignedTrainer by remember { mutableStateOf(memberToEdit?.assignedTrainer ?: "None") }
    var startDate by remember { mutableLongStateOf(memberToEdit?.startDate ?: System.currentTimeMillis()) }

    val avatarColors = listOf("#84CC16", "#38BDF8", "#F59E0B", "#A855F7", "#EF4444", "#10B981")
    var selectedColorHex by remember { mutableStateOf(memberToEdit?.avatarColorHex ?: avatarColors[0]) }

    var selectedPlan by remember {
        mutableStateOf<PlanEntity?>(null)
    }

    LaunchedEffect(allPlans, memberToEdit) {
        if (allPlans.isNotEmpty() && selectedPlan == null) {
            selectedPlan = if (memberToEdit != null) {
                allPlans.find { it.id == memberToEdit.planId } ?: allPlans.first()
            } else {
                allPlans.first()
            }
        }
    }

    var trainerExpanded by remember { mutableStateOf(false) }
    val trainerOptions = listOf("None", "Coach Leo", "Coach Sarah", "Coach Marcus", "Coach Tyler")

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (memberToEdit == null) "Add New Member" else "Edit Member Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = CharcoalDark
                        )
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar Preview and Color Picker
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val previewInitials = name.trim().split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .map { it.first().uppercaseChar() }
                        .joinToString("")
                        .ifEmpty { "ME" }

                    MemberAvatar(
                        initials = previewInitials,
                        colorHex = selectedColorHex,
                        sizeDp = 64
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "AVATAR THEME COLOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            avatarColors.forEach { hex ->
                                val color = Color(android.graphics.Color.parseColor(hex))
                                val isSelected = hex == selectedColorHex
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { selectedColorHex = hex }
                                        .then(
                                            if (isSelected) Modifier.border(2.dp, CharcoalDark, CircleShape)
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = CharcoalDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Basic Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "MEMBER INFORMATION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        label = { Text("Full Name *") },
                        placeholder = { Text("e.g. Marcus Rodriguez") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = CharcoalDark)
                        },
                        isError = nameError,
                        supportingText = if (nameError) {
                            { Text("Member name is required", color = AlertRed) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_name_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            phoneError = false
                        },
                        label = { Text("Phone Number *") },
                        placeholder = { Text("e.g. +1 (555) 234-8901") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = CharcoalDark)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phoneError,
                        supportingText = if (phoneError) {
                            { Text("Phone number is required", color = AlertRed) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_phone_input")
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("e.g. marcus@example.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = CharcoalDark)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_email_input")
                    )

                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        label = { Text("Emergency Contact (Name & Phone)") },
                        placeholder = { Text("e.g. Elena Rodriguez +1 (555) 234-8902") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_emergency_input")
                    )
                }
            }

            // Membership Plan Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SELECT MEMBERSHIP PLAN *",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    )

                    allPlans.forEach { plan ->
                        val isSelected = selectedPlan?.id == plan.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlan = plan },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF9FAFB),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) ElectricLime else Color(0xFFE5E7EB)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, if (isSelected) ElectricLime else Color.Gray, CircleShape)
                                        .background(if (isSelected) ElectricLime else Color.Transparent)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = plan.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalDark
                                        )
                                    )
                                    Text(
                                        text = "${plan.durationDays} Days · ${plan.description}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                        maxLines = 1
                                    )
                                }

                                Text(
                                    text = "$${plan.price}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = CharcoalDark
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Start Date & Expiry Calculation preview
                    val planDays = selectedPlan?.durationDays ?: 30
                    val calculatedExpiry = startDate + (planDays.toLong() * 24 * 60 * 60 * 1000)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF3F4F6)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = CharcoalDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Start Date: ${formatAppDate(startDate)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Valid Until: ${formatAppDate(calculatedExpiry)} (+${planDays} days)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ElectricLime,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Assigned Trainer & Notes Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ADDITIONAL DETAILS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    )

                    // Trainer Selector
                    ExposedDropdownMenuBox(
                        expanded = trainerExpanded,
                        onExpandedChange = { trainerExpanded = !trainerExpanded }
                    ) {
                        OutlinedTextField(
                            value = assignedTrainer,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assigned Trainer (Optional)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = trainerExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("member_trainer_input")
                        )
                        ExposedDropdownMenu(
                            expanded = trainerExpanded,
                            onDismissRequest = { trainerExpanded = false }
                        ) {
                            trainerOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        assignedTrainer = option
                                        trainerExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Admin & Desk Notes") },
                        placeholder = { Text("Medical notes, workout goals, access preferences...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_notes_input")
                    )
                }
            }

            // Save & Cancel Actions
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    if (phone.isBlank()) {
                        phoneError = true
                        return@Button
                    }
                    val plan = selectedPlan ?: allPlans.firstOrNull() ?: return@Button

                    viewModel.saveMember(
                        id = memberToEdit?.id ?: 0L,
                        name = name.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        avatarColorHex = selectedColorHex,
                        plan = plan,
                        startDate = startDate,
                        assignedTrainer = assignedTrainer,
                        notes = notes.trim(),
                        emergencyContact = emergencyContact.trim(),
                        onSuccess = { onNavigateBack() }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CharcoalDark,
                    contentColor = ElectricLime
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_member_button")
            ) {
                Text(
                    text = if (memberToEdit == null) "SAVE & ACTIVATE MEMBER" else "UPDATE MEMBER PROFILE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
