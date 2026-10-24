package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.MemberEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatAppDate
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElectricLimeDark
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.FitAdminViewModel
import com.example.ui.viewmodel.MemberFilter
import com.example.ui.viewmodel.MemberSort

@Composable
fun MembersListScreen(
    viewModel: FitAdminViewModel,
    onNavigateToAddMember: () -> Unit,
    onMemberSelected: (Long) -> Unit,
    onQuickRenew: (MemberEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val members by viewModel.filteredMembers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()

    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddMember,
                containerColor = ElectricLime,
                contentColor = CharcoalDark,
                modifier = Modifier.testTag("add_member_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Member")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "NEW MEMBER",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Bar with Search & Sort
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GYM MEMBERS REGISTRY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Members (${members.size})",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = CharcoalDark
                            )
                        )
                    }

                    // Sort Button with dropdown
                    Box {
                        Button(
                            onClick = { showSortMenu = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF3F4F6),
                                contentColor = CharcoalDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("member_sort_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (selectedSort) {
                                    MemberSort.EXPIRY_DATE -> "Expiry"
                                    MemberSort.NAME_ASC -> "Name"
                                    MemberSort.RECENT_CHECKIN -> "Recent"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort by Expiry Date") },
                                onClick = {
                                    viewModel.setSort(MemberSort.EXPIRY_DATE)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Name (A-Z)") },
                                onClick = {
                                    viewModel.setSort(MemberSort.NAME_ASC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Recent Check-in") },
                                onClick = {
                                    viewModel.setSort(MemberSort.RECENT_CHECKIN)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by member name, phone, or plan...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
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
                        .testTag("member_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipButton(
                        label = "All Members",
                        isSelected = selectedFilter == MemberFilter.ALL,
                        onClick = { viewModel.setFilter(MemberFilter.ALL) },
                        modifier = Modifier.testTag("filter_all")
                    )
                    FilterChipButton(
                        label = "Active",
                        isSelected = selectedFilter == MemberFilter.ACTIVE,
                        activeColor = ElectricLime,
                        onClick = { viewModel.setFilter(MemberFilter.ACTIVE) },
                        modifier = Modifier.testTag("filter_active")
                    )
                    FilterChipButton(
                        label = "Expiring Soon",
                        isSelected = selectedFilter == MemberFilter.EXPIRING_SOON,
                        activeColor = WarningAmber,
                        onClick = { viewModel.setFilter(MemberFilter.EXPIRING_SOON) },
                        modifier = Modifier.testTag("filter_expiring")
                    )
                    FilterChipButton(
                        label = "Expired",
                        isSelected = selectedFilter == MemberFilter.EXPIRED,
                        activeColor = AlertRed,
                        onClick = { viewModel.setFilter(MemberFilter.EXPIRED) },
                        modifier = Modifier.testTag("filter_expired")
                    )
                }
            }

            // Data Table Header Row (Data-table + card hybrid feel)
            Surface(
                color = Color(0xFFE5E7EB),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MEMBER & PLAN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B5563),
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.weight(1.8f)
                    )
                    Text(
                        text = "EXPIRY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B5563),
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B5563),
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.width(76.dp)
                    )
                }
            }

            // Members List Rows
            if (members.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No members match criteria",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalDark
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing filters or search query",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(members, key = { it.id }) { member ->
                        MemberTableRow(
                            member = member,
                            onClick = { onMemberSelected(member.id) },
                            onCheckIn = { viewModel.processCheckIn(member) },
                            onRenew = { onQuickRenew(member) },
                            modifier = Modifier.testTag("member_row_${member.id}")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color = CharcoalDark,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) activeColor else Color(0xFFF3F4F6),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)) else null
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                color = if (isSelected) {
                    if (activeColor == ElectricLime) CharcoalDark else Color.White
                } else Color(0xFF4B5563)
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun MemberTableRow(
    member: MemberEntity,
    onClick: () -> Unit,
    onCheckIn: () -> Unit,
    onRenew: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val isExpired = member.isExpired(now)
    val daysLeft = member.daysUntilExpiry(now)

    Surface(
        onClick = onClick,
        color = SurfaceWhite,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo/Avatar
                MemberAvatar(
                    initials = member.avatarInitials,
                    colorHex = member.avatarColorHex,
                    sizeDp = 42
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Plan column
                Column(modifier = Modifier.weight(1.8f)) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = member.planName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4B5563)
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = member.phone,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        ),
                        maxLines = 1
                    )
                }

                // Expiry Date Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatAppDate(member.expiryDate),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    )
                    Text(
                        text = when {
                            isExpired -> "Expired ${Math.abs(daysLeft)}d ago"
                            daysLeft == 0L -> "Expires today"
                            else -> "in ${daysLeft}d"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = if (isExpired) AlertRed else if (daysLeft <= 7) WarningAmber else Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Status Badge & Quick Actions
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(76.dp)
                ) {
                    StatusBadge(
                        isExpired = isExpired,
                        daysUntilExpiry = daysLeft
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        IconButton(
                            onClick = onCheckIn,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = "Check In",
                                tint = if (isExpired) Color.Gray else ElectricLimeDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onRenew,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = "Renew",
                                tint = CharcoalDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF3F4F6))
            )
        }
    }
}
