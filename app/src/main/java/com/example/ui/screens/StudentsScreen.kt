package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.calculation.PerformanceCalculator
import com.example.data.model.BatchEntity
import com.example.data.model.StudentEntity
import com.example.data.model.StudentProgressPoint
import com.example.data.model.StudentReportCard
import com.example.data.model.StudentSubjectAverage
import com.example.data.model.StudentWithBatch
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GradeBadge
import com.example.ui.components.RankBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.components.SubjectPerformanceBarChart
import com.example.ui.components.TrendLineChart
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
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    initialAddOpen: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val students by viewModel.enrichedStudents.collectAsStateWithLifecycle()
    val batches by viewModel.batches.collectAsStateWithLifecycle()
    val allExams by viewModel.allExams.collectAsStateWithLifecycle()
    val allMarks by viewModel.allMarks.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedBatchId by remember { mutableStateOf<Long?>(null) }
    var showActiveOnly by remember { mutableStateOf(true) }

    var studentToEdit by remember { mutableStateOf<StudentEntity?>(null) }
    var isAddEditDialogOpen by remember { mutableStateOf(initialAddOpen) }

    val selectedStudentId by viewModel.selectedStudentId.collectAsStateWithLifecycle()
    var reportCardStudent by remember { mutableStateOf<StudentReportCard?>(null) }

    // Filter students
    val filteredStudents = students.filter { item ->
        val matchesSearch = item.student.fullName.contains(searchQuery, ignoreCase = true) ||
                item.student.rollNumber.contains(searchQuery, ignoreCase = true)
        val matchesBatch = selectedBatchId == null || item.student.batchId == selectedBatchId
        val matchesStatus = !showActiveOnly || item.student.isActive
        matchesSearch && matchesBatch && matchesStatus
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    studentToEdit = null
                    isAddEditDialogOpen = true
                },
                containerColor = RoyalBlue700,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_student_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Student")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add Student", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name or roll number...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Slate400) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate400)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Batch filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            label = "All Batches (${students.size})",
                            isSelected = selectedBatchId == null,
                            onClick = { selectedBatchId = null }
                        )
                    }
                    items(batches) { batch ->
                        val countInBatch = students.count { it.student.batchId == batch.id }
                        FilterChip(
                            label = "${batch.name.take(16)} ($countInBatch)",
                            isSelected = selectedBatchId == batch.id,
                            onClick = { selectedBatchId = batch.id }
                        )
                    }
                }
            }

            HorizontalDivider(color = Slate200)

            // Student Directory List
            if (filteredStudents.isEmpty()) {
                EmptyStateView(
                    title = if (students.isEmpty()) "No Students Added Yet" else "No matching students found",
                    subtitle = if (students.isEmpty()) "Tap 'Add Student' to enroll your first student." else "Try searching with a different name or clear the batch filter.",
                    icon = Icons.Default.Person,
                    actionButton = if (students.isEmpty()) {
                        {
                            Button(
                                onClick = {
                                    studentToEdit = null
                                    isAddEditDialogOpen = true
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add First Student")
                            }
                        }
                    } else null
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Showing ${filteredStudents.size} students",
                            style = MaterialTheme.typography.labelMedium,
                            color = Slate500,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(filteredStudents, key = { it.student.id }) { item ->
                        StudentCardItem(
                            studentWithBatch = item,
                            onClick = {
                                viewModel.selectedStudentId.value = item.student.id
                            },
                            onEdit = {
                                studentToEdit = item.student
                                isAddEditDialogOpen = true
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Student Dialog
    if (isAddEditDialogOpen) {
        AddEditStudentDialog(
            student = studentToEdit,
            batches = batches,
            onDismiss = { isAddEditDialogOpen = false },
            onSave = { savedStudent ->
                viewModel.saveStudent(savedStudent) {
                    isAddEditDialogOpen = false
                }
            }
        )
    }

    // Student Detail Sheet
    val currentSelectedStudent = students.find { it.student.id == selectedStudentId }
    if (currentSelectedStudent != null) {
        val studentId = currentSelectedStudent.student.id

        // Calculate progress points for trend line
        val progressPoints = remember(studentId, allExams, allMarks) {
            PerformanceCalculator.calculateStudentProgress(
                studentId = studentId,
                allExams = allExams,
                allExamSubjects = emptyList(),
                allStudents = students.map { it.student },
                allMarks = allMarks
            )
        }

        // Calculate subject averages
        val subjectAverages = remember(studentId, allMarks) {
            PerformanceCalculator.calculateStudentSubjectAverages(
                studentId = studentId,
                allExamSubjects = emptyList(),
                allMarks = allMarks
            )
        }

        StudentDetailSheet(
            studentWithBatch = currentSelectedStudent,
            progressPoints = progressPoints,
            subjectAverages = subjectAverages,
            onDismiss = { viewModel.selectedStudentId.value = null },
            onEdit = {
                studentToEdit = currentSelectedStudent.student
                isAddEditDialogOpen = true
            },
            onDelete = {
                viewModel.deleteStudent(currentSelectedStudent.student)
                viewModel.selectedStudentId.value = null
            },
            onGenerateReportCard = {
                scope.launch {
                    val rc = viewModel.generateStudentReportCard(studentId)
                    reportCardStudent = rc
                }
            }
        )
    }

    // Report Card Dialog
    if (reportCardStudent != null) {
        ReportCardPreviewDialog(
            reportCard = reportCardStudent!!,
            onDismiss = { reportCardStudent = null },
            onShare = { formattedText ->
                viewModel.shareText(
                    context = context,
                    title = "Academic Report Card - ${reportCardStudent!!.student.fullName}",
                    content = formattedText
                )
            }
        )
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) RoyalBlue700 else Slate100,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Slate700,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun StudentCardItem(
    studentWithBatch: StudentWithBatch,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    val st = studentWithBatch.student

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("student_card_${st.id}"),
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
            StudentAvatar(name = st.fullName, size = 46.dp, gender = st.gender)
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = st.fullName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    if (!st.isActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = Slate100) {
                            Text(
                                text = "INACTIVE",
                                color = Slate400,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${st.rollNumber} • ${studentWithBatch.batchName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400
                )
                if (studentWithBatch.isFlagged) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Rose600,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = studentWithBatch.flagReason,
                            color = Rose600,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (studentWithBatch.totalExamsTaken > 0) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f%%", studentWithBatch.overallPercentage),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue700
                    )
                    GradeBadge(grade = studentWithBatch.overallGrade, size = "small")
                } else {
                    Text(
                        text = "No tests",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentDialog(
    student: StudentEntity?,
    batches: List<BatchEntity>,
    onDismiss: () -> Unit,
    onSave: (StudentEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(student?.fullName ?: "") }
    var rollNumber by remember { mutableStateOf(student?.rollNumber ?: "") }
    var gender by remember { mutableStateOf(student?.gender ?: "Male") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    var parentName by remember { mutableStateOf(student?.parentName ?: "") }
    var parentPhone by remember { mutableStateOf(student?.parentPhone ?: "") }
    var address by remember { mutableStateOf(student?.address ?: "") }
    var selectedBatchId by remember { mutableStateOf(student?.batchId ?: batches.firstOrNull()?.id ?: 0L) }
    var admissionDate by remember { mutableStateOf(student?.admissionDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var isActive by remember { mutableStateOf(student?.isActive ?: true) }
    var notes by remember { mutableStateOf(student?.notes ?: "") }

    var isBatchDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (student == null) "Add New Student" else "Edit Student Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = { rollNumber = it },
                    label = { Text("Roll Number / ID (Auto if blank)") },
                    placeholder = { Text("e.g. STU-001") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Batch Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = isBatchDropdownExpanded,
                    onExpandedChange = { isBatchDropdownExpanded = it }
                ) {
                    val batchName = batches.find { it.id == selectedBatchId }?.name ?: "Select Batch"
                    OutlinedTextField(
                        value = batchName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assigned Batch *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBatchDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isBatchDropdownExpanded,
                        onDismissRequest = { isBatchDropdownExpanded = false }
                    ) {
                        batches.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b.name) },
                                onClick = {
                                    selectedBatchId = b.id
                                    isBatchDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Student Phone") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = parentPhone,
                        onValueChange = { parentPhone = it },
                        label = { Text("Parent Phone") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent / Guardian Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Academic Notes / Observations") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Student Status",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fullName.isNotBlank()) {
                                onSave(
                                    StudentEntity(
                                        id = student?.id ?: 0L,
                                        rollNumber = rollNumber,
                                        fullName = fullName.trim(),
                                        gender = gender,
                                        phone = phone.trim(),
                                        parentName = parentName.trim(),
                                        parentPhone = parentPhone.trim(),
                                        address = address.trim(),
                                        batchId = selectedBatchId,
                                        admissionDate = admissionDate,
                                        isActive = isActive,
                                        notes = notes.trim()
                                    )
                                )
                            }
                        },
                        enabled = fullName.isNotBlank(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (student == null) "Save Student" else "Update")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailSheet(
    studentWithBatch: StudentWithBatch,
    progressPoints: List<StudentProgressPoint>,
    subjectAverages: List<StudentSubjectAverage>,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onGenerateReportCard: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val st = studentWithBatch.student
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StudentAvatar(name = st.fullName, size = 56.dp, gender = st.gender)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = st.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "${st.rollNumber} • ${studentWithBatch.batchName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RoyalBlue600)
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Rose600)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onGenerateReportCard,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue700),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Report Card", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate50)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cumulative Avg", fontSize = 11.sp, color = Slate500)
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f%%", studentWithBatch.overallPercentage),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue700
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Grade", fontSize = 11.sp, color = Slate500)
                        GradeBadge(grade = studentWithBatch.overallGrade, size = "large")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Attendance", fontSize = 11.sp, color = Slate500)
                        Text(
                            text = String.format(Locale.getDefault(), "%.0f%%", studentWithBatch.attendanceRate),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (studentWithBatch.attendanceRate >= 75.0) Emerald600 else Rose600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Trend Chart
            Text(
                text = "Progress Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(8.dp))
            TrendLineChart(points = progressPoints)

            Spacer(modifier = Modifier.height(16.dp))

            // Subject Strengths Bar Chart
            if (subjectAverages.isNotEmpty()) {
                SubjectPerformanceBarChart(averages = subjectAverages)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Contact & Notes Info
            Text(
                text = "Student & Guardian Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    DetailRow(label = "Student Phone", value = st.phone.ifBlank { "Not provided" })
                    DetailRow(label = "Parent / Guardian", value = st.parentName.ifBlank { "Not provided" })
                    DetailRow(label = "Parent Phone", value = st.parentPhone.ifBlank { "Not provided" })
                    DetailRow(label = "Admission Date", value = st.admissionDate.ifBlank { "N/A" })
                    if (st.notes.isNotBlank()) {
                        DetailRow(label = "Teacher Notes", value = st.notes)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Student?") },
            text = { Text("Are you sure you want to delete ${st.fullName}? All associated marks and records will be deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Slate500)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Slate900
        )
    }
}

@Composable
fun ReportCardPreviewDialog(
    reportCard: StudentReportCard,
    onDismiss: () -> Unit,
    onShare: (String) -> Unit
) {
    val formattedReport = remember(reportCard) {
        buildString {
            appendLine("==============================================")
            appendLine("       ${reportCard.coachingName.uppercase()}")
            appendLine("          STUDENT PERFORMANCE REPORT")
            appendLine("==============================================")
            appendLine("Student Name : ${reportCard.student.fullName}")
            appendLine("Roll Number  : ${reportCard.student.rollNumber}")
            appendLine("Batch/Class  : ${reportCard.batchName}")
            appendLine("Teacher      : ${reportCard.teacherName}")
            appendLine("Date Issued  : ${reportCard.issueDate}")
            appendLine("----------------------------------------------")
            appendLine("ACADEMIC PERFORMANCE SUMMARY:")
            appendLine("Cumulative Score : ${String.format(Locale.getDefault(), "%.1f%%", reportCard.cumulativePercentage)}")
            appendLine("Overall Grade    : ${reportCard.cumulativeGrade}")
            appendLine("Attendance Rate  : ${String.format(Locale.getDefault(), "%.0f%%", reportCard.attendancePercentage)}")
            appendLine("----------------------------------------------")
            appendLine("EXAM DETAILS:")
            reportCard.examResults.forEach { ex ->
                appendLine("• ${ex.examTitle} (${ex.examDate})")
                ex.subjectScores.forEach { sc ->
                    val markDisplay = if (sc.isAbsent) "ABSENT" else "${sc.obtained}/${sc.max}"
                    appendLine("   - ${sc.subjectName}: $markDisplay")
                }
                appendLine("   Total: ${ex.totalObtained}/${ex.totalMax} (${String.format(Locale.getDefault(), "%.1f%%", ex.percentage)}) | Grade: ${ex.grade} | Status: ${ex.status}")
                appendLine("")
            }
            if (reportCard.generalNotes.isNotBlank()) {
                appendLine("TEACHER REMARKS:")
                appendLine(reportCard.generalNotes)
                appendLine("----------------------------------------------")
            }
            appendLine("Generated by Student Performance Tracker")
            appendLine("==============================================")
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Report Card",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Printable Card Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate50),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = reportCard.coachingName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = RoyalBlue700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Student Progress Record",
                            fontSize = 11.sp,
                            color = Slate500,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow(label = "Student", value = reportCard.student.fullName)
                        DetailRow(label = "Roll No", value = reportCard.student.rollNumber)
                        DetailRow(label = "Class / Batch", value = reportCard.batchName)
                        DetailRow(label = "Cumulative Average", value = String.format(Locale.getDefault(), "%.1f%%", reportCard.cumulativePercentage))
                        DetailRow(label = "Overall Grade", value = reportCard.cumulativeGrade)
                        DetailRow(label = "Attendance", value = String.format(Locale.getDefault(), "%.0f%%", reportCard.attendancePercentage))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onShare(formattedReport) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue700)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share with Parent")
                    }
                }
            }
        }
    }
}
