package com.example.ui.screens

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

data class NavigationItem(
    val tab: AppTab,
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    val navItems = listOf(
        NavigationItem(AppTab.DASHBOARD, Icons.Default.Dashboard, "Dashboard"),
        NavigationItem(AppTab.STUDENTS, Icons.Default.Groups, "Students"),
        NavigationItem(AppTab.BATCHES, Icons.Default.Class, "Batches"),
        NavigationItem(AppTab.EXAMS, Icons.Default.Assignment, "Exams"),
        NavigationItem(AppTab.MARKS_ENTRY, Icons.Default.EditNote, "Marks Entry"),
        NavigationItem(AppTab.ATTENDANCE, Icons.Default.FactCheck, "Attendance"),
        NavigationItem(AppTab.FEES, Icons.Default.Payment, "Fees"),
        NavigationItem(AppTab.SETTINGS, Icons.Default.Tune, "Settings")
    )

    // Primary 5 tabs for bottom navigation
    val bottomNavItems = listOf(
        NavigationItem(AppTab.DASHBOARD, Icons.Default.Dashboard, "Home"),
        NavigationItem(AppTab.STUDENTS, Icons.Default.Groups, "Students"),
        NavigationItem(AppTab.EXAMS, Icons.Default.Assignment, "Exams"),
        NavigationItem(AppTab.MARKS_ENTRY, Icons.Default.EditNote, "Marks"),
        NavigationItem(AppTab.SETTINGS, Icons.Default.Tune, "Settings")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoyalBlue700)
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = RoyalBlue700, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = settings?.teacherName ?: "Teacher",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = settings?.coachingName ?: "Coaching Centre",
                        fontSize = 12.sp,
                        color = RoyalBlue50.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Items in Drawer
                navItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontWeight = if (currentTab == item.tab) FontWeight.Bold else FontWeight.Medium) },
                        selected = currentTab == item.tab,
                        onClick = {
                            viewModel.setTab(item.tab)
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = RoyalBlue50,
                            selectedIconColor = RoyalBlue700,
                            selectedTextColor = RoyalBlue700
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(color = Slate200)

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = Slate500) },
                    label = { Text("Sign Out", color = Slate700) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentTab.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = settings?.coachingName ?: "Coaching Centre",
                                style = MaterialTheme.typography.labelSmall,
                                color = RoyalBlue50.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Navigation Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { isMenuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Attendance Register") },
                                onClick = {
                                    viewModel.setTab(AppTab.ATTENDANCE)
                                    isMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Fees Management") },
                                onClick = {
                                    viewModel.setTab(AppTab.FEES)
                                    isMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Manage Batches") },
                                onClick = {
                                    viewModel.setTab(AppTab.BATCHES)
                                    isMenuExpanded = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    viewModel.setTab(AppTab.SETTINGS)
                                    isMenuExpanded = false
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = RoyalBlue700
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentTab == item.tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setTab(item.tab) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoyalBlue700,
                                selectedTextColor = RoyalBlue700,
                                indicatorColor = RoyalBlue50
                            )
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Crossfade(
                targetState = currentTab,
                label = "ScreenTransition",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { tab ->
                when (tab) {
                    AppTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToAddStudent = {
                            viewModel.setTab(AppTab.STUDENTS)
                        },
                        onNavigateToCreateExam = {
                            viewModel.setTab(AppTab.EXAMS)
                        },
                        onNavigateToMarksEntry = { examId ->
                            if (examId != null) viewModel.loadMarksForExam(examId)
                            viewModel.setTab(AppTab.MARKS_ENTRY)
                        },
                        onNavigateToAttendance = {
                            viewModel.setTab(AppTab.ATTENDANCE)
                        }
                    )
                    AppTab.STUDENTS -> StudentsScreen(viewModel = viewModel)
                    AppTab.BATCHES -> BatchesScreen(viewModel = viewModel)
                    AppTab.EXAMS -> ExamsScreen(viewModel = viewModel)
                    AppTab.MARKS_ENTRY -> MarksEntryScreen(viewModel = viewModel)
                    AppTab.ATTENDANCE -> AttendanceScreen(viewModel = viewModel)
                    AppTab.FEES -> FeesScreen(viewModel = viewModel)
                    AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
