package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AddEditMemberScreen
import com.example.ui.screens.CheckInScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpiringSoonScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemberProfileScreen
import com.example.ui.screens.MembersListScreen
import com.example.ui.screens.PlansSetupScreen
import com.example.ui.screens.RecordPaymentScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElectricLimeDark
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.FitAdminViewModel

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object Members : Screen("members")
    data object AddEditMember : Screen("add_edit_member")
    data object MemberProfile : Screen("member_profile")
    data object Plans : Screen("plans")
    data object RecordPayment : Screen("record_payment")
    data object CheckIn : Screen("check_in")
    data object ExpiringSoon : Screen("expiring_soon")
}

data class NavigationTab(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun FitAdminApp(
    viewModel: FitAdminViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val notificationMessage by viewModel.notificationMessage.collectAsStateWithLifecycle()
    val expiringMembers by viewModel.membersExpiringSoon.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(notificationMessage) {
        notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    var screenHistory by remember { mutableStateOf(listOf<Screen>()) }

    var selectedMemberIdForProfile by remember { mutableStateOf<Long?>(null) }
    var memberIdToEdit by remember { mutableStateOf<Long?>(null) }
    var preselectedMemberIdForPayment by remember { mutableStateOf<Long?>(null) }

    fun navigateTo(screen: Screen) {
        screenHistory = screenHistory + currentScreen
        currentScreen = screen
    }

    fun navigateBack() {
        if (screenHistory.isNotEmpty()) {
            val previous = screenHistory.last()
            screenHistory = screenHistory.dropLast(1)
            currentScreen = previous
        } else {
            currentScreen = Screen.Dashboard
        }
    }

    if (!isLoggedIn) {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                currentScreen = Screen.Dashboard
                screenHistory = emptyList()
            },
            modifier = modifier
        )
        return
    }

    val tabs = listOf(
        NavigationTab(Screen.Dashboard, "Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
        NavigationTab(Screen.Members, "Members", Icons.Default.People, "nav_members"),
        NavigationTab(Screen.CheckIn, "Check-In", Icons.Default.FitnessCenter, "nav_checkin"),
        NavigationTab(Screen.ExpiringSoon, "Expiring", Icons.Default.NotificationsActive, "nav_expiring"),
        NavigationTab(Screen.Plans, "Plans", Icons.Default.CardMembership, "nav_plans")
    )

    val isTopLevelScreen = currentScreen in listOf(
        Screen.Dashboard,
        Screen.Members,
        Screen.CheckIn,
        Screen.ExpiringSoon,
        Screen.Plans
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isTopLevelScreen) {
                NavigationBar(
                    containerColor = CharcoalDark,
                    contentColor = Color.White
                ) {
                    tabs.forEach { tab ->
                        val isSelected = currentScreen == tab.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentScreen != tab.screen) {
                                    currentScreen = tab.screen
                                    screenHistory = emptyList()
                                }
                            },
                            icon = {
                                if (tab.screen == Screen.ExpiringSoon && expiringMembers.isNotEmpty()) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = WarningAmber,
                                                contentColor = CharcoalDark
                                            ) {
                                                Text(
                                                    text = "${expiringMembers.size}",
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            tint = if (isSelected) ElectricLime else Color(0xFF9CA3AF)
                                        )
                                    }
                                } else if (tab.screen == Screen.CheckIn) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) ElectricLime else CharcoalSurface),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            tint = if (isSelected) CharcoalDark else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        tint = if (isSelected) ElectricLime else Color(0xFF9CA3AF)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                        fontSize = 10.sp,
                                        color = if (isSelected) ElectricLime else Color(0xFF9CA3AF)
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = CharcoalSurface
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { screen ->
            when (screen) {
                Screen.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { currentScreen = Screen.Dashboard }
                    )
                }

                Screen.Dashboard -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToCheckIn = { currentScreen = Screen.CheckIn },
                        onNavigateToMembers = { currentScreen = Screen.Members },
                        onNavigateToAddMember = {
                            memberIdToEdit = null
                            navigateTo(Screen.AddEditMember)
                        },
                        onNavigateToRecordPayment = {
                            preselectedMemberIdForPayment = null
                            navigateTo(Screen.RecordPayment)
                        },
                        onNavigateToExpiring = { currentScreen = Screen.ExpiringSoon },
                        onNavigateToPlans = { currentScreen = Screen.Plans },
                        onLogout = { viewModel.logout() }
                    )
                }

                Screen.Members -> {
                    MembersListScreen(
                        viewModel = viewModel,
                        onNavigateToAddMember = {
                            memberIdToEdit = null
                            navigateTo(Screen.AddEditMember)
                        },
                        onMemberSelected = { memberId ->
                            selectedMemberIdForProfile = memberId
                            viewModel.selectMember(memberId)
                            navigateTo(Screen.MemberProfile)
                        },
                        onQuickRenew = { member ->
                            preselectedMemberIdForPayment = member.id
                            navigateTo(Screen.RecordPayment)
                        }
                    )
                }

                Screen.AddEditMember -> {
                    AddEditMemberScreen(
                        viewModel = viewModel,
                        memberIdToEdit = memberIdToEdit,
                        onNavigateBack = { navigateBack() }
                    )
                }

                Screen.MemberProfile -> {
                    val id = selectedMemberIdForProfile ?: 1L
                    MemberProfileScreen(
                        viewModel = viewModel,
                        memberId = id,
                        onNavigateBack = { navigateBack() },
                        onNavigateToEdit = { editId ->
                            memberIdToEdit = editId
                            navigateTo(Screen.AddEditMember)
                        },
                        onRenewPayment = { member ->
                            preselectedMemberIdForPayment = member.id
                            navigateTo(Screen.RecordPayment)
                        }
                    )
                }

                Screen.Plans -> {
                    PlansSetupScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (screenHistory.isNotEmpty()) navigateBack()
                            else currentScreen = Screen.Dashboard
                        }
                    )
                }

                Screen.RecordPayment -> {
                    RecordPaymentScreen(
                        viewModel = viewModel,
                        preSelectedMemberId = preselectedMemberIdForPayment,
                        onNavigateBack = { navigateBack() }
                    )
                }

                Screen.CheckIn -> {
                    CheckInScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (screenHistory.isNotEmpty()) navigateBack()
                            else currentScreen = Screen.Dashboard
                        },
                        onRenewMember = { member ->
                            preselectedMemberIdForPayment = member.id
                            navigateTo(Screen.RecordPayment)
                        }
                    )
                }

                Screen.ExpiringSoon -> {
                    ExpiringSoonScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (screenHistory.isNotEmpty()) navigateBack()
                            else currentScreen = Screen.Dashboard
                        },
                        onRenewMember = { member ->
                            preselectedMemberIdForPayment = member.id
                            navigateTo(Screen.RecordPayment)
                        }
                    )
                }
            }
        }
    }
}
