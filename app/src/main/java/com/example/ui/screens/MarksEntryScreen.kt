package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.calculation.PerformanceCalculator
import com.example.data.model.ExamEntity
import com.example.data.model.ExamSubjectEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GradeBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
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
import com.example.ui.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarksEntryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val exams by viewModel.allExams.collectAsStateWithLifecycle()
    val batches by viewModel.batches.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val allMarks by viewModel.allMarks.collectAsStateWithLifecycle()
    val selectedExamId by viewModel.marksEntryExamId.collectAsStateWithLifecycle()

    var isExamDropdownExpanded by remember { mutableStateOf(false) }

    val batchMap = remember(batches) { batches.associate { it.id to it.name } }
    val currentExam = exams.find { it.id == selectedExamId } ?: exams.firstOrNull()

    // Local matrix edits
    val marksInputs = remember { mutableStateMapOf<Pair<Long, Long>, String>() } // (studentId, subjectId) -> text
    val absentFlags = remember { mutableStateMapOf<Pair<Long, Long>, Boolean>() } // (studentId, subjectId) -> isAbsent
    val remarksInputs = remember { mutableStateMapOf<Long, String>() }

    val batchStudents = remember(currentExam, allStudents) {
        if (currentExam != null) {
            allStudents.filter { it.batchId == currentExam.batchId && it.isActive }
        } else emptyList()
    }

    val examSubjects = remember(currentExam, allSubjects) {
        if (currentExam != null) {
            allSubjects.filter { it.batchId == currentExam.batchId }
        } else emptyList()
    }

    // Load initial values from DB
    LaunchedEffect(currentExam?.id, allMarks) {
        if (currentExam != null) {
            marksInputs.clear()
            absentFlags.clear()
            remarksInputs.clear()

            val existingMarks = allMarks.filter { it.examId == currentExam.id }
            existingMarks.forEach { m ->
                marksInputs[m.studentId to m.subjectId] = if (m.marksObtained % 1.0 == 0.0) m.marksObtained.toInt().toString() else m.marksObtained.toString()
                absentFlags[m.studentId to m.subjectId] = m.isAbsent
                if (m.remarks.isNotBlank()) {
                    remarksInputs[m.studentId] = m.remarks
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentExam != null && batchStudents.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${batchStudents.size} Students in Matrix",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Auto calculates total & grades",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500
                            )
                        }
                        Button(
                            onClick = {
                                if (currentExam != null) {
                                    // push to viewModel
                                    batchStudents.forEach { st ->
                                        examSubjects.forEach { sub ->
                                            val isAbs = absentFlags[st.id to sub.id] ?: false
                                            val text = marksInputs[st.id to sub.id] ?: "0"
                                            val marks = text.toDoubleOrNull() ?: 0.0
                                            viewModel.updateDraftMark(st.id, sub.id, marks, isAbs)
                                        }
                                        val remark = remarksInputs[st.id] ?: ""
                                        viewModel.updateDraftRemark(st.id, remark)
                                    }
                                    viewModel.saveAllDraftMarks(currentExam.id) {}
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue700),
                            modifier = Modifier.testTag("save_marks_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save All Marks", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Exam Selector Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Marks Spreadsheet Entry Grid",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "Fast-enter student scores with instant calculation and absentee toggles",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Exam Dropdown
                ExposedDropdownMenuBox(
                    expanded = isExamDropdownExpanded,
                    onExpandedChange = { isExamDropdownExpanded = it }
                ) {
                    val examTitle = if (currentExam != null) {
                        "${currentExam.title} (${batchMap[currentExam.batchId] ?: "Batch"}) - ${currentExam.date}"
                    } else "No Exams Available"

                    OutlinedTextField(
                        value = examTitle,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Exam to Grade") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExamDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isExamDropdownExpanded,
                        onDismissRequest = { isExamDropdownExpanded = false }
                    ) {
                        exams.forEach { ex ->
                            DropdownMenuItem(
                                text = { Text("${ex.title} (${batchMap[ex.batchId] ?: "Batch"}) - ${ex.date}") },
                                onClick = {
                                    viewModel.loadMarksForExam(ex.id)
                                    isExamDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Slate200)

            if (currentExam == null || batchStudents.isEmpty()) {
                EmptyStateView(
                    title = "No Students or Exam Selected",
                    subtitle = "Select an active exam with students enrolled to start entering test marks.",
                    icon = Icons.Default.EditNote
                )
            } else {
                val horizontalScroll = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalScroll)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .background(Slate100)
                            .padding(vertical = 10.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Student Name & Roll",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate700,
                            modifier = Modifier.width(180.dp)
                        )

                        examSubjects.forEach { sub ->
                            Column(
                                modifier = Modifier
                                    .width(130.dp)
                                    .padding(horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = sub.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Slate900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "(Max: 100)",
                                    fontSize = 10.sp,
                                    color = Slate400
                                )
                            }
                        }

                        Text(
                            text = "Total & %",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(90.dp)
                        )

                        Text(
                            text = "Grade",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(60.dp)
                        )

                        Text(
                            text = "Teacher Remarks",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate700,
                            modifier = Modifier.width(160.dp)
                        )
                    }

                    HorizontalDivider(color = Slate200)

                    // Student Rows
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(batchStudents, key = { it.id }) { student ->
                            // Calculate live total & percentage for this row
                            var rowObtained = 0.0
                            var rowMax = 0.0
                            var anySubject = false
                            var isAllAbsent = true

                            examSubjects.forEach { sub ->
                                val isAbs = absentFlags[student.id to sub.id] ?: false
                                if (!isAbs) {
                                    isAllAbsent = false
                                    val markVal = marksInputs[student.id to sub.id]?.toDoubleOrNull() ?: 0.0
                                    rowObtained += markVal
                                }
                                rowMax += 100.0
                                anySubject = true
                            }

                            val rowPercentage = if (rowMax > 0 && !isAllAbsent) (rowObtained / rowMax) * 100.0 else 0.0
                            val rowGrade = if (isAllAbsent) "ABS" else PerformanceCalculator.determineGrade(rowPercentage)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Student Info Column
                                Row(
                                    modifier = Modifier.width(180.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StudentAvatar(name = student.fullName, size = 32.dp, gender = student.gender)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = student.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Slate900,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = student.rollNumber,
                                            fontSize = 11.sp,
                                            color = Slate400
                                        )
                                    }
                                }

                                // Subject input columns
                                examSubjects.forEach { sub ->
                                    val key = student.id to sub.id
                                    val markText = marksInputs[key] ?: ""
                                    val isAbs = absentFlags[key] ?: false
                                    val numericVal = markText.toDoubleOrNull() ?: 0.0
                                    val isInvalid = !isAbs && (numericVal > 100.0 || numericVal < 0.0)

                                    Row(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .padding(horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isAbs) {
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp)
                                                    .clip(RoundedCornerShape(6.dp)),
                                                color = Rose50
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("ABSENT", color = Rose600, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else {
                                            OutlinedTextField(
                                                value = markText,
                                                onValueChange = { marksInputs[key] = it },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                isError = isInvalid,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    errorBorderColor = Rose600,
                                                    errorContainerColor = Rose50
                                                ),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        // Absent Toggle Button
                                        Surface(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .clickable {
                                                    absentFlags[key] = !isAbs
                                                },
                                            color = if (isAbs) Rose600 else Slate100
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "AB",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isAbs) Color.White else Slate500
                                                )
                                            }
                                        }
                                    }
                                }

                                // Total & % Column
                                Column(
                                    modifier = Modifier.width(90.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (isAllAbsent) {
                                        Text("ABS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate400)
                                    } else {
                                        Text(
                                            text = "${rowObtained.toInt()}/${rowMax.toInt()}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Slate900
                                        )
                                        Text(
                                            text = String.format(Locale.getDefault(), "%.1f%%", rowPercentage),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = RoyalBlue700
                                        )
                                    }
                                }

                                // Grade Column
                                Box(
                                    modifier = Modifier.width(60.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    GradeBadge(grade = rowGrade, size = "small")
                                }

                                // Remarks Column
                                OutlinedTextField(
                                    value = remarksInputs[student.id] ?: "",
                                    onValueChange = { remarksInputs[student.id] = it },
                                    placeholder = { Text("Observation", fontSize = 11.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(48.dp)
                                )
                            }
                            HorizontalDivider(color = Slate100)
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}
