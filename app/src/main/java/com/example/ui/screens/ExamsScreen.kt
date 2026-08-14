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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Leaderboard
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.calculation.PerformanceCalculator
import com.example.data.model.BatchEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ExamSubjectEntity
import com.example.data.model.ExamWithDetails
import com.example.data.model.StudentEntity
import com.example.data.model.StudentExamResult
import com.example.data.model.SubjectEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GradeBadge
import com.example.ui.components.GradeDistributionCard
import com.example.ui.components.PassFailChip
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
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    initialCreateOpen: Boolean = false
) {
    val exams by viewModel.allExams.collectAsStateWithLifecycle()
    val batches by viewModel.batches.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val allMarks by viewModel.allMarks.collectAsStateWithLifecycle()

    var isCreateExamOpen by remember { mutableStateOf(initialCreateOpen) }
    var examToEdit by remember { mutableStateOf<ExamEntity?>(null) }
    val selectedExamId by viewModel.selectedExamId.collectAsStateWithLifecycle()
    var selectedBatchFilter by remember { mutableStateOf<Long?>(null) }

    val batchMap = remember(batches) { batches.associate { it.id to it.name } }

    val filteredExams = exams.filter { ex ->
        selectedBatchFilter == null || ex.batchId == selectedBatchFilter
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    examToEdit = null
                    isCreateExamOpen = true
                },
                containerColor = RoyalBlue700,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_exam_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Exam")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "New Exam", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header & Batch Filter Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Exams & Test Records",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Manage test schedules, subjects, and view rankings",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            label = "All Batches (${exams.size})",
                            isSelected = selectedBatchFilter == null,
                            onClick = { selectedBatchFilter = null }
                        )
                    }
                    items(batches) { batch ->
                        val count = exams.count { it.batchId == batch.id }
                        FilterChip(
                            label = "${batch.name.take(16)} ($count)",
                            isSelected = selectedBatchFilter == batch.id,
                            onClick = { selectedBatchFilter = batch.id }
                        )
                    }
                }
            }

            HorizontalDivider(color = Slate200)

            if (filteredExams.isEmpty()) {
                EmptyStateView(
                    title = "No Exams Recorded",
                    subtitle = "Tap 'New Exam' to schedule your first unit test or midterm exam.",
                    icon = Icons.Default.Assignment,
                    actionButton = {
                        Button(
                            onClick = {
                                examToEdit = null
                                isCreateExamOpen = true
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Create Exam")
                        }
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredExams, key = { it.id }) { exam ->
                        val batchName = batchMap[exam.batchId] ?: "Batch"
                        val examMarks = allMarks.filter { it.examId == exam.id }
                        val appearedCount = examMarks.filter { !it.isAbsent }.map { it.studentId }.distinct().size
                        val totalStudentsInBatch = allStudents.count { it.batchId == exam.batchId && it.isActive }

                        ExamCardItem(
                            exam = exam,
                            batchName = batchName,
                            appearedCount = appearedCount,
                            totalStudents = totalStudentsInBatch,
                            onClick = {
                                viewModel.selectedExamId.value = exam.id
                            },
                            onEnterMarks = {
                                viewModel.loadMarksForExam(exam.id)
                                viewModel.setTab(AppTab.MARKS_ENTRY)
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

    // Create / Edit Exam Dialog
    if (isCreateExamOpen) {
        CreateExamDialog(
            exam = examToEdit,
            batches = batches,
            allSubjects = allSubjects,
            onDismiss = { isCreateExamOpen = false },
            onSave = { title, examType, date, batchId, passingPct, selectedSubjects ->
                viewModel.saveExam(
                    title = title,
                    examType = examType,
                    date = date,
                    batchId = batchId,
                    passingPercentage = passingPct,
                    subjects = selectedSubjects,
                    examId = examToEdit?.id ?: 0L
                ) {
                    isCreateExamOpen = false
                }
            }
        )
    }

    // Exam Detail Bottom Sheet
    val currentExam = exams.find { it.id == selectedExamId }
    if (currentExam != null) {
        val examBatchName = batchMap[currentExam.batchId] ?: "Batch"
        val batchStudents = allStudents.filter { it.batchId == currentExam.batchId }
        val examMarks = allMarks.filter { it.examId == currentExam.id }

        // Fetch subjects for this exam
        val examSubjects = allSubjects.filter { it.batchId == currentExam.batchId }.map {
            ExamSubjectEntity(examId = currentExam.id, subjectId = it.id, subjectName = it.name, maxMarks = 100.0)
        }

        val examResults = remember(currentExam.id, examMarks) {
            PerformanceCalculator.calculateExamResults(
                exam = currentExam,
                subjects = examSubjects,
                students = batchStudents,
                marks = examMarks
            )
        }

        val subjectStats = remember(currentExam.id, examMarks) {
            PerformanceCalculator.calculateSubjectStats(
                subjects = examSubjects,
                marks = examMarks,
                passingPercentage = currentExam.passingPercentage
            )
        }

        val gradeDistribution = remember(examResults) {
            PerformanceCalculator.calculateGradeDistribution(examResults)
        }

        ExamDetailSheet(
            exam = currentExam,
            batchName = examBatchName,
            results = examResults,
            subjects = examSubjects,
            subjectStats = subjectStats,
            gradeDistribution = gradeDistribution,
            onDismiss = { viewModel.selectedExamId.value = null },
            onEnterMarks = {
                viewModel.selectedExamId.value = null
                viewModel.loadMarksForExam(currentExam.id)
                viewModel.setTab(AppTab.MARKS_ENTRY)
            },
            onDelete = {
                viewModel.deleteExam(currentExam)
            }
        )
    }
}

@Composable
fun ExamCardItem(
    exam: ExamEntity,
    batchName: String,
    appearedCount: Int,
    totalStudents: Int,
    onClick: () -> Unit,
    onEnterMarks: () -> Unit
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isUpcoming = exam.date > today

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("exam_card_${exam.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isUpcoming) Amber50 else RoyalBlue50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isUpcoming) Icons.Default.Event else Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = if (isUpcoming) Amber600 else RoyalBlue600,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = exam.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            if (isUpcoming) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Amber50) {
                                    Text(
                                        text = "UPCOMING",
                                        color = Amber600,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "$batchName • Date: ${exam.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Slate100
                ) {
                    Text(
                        text = "Pass Cutoff: ${exam.passingPercentage.toInt()}% • Type: ${exam.examType}",
                        fontSize = 11.sp,
                        color = Slate500,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Button(
                    onClick = onEnterMarks,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue700),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (appearedCount > 0) "Edit Marks ($appearedCount)" else "Enter Marks", fontSize = 11.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExamDialog(
    exam: ExamEntity?,
    batches: List<BatchEntity>,
    allSubjects: List<SubjectEntity>,
    onDismiss: () -> Unit,
    onSave: (title: String, examType: String, date: String, batchId: Long, passingPercentage: Double, subjects: List<Pair<SubjectEntity, Double>>) -> Unit
) {
    var title by remember { mutableStateOf(exam?.title ?: "") }
    var examType by remember { mutableStateOf(exam?.examType ?: "Monthly Test") }
    var date by remember { mutableStateOf(exam?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var selectedBatchId by remember { mutableStateOf(exam?.batchId ?: batches.firstOrNull()?.id ?: 0L) }
    var passingPctText by remember { mutableStateOf((exam?.passingPercentage ?: 40.0).toString()) }

    var isBatchExpanded by remember { mutableStateOf(false) }
    var isTypeExpanded by remember { mutableStateOf(false) }

    val batchSubjects = allSubjects.filter { it.batchId == selectedBatchId }
    val selectedSubjectsMap = remember { mutableStateMapOf<Long, Double>() }

    // Initialize subjects
    remember(selectedBatchId) {
        selectedSubjectsMap.clear()
        batchSubjects.forEach { sub ->
            selectedSubjectsMap[sub.id] = 100.0
        }
    }

    val examTypes = listOf("Unit Test", "Monthly Test", "Mock Test", "Midterm", "Half Yearly", "Final Exam", "Practice Quiz")

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
                    text = if (exam == null) "Create New Exam" else "Edit Exam",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Exam Title *") },
                    placeholder = { Text("e.g. Monthly Test – August") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Batch Selector
                ExposedDropdownMenuBox(
                    expanded = isBatchExpanded,
                    onExpandedChange = { isBatchExpanded = it }
                ) {
                    val batchName = batches.find { it.id == selectedBatchId }?.name ?: "Select Batch"
                    OutlinedTextField(
                        value = batchName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Batch *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBatchExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isBatchExpanded,
                        onDismissRequest = { isBatchExpanded = false }
                    ) {
                        batches.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b.name) },
                                onClick = {
                                    selectedBatchId = b.id
                                    isBatchExpanded = false
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
                    // Exam Type Selector
                    ExposedDropdownMenuBox(
                        expanded = isTypeExpanded,
                        onExpandedChange = { isTypeExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = examType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Exam Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isTypeExpanded,
                            onDismissRequest = { isTypeExpanded = false }
                        ) {
                            examTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        examType = type
                                        isTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = passingPctText,
                    onValueChange = { passingPctText = it },
                    label = { Text("Passing Percentage (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Include Subjects & Max Marks:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (batchSubjects.isEmpty()) {
                    Text(
                        text = "No subjects defined in this batch yet. Add subjects in Batches tab.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Rose600
                    )
                } else {
                    batchSubjects.forEach { sub ->
                        val isChecked = selectedSubjectsMap.containsKey(sub.id)
                        var maxMarksText by remember { mutableStateOf("100") }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedSubjectsMap[sub.id] = maxMarksText.toDoubleOrNull() ?: 100.0
                                    } else {
                                        selectedSubjectsMap.remove(sub.id)
                                    }
                                }
                            )
                            Text(
                                text = sub.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            if (isChecked) {
                                OutlinedTextField(
                                    value = maxMarksText,
                                    onValueChange = {
                                        maxMarksText = it
                                        selectedSubjectsMap[sub.id] = it.toDoubleOrNull() ?: 100.0
                                    },
                                    label = { Text("Max Marks") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.width(110.dp)
                                )
                            }
                        }
                    }
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
                            if (title.isNotBlank()) {
                                val included = batchSubjects.filter { selectedSubjectsMap.containsKey(it.id) }
                                    .map { it to (selectedSubjectsMap[it.id] ?: 100.0) }
                                onSave(
                                    title.trim(),
                                    examType,
                                    date,
                                    selectedBatchId,
                                    passingPctText.toDoubleOrNull() ?: 40.0,
                                    included
                                )
                            }
                        },
                        enabled = title.isNotBlank() && selectedSubjectsMap.isNotEmpty(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (exam == null) "Schedule Exam" else "Update Exam")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailSheet(
    exam: ExamEntity,
    batchName: String,
    results: List<StudentExamResult>,
    subjects: List<ExamSubjectEntity>,
    subjectStats: List<com.example.data.model.SubjectExamStats>,
    gradeDistribution: List<com.example.data.model.GradeDistribution>,
    onDismiss: () -> Unit,
    onEnterMarks: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val presentCount = results.count { !it.isAllAbsent }
    val passCount = results.count { it.isPassed }
    val classAverage = if (presentCount > 0) results.filter { !it.isAllAbsent }.map { it.percentage }.average() else 0.0
    val highestScore = if (presentCount > 0) results.filter { !it.isAllAbsent }.maxOfOrNull { it.percentage } ?: 0.0 else 0.0
    val lowestScore = if (presentCount > 0) results.filter { !it.isAllAbsent }.minOfOrNull { it.percentage } ?: 0.0 else 0.0

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
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exam.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "$batchName • Date: ${exam.date} • Passing: ${exam.passingPercentage.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500
                    )
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Rose600)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action: Edit / Enter Marks Grid
            Button(
                onClick = onEnterMarks,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.EditNote, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Marks Spreadsheet Grid", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Performance Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = RoyalBlue50)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Class Average", fontSize = 11.sp, color = Slate500)
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f%%", classAverage),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue700
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald50)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Highest", fontSize = 11.sp, color = Slate500)
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f%%", highestScore),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald600
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pass Rate", fontSize = 11.sp, color = Slate500)
                        val passRate = if (presentCount > 0) (passCount.toDouble() / presentCount) * 100.0 else 0.0
                        Text(
                            text = "${passRate.toInt()}%",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (passRate >= 70) Emerald600 else Rose600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grade Distribution Chart
            GradeDistributionCard(distributions = gradeDistribution)

            Spacer(modifier = Modifier.height(16.dp))

            // Subject-wise performance summary
            if (subjectStats.isNotEmpty()) {
                Text(
                    text = "Subject-Wise Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(8.dp))
                subjectStats.forEach { stat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate50)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stat.subjectName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "Max Marks: ${stat.maxMarks.toInt()}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Avg", fontSize = 10.sp, color = Slate400)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f", stat.averageMarks),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = RoyalBlue700
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Max", fontSize = 10.sp, color = Slate400)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.0f", stat.highestMarks),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Emerald600
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Min", fontSize = 10.sp, color = Slate400)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.0f", stat.lowestMarks),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Rose600
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Student Ranks Leaderboard Table
            Text(
                text = "Exam Results & Batch Ranks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (results.isEmpty()) {
                Text(
                    text = "No marks entered yet. Tap 'Open Marks Spreadsheet Grid' to enter scores.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                results.forEach { res ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RankBadge(rank = res.rank)
                        Spacer(modifier = Modifier.width(8.dp))
                        StudentAvatar(name = res.student.fullName, size = 36.dp, gender = res.student.gender)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = res.student.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Slate900
                            )
                            Text(
                                text = "Roll: ${res.student.rollNumber} • Total: ${res.totalMarksObtained.toInt()}/${res.totalMaxMarks.toInt()}",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            if (res.isAllAbsent) {
                                PassFailChip(isPassed = false, isAbsent = true)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f%%", res.percentage),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (res.isPassed) RoyalBlue700 else Rose600
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    GradeBadge(grade = res.grade, size = "small")
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                PassFailChip(isPassed = res.isPassed)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Exam?") },
            text = { Text("Are you sure you want to delete '${exam.title}'? All entered student marks for this exam will be permanently deleted.") },
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
