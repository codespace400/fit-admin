package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun RecordPaymentScreen(
    viewModel: FitAdminViewModel,
    preSelectedMemberId: Long? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()
    val allPlans by viewModel.allPlans.collectAsStateWithLifecycle()

    var selectedMember by remember {
        mutableStateOf(allMembers.find { it.id == preSelectedMemberId })
    }

    var selectedPlan by remember {
        mutableStateOf(allPlans.firstOrNull())
    }

    var amount by remember { mutableStateOf("59.99") }
    var paymentDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var autoExtendExpiry by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    val paymentMethods = listOf("POS Terminal", "Credit Card", "Cash", "Bank Transfer", "Mobile Pay")
    var selectedMethod by remember { mutableStateOf(paymentMethods[0]) }

    var memberDropdownExpanded by remember { mutableStateOf(false) }
    var memberSearchFilter by remember { mutableStateOf("") }

    LaunchedEffect(preSelectedMemberId, allMembers) {
        if (selectedMember == null && preSelectedMemberId != null) {
            val found = allMembers.find { it.id == preSelectedMemberId }
            if (found != null) {
                selectedMember = found
            }
        }
    }

    LaunchedEffect(selectedMember, allPlans) {
        if (selectedMember != null && allPlans.isNotEmpty()) {
            val matchedPlan = allPlans.find { it.id == selectedMember?.planId } ?: allPlans.first()
            selectedPlan = matchedPlan
            amount = matchedPlan.price.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Record Payment & Dues", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CharcoalDark))
                        Text("Membership renewals & transactions", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CharcoalDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
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
            // 1. Member Selection Card
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
                        text = "SELECT GYM MEMBER *",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = memberDropdownExpanded,
                        onExpandedChange = { memberDropdownExpanded = !memberDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedMember?.name ?: memberSearchFilter,
                            onValueChange = {
                                memberSearchFilter = it
                                memberDropdownExpanded = true
                            },
                            readOnly = selectedMember != null,
                            placeholder = { Text("Search or select member...") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = CharcoalDark)
                            },
                            trailingIcon = {
                                if (selectedMember != null) {
                                    TextButton(onClick = {
                                        selectedMember = null
                                        memberSearchFilter = ""
                                    }) {
                                        Text("Change", style = MaterialTheme.typography.labelSmall)
                                    }
                                } else {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberDropdownExpanded)
                                }
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("payment_member_search_input")
                        )

                        val filteredList = allMembers.filter {
                            memberSearchFilter.isBlank() || it.name.contains(memberSearchFilter, ignoreCase = true) || it.phone.contains(memberSearchFilter)
                        }

                        ExposedDropdownMenu(
                            expanded = memberDropdownExpanded,
                            onDismissRequest = { memberDropdownExpanded = false }
                        ) {
                            filteredList.forEach { m ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            MemberAvatar(initials = m.avatarInitials, colorHex = m.avatarColorHex, sizeDp = 28)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(m.name, fontWeight = FontWeight.Bold)
                                                Text("${m.planName} · ${m.phone}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedMember = m
                                        val matched = allPlans.find { it.id == m.planId } ?: allPlans.firstOrNull()
                                        selectedPlan = matched
                                        if (matched != null) amount = matched.price.toString()
                                        memberDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedMember != null) {
                        val m = selectedMember!!
                        val now = System.currentTimeMillis()
                        val isExpired = m.isExpired(now)
                        val daysLeft = m.daysUntilExpiry(now)

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF3F4F6)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MemberAvatar(initials = m.avatarInitials, colorHex = m.avatarColorHex, sizeDp = 36)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(m.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(
                                        text = "Current Expiry: ${formatAppDate(m.expiryDate)} (${if (isExpired) "Expired" else "${daysLeft}d left"})",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isExpired) AlertRed else ElectricLime,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Plan Selection & Amount
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
                        text = "PLAN & PAYMENT AMOUNT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    )

                    Text("Select Renewal Plan:", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))

                    allPlans.forEach { plan ->
                        val isSelected = selectedPlan?.id == plan.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlan = plan
                                    amount = plan.price.toString()
                                },
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
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = plan.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalDark
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "+${plan.durationDays} Days · $${plan.price}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = CharcoalDark
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount Paid ($) *") },
                        leadingIcon = {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = CharcoalDark)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_amount_input")
                    )
                }
            }

            // 3. Payment Method & Date
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
                        text = "PAYMENT METHOD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        paymentMethods.take(3).forEach { method ->
                            MethodChip(
                                method = method,
                                isSelected = selectedMethod == method,
                                onSelect = { selectedMethod = method },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        paymentMethods.drop(3).forEach { method ->
                            MethodChip(
                                method = method,
                                isSelected = selectedMethod == method,
                                onSelect = { selectedMethod = method },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Transaction Notes / Reference") },
                        placeholder = { Text("e.g. Card terminal approval #89021") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 4. Auto-Extend Expiry Date Switch & Preview
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Extend Expiry Date",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalDark
                                )
                            )
                            Text(
                                text = "Automatically updates member validity upon saving payment",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }

                        Switch(
                            checked = autoExtendExpiry,
                            onCheckedChange = { autoExtendExpiry = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ElectricLime
                            )
                        )
                    }

                    if (autoExtendExpiry && selectedMember != null && selectedPlan != null) {
                        val m = selectedMember!!
                        val plan = selectedPlan!!
                        val now = System.currentTimeMillis()
                        val baseTime = if (m.expiryDate < now) now else m.expiryDate
                        val newExpiry = baseTime + (plan.durationDays.toLong() * 24 * 60 * 60 * 1000)

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricLime)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "EXPIRY DATE EXTENSION PREVIEW:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Current: ${formatAppDate(m.expiryDate)}  ➜  New: ${formatAppDate(newExpiry)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = CharcoalDark
                                    )
                                )
                                Text(
                                    text = "Valid for +${plan.durationDays} days",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ElectricLime)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Save Button
            Button(
                onClick = {
                    val m = selectedMember ?: return@Button
                    val p = selectedPlan ?: return@Button
                    val amountVal = amount.toDoubleOrNull() ?: p.price

                    viewModel.recordPayment(
                        member = m,
                        plan = p,
                        amount = amountVal,
                        paymentDate = paymentDate,
                        paymentMethod = selectedMethod,
                        autoExtend = autoExtendExpiry,
                        notes = notes.trim(),
                        onSuccess = { onNavigateBack() }
                    )
                },
                enabled = selectedMember != null && selectedPlan != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CharcoalDark,
                    contentColor = ElectricLime
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_payment_button")
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SAVE PAYMENT & ISSUE RECEIPT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MethodChip(
    method: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onSelect() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CharcoalDark else Color(0xFFF3F4F6),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CharcoalDark) else null
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = method,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    color = if (isSelected) ElectricLime else Color(0xFF4B5563),
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}
