package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.AttentionStudent
import com.example.data.model.DashboardSummary
import com.example.data.model.ExamWithDetails
import com.example.data.model.TopPerformer
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GradeBadge
import com.example.ui.components.MetricStatCard
import com.example.ui.components.RankBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber600
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.Rose500
import com.example.ui.theme.Rose50
import com.example.ui.theme.Rose600
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onNavigateToAddStudent: () -> Unit = {},
    onNavigateToCreateExam: () -> Unit = {},
    onNavigateToMarksEntry: (Long?) -> Unit = {},
    onNavigateToAttendance: () -> Unit = {}
) {
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_welcome_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue700)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome back, ${settings?.teacherName ?: "Teacher"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = settings?.coachingName ?: "Coaching Centre",
                                style = MaterialTheme.typography.bodySmall,
                                color = RoyalBlue50.copy(alpha = 0.9f)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Emerald500)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Synced",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Active Students",
                        value = "${summary.totalActiveStudents}",
                        subtitle = "${summary.totalBatches} batches active",
                        icon = Icons.Default.Groups,
                        accentColor = RoyalBlue600,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTab(AppTab.STUDENTS) }
                    )
                    MetricStatCard(
                        title = "Exams Conducted",
                        value = "${summary.totalExamsConducted}",
                        subtitle = "Performance tracked",
                        icon = Icons.Default.AssignmentTurnedIn,
                        accentColor = Emerald600,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTab(AppTab.EXAMS) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Pending Fees",
                        value = "₹${summary.pendingFeesTotal.toInt()}",
                        subtitle = "${summary.pendingStudentsCount} students pending",
                        icon = Icons.Default.Payment,
                        accentColor = Amber600,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTab(AppTab.FEES) }
                    )
                    MetricStatCard(
                        title = "Needs Attention",
                        value = "${summary.attentionList.size}",
                        subtitle = "Below 40% or declining",
                        icon = Icons.Default.Warning,
                        accentColor = Rose600,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionItem(
                            label = "Add Student",
                            icon = Icons.Default.PersonAdd,
                            color = RoyalBlue600,
                            onClick = onNavigateToAddStudent
                        )
                        QuickActionItem(
                            label = "New Exam",
                            icon = Icons.Default.Assignment,
                            color = Emerald600,
                            onClick = onNavigateToCreateExam
                        )
                        QuickActionItem(
                            label = "Enter Marks",
                            icon = Icons.Default.EditNote,
                            color = Amber600,
                            onClick = { onNavigateToMarksEntry(null) }
                        )
                        QuickActionItem(
                            label = "Attendance",
                            icon = Icons.Default.FactCheck,
                            color = Color(0xFF8B5CF6),
                            onClick = onNavigateToAttendance
                        )
                    }
                }
            }
        }

        // Needs Attention Alert List
        if (summary.attentionList.isNotEmpty()) {
            item {
                AttentionAlertSection(
                    attentionStudents = summary.attentionList,
                    onStudentClick = { studentId ->
                        viewModel.selectedStudentId.value = studentId
                        viewModel.setTab(AppTab.STUDENTS)
                    }
                )
            }
        }

        // Top 5 Performers Leaderboard
        item {
            TopPerformersSection(
                topPerformers = summary.topPerformers,
                onStudentClick = { studentId ->
                    viewModel.selectedStudentId.value = studentId
                    viewModel.setTab(AppTab.STUDENTS)
                }
            )
        }

        // Recent Exams
        item {
            RecentExamsSection(
                recentExams = summary.recentExams,
                upcomingExams = summary.upcomingExams,
                onExamClick = { examId ->
                    viewModel.selectedExamId.value = examId
                    viewModel.setTab(AppTab.EXAMS)
                },
                onEnterMarks = { examId ->
                    viewModel.loadMarksForExam(examId)
                    viewModel.setTab(AppTab.MARKS_ENTRY)
                }
            )
        }
    }
}

@Composable
fun QuickActionItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Slate700
        )
    }
}

@Composable
fun AttentionAlertSection(
    attentionStudents: List<AttentionStudent>,
    onStudentClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attention_alert_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Rose500.copy(alpha = 0.3f)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Rose600,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Needs Extra Attention",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Rose600
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Rose50
                ) {
                    Text(
                        text = "${attentionStudents.size} flagged",
                        color = Rose600,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            attentionStudents.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onStudentClick(item.student.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StudentAvatar(
                        name = item.student.fullName,
                        size = 36.dp,
                        gender = item.student.gender
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.student.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "${item.student.rollNumber} • ${item.batchName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Rose50
                    ) {
                        Text(
                            text = item.reason,
                            color = Rose600,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopPerformersSection(
    topPerformers: List<TopPerformer>,
    onStudentClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("top_performers_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Top Performers Leaderboard",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Overall Rank",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (topPerformers.isEmpty()) {
                Text(
                    text = "No graded exam records yet. Conduct an exam to see top ranks!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                topPerformers.forEach { performer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onStudentClick(performer.student.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RankBadge(rank = performer.rank)
                        Spacer(modifier = Modifier.width(10.dp))
                        StudentAvatar(
                            name = performer.student.fullName,
                            size = 36.dp,
                            gender = performer.student.gender
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = performer.student.fullName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "${performer.student.rollNumber} • ${performer.batchName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f%%", performer.averagePercentage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue700
                            )
                            GradeBadge(grade = performer.grade, size = "small")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentExamsSection(
    recentExams: List<ExamWithDetails>,
    upcomingExams: List<ExamWithDetails>,
    onExamClick: (Long) -> Unit,
    onEnterMarks: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent & Upcoming Exams",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (recentExams.isEmpty() && upcomingExams.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = "No exams scheduled. Tap 'New Exam' to schedule one!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            recentExams.forEach { ex ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExamClick(ex.exam.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RoyalBlue50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                tint = RoyalBlue600,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ex.exam.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "${ex.batchName} • Date: ${ex.exam.date}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400
                            )
                        }
                        OutlinedButton(
                            onClick = { onEnterMarks(ex.exam.id) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Marks Grid", fontSize = 11.sp)
                        }
                    }
                }
            }

            upcomingExams.forEach { ex ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExamClick(ex.exam.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Amber50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = Amber600,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ex.exam.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Amber50
                                ) {
                                    Text(
                                        text = "UPCOMING",
                                        color = Amber600,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${ex.batchName} • Date: ${ex.exam.date}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400
                            )
                        }
                    }
                }
            }
        }
    }
}
