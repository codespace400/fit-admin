package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.entity.PlanEntity
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.SurfaceWhite
import com.example.ui.viewmodel.FitAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansSetupScreen(
    viewModel: FitAdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val allPlans by viewModel.allPlans.collectAsStateWithLifecycle()
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingPlan by remember { mutableStateOf<PlanEntity?>(null) }
    var planToDelete by remember { mutableStateOf<PlanEntity?>(null) }

    // Dialog state
    var planName by remember { mutableStateOf("") }
    var planDuration by remember { mutableStateOf("30") }
    var planPrice by remember { mutableStateOf("59.99") }
    var planDescription by remember { mutableStateOf("") }
    val colorOptions = listOf("#84CC16", "#38BDF8", "#F59E0B", "#A855F7", "#EF4444")
    var selectedColor by remember { mutableStateOf(colorOptions[0]) }

    fun openEdit(plan: PlanEntity?) {
        editingPlan = plan
        if (plan != null) {
            planName = plan.name
            planDuration = plan.durationDays.toString()
            planPrice = plan.price.toString()
            planDescription = plan.description
            selectedColor = plan.colorHex
        } else {
            planName = ""
            planDuration = "30"
            planPrice = "49.99"
            planDescription = "Full gym access"
            selectedColor = colorOptions[0]
        }
        showAddEditDialog = true
    }

    if (showAddEditDialog) {
        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = {
                Text(
                    text = if (editingPlan == null) "Create Membership Plan" else "Edit Plan",
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = planName,
                        onValueChange = { planName = it },
                        label = { Text("Plan Name *") },
                        placeholder = { Text("e.g. VIP Elite Annual") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("plan_name_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = planDuration,
                            onValueChange = { planDuration = it },
                            label = { Text("Duration (Days) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("plan_duration_input")
                        )

                        OutlinedTextField(
                            value = planPrice,
                            onValueChange = { planPrice = it },
                            label = { Text("Price ($) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("plan_price_input")
                        )
                    }

                    OutlinedTextField(
                        value = planDescription,
                        onValueChange = { planDescription = it },
                        label = { Text("Description & Perks") },
                        placeholder = { Text("Sauna, locker, classes...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("plan_desc_input")
                    )

                    Text(
                        text = "PLAN ACCENT COLOR",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Gray)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colorOptions.forEach { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = hex == selectedColor
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = hex }
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, CharcoalDark, CircleShape)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = CharcoalDark, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = planDuration.toIntOrNull() ?: 30
                        val price = planPrice.toDoubleOrNull() ?: 49.99
                        if (planName.isNotBlank()) {
                            viewModel.savePlan(
                                id = editingPlan?.id ?: 0L,
                                name = planName.trim(),
                                durationDays = duration,
                                price = price,
                                description = planDescription.trim(),
                                colorHex = selectedColor,
                                onSuccess = { showAddEditDialog = false }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark, contentColor = ElectricLime),
                    modifier = Modifier.testTag("save_plan_button")
                ) {
                    Text("SAVE PLAN", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (planToDelete != null) {
        val plan = planToDelete!!
        AlertDialog(
            onDismissRequest = { planToDelete = null },
            title = { Text("Delete Plan?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${plan.name}'? Existing members subscribed to this plan will remain valid.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlan(plan)
                        planToDelete = null
                    }
                ) {
                    Text("Delete", color = AlertRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { planToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Membership Plans", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CharcoalDark))
                        Text("Pricing tiers & durations", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openEdit(null) },
                containerColor = CharcoalDark,
                contentColor = ElectricLime,
                modifier = Modifier.testTag("add_plan_button")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add Plan")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NEW PLAN", fontWeight = FontWeight.Black)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allPlans, key = { it.id }) { plan ->
                val planColor = try {
                    Color(android.graphics.Color.parseColor(plan.colorHex))
                } catch (e: Exception) {
                    ElectricLime
                }

                val subscribersCount = allMembers.count { it.planId == plan.id }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(planColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = plan.name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CharcoalDark
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$${plan.price}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CharcoalDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = plan.description,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF4B5563))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF3F4F6)
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(
                                        text = "${plan.durationDays} Days",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CharcoalDark)
                                    )
                                    Text(
                                        text = " · $subscribersCount active members",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                                    )
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = { openEdit(plan) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CharcoalDark, modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = { planToDelete = plan },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
