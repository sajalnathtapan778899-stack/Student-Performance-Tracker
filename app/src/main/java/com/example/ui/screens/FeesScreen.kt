package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FeeRecordEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber600
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
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
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val feeRecords by viewModel.allFeeRecords.collectAsStateWithLifecycle()
    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val studentMap = remember(students) { students.associateBy { it.id } }

    var filterStatus by remember { mutableStateOf<String?>(null) } // null = all, PAID, PENDING, OVERDUE
    var isAddFeeDialogOpen by remember { mutableStateOf(false) }
    var feeToEdit by remember { mutableStateOf<FeeRecordEntity?>(null) }

    val totalDue = feeRecords.sumOf { it.amountDue }
    val totalPaid = feeRecords.sumOf { it.amountPaid }
    val totalPending = totalDue - totalPaid

    val filteredRecords = feeRecords.filter { record ->
        filterStatus == null || record.status.equals(filterStatus, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    feeToEdit = null
                    isAddFeeDialogOpen = true
                },
                containerColor = RoyalBlue700,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_fee_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Record Fee")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Record Fee", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header & Overview Stats
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tuition & Coaching Fee Management",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "Track monthly coaching dues, partial receipts, and outstanding balances",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate100,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Billed", fontSize = 11.sp, color = Slate500)
                            Text("₹${totalDue.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Emerald50,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Collected", fontSize = 11.sp, color = Emerald600)
                            Text("₹${totalPaid.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald600)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Amber50,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Pending", fontSize = 11.sp, color = Amber600)
                            Text("₹${totalPending.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Amber600)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter status chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(label = "All Records (${feeRecords.size})", isSelected = filterStatus == null, onClick = { filterStatus = null })
                    }
                    item {
                        val pendingCount = feeRecords.count { it.status == "PENDING" }
                        FilterChip(label = "Pending ($pendingCount)", isSelected = filterStatus == "PENDING", onClick = { filterStatus = "PENDING" })
                    }
                    item {
                        val overdueCount = feeRecords.count { it.status == "OVERDUE" }
                        FilterChip(label = "Overdue ($overdueCount)", isSelected = filterStatus == "OVERDUE", onClick = { filterStatus = "OVERDUE" })
                    }
                    item {
                        val paidCount = feeRecords.count { it.status == "PAID" }
                        FilterChip(label = "Paid ($paidCount)", isSelected = filterStatus == "PAID", onClick = { filterStatus = "PAID" })
                    }
                }
            }

            HorizontalDivider(color = Slate200)

            if (filteredRecords.isEmpty()) {
                EmptyStateView(
                    title = "No Fee Records Found",
                    subtitle = "Tap 'Record Fee' to create a monthly fee invoice for a student.",
                    icon = Icons.Default.Payment
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredRecords, key = { it.id }) { record ->
                        val student = studentMap[record.studentId]

                        FeeRecordCardItem(
                            record = record,
                            student = student,
                            onMarkPaid = {
                                viewModel.saveFeeRecord(
                                    record.copy(
                                        amountPaid = record.amountDue,
                                        status = "PAID",
                                        paymentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                    )
                                ) {}
                            },
                            onDelete = {
                                viewModel.deleteFeeRecord(record)
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

    // Add / Edit Fee Dialog
    if (isAddFeeDialogOpen) {
        AddEditFeeDialog(
            record = feeToEdit,
            students = students,
            onDismiss = { isAddFeeDialogOpen = false },
            onSave = { newRecord ->
                viewModel.saveFeeRecord(newRecord) {
                    isAddFeeDialogOpen = false
                }
            }
        )
    }
}

@Composable
fun FeeRecordCardItem(
    record: FeeRecordEntity,
    student: StudentEntity?,
    onMarkPaid: () -> Unit,
    onDelete: () -> Unit
) {
    val (statusBg, statusFg) = when (record.status.uppercase(Locale.getDefault())) {
        "PAID" -> Emerald50 to Emerald600
        "OVERDUE" -> Rose50 to Rose600
        else -> Amber50 to Amber600
    }

    val isPending = record.status != "PAID"
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    StudentAvatar(
                        name = student?.fullName ?: "Student",
                        size = 38.dp,
                        gender = student?.gender ?: "Male"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = student?.fullName ?: "Unknown Student",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "${record.monthPeriod} • Due: ${record.dueDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg
                ) {
                    Text(
                        text = record.status,
                        color = statusFg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Amount: ₹${record.amountDue.toInt()} (Paid: ₹${record.amountPaid.toInt()})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Slate700
                    )
                    if (!record.paymentDate.isNullOrBlank()) {
                        Text(
                            text = "Paid on: ${record.paymentDate} (${record.paymentMethod})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPending) {
                        Button(
                            onClick = onMarkPaid,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark Paid", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Slate400, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Fee Record?") },
            text = { Text("Are you sure you want to delete this fee invoice?") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFeeDialog(
    record: FeeRecordEntity?,
    students: List<StudentEntity>,
    onDismiss: () -> Unit,
    onSave: (FeeRecordEntity) -> Unit
) {
    var selectedStudentId by remember { mutableStateOf(record?.studentId ?: students.firstOrNull()?.id ?: 0L) }
    var monthPeriod by remember { mutableStateOf(record?.monthPeriod ?: SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())) }
    var amountDueText by remember { mutableStateOf((record?.amountDue ?: 2500.0).toInt().toString()) }
    var amountPaidText by remember { mutableStateOf((record?.amountPaid ?: 0.0).toInt().toString()) }
    var dueDate by remember { mutableStateOf(record?.dueDate ?: SimpleDateFormat("yyyy-MM-10", Locale.getDefault()).format(Date())) }
    var paymentMethod by remember { mutableStateOf(record?.paymentMethod ?: "UPI") }
    var remarks by remember { mutableStateOf(record?.remarks ?: "") }

    var isStudentExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (record == null) "Record Fee Invoice" else "Edit Fee Record",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Student Selector
                ExposedDropdownMenuBox(
                    expanded = isStudentExpanded,
                    onExpandedChange = { isStudentExpanded = it }
                ) {
                    val stName = students.find { it.id == selectedStudentId }?.fullName ?: "Select Student"
                    OutlinedTextField(
                        value = stName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Student *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStudentExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isStudentExpanded,
                        onDismissRequest = { isStudentExpanded = false }
                    ) {
                        students.forEach { st ->
                            DropdownMenuItem(
                                text = { Text("${st.fullName} (${st.rollNumber})") },
                                onClick = {
                                    selectedStudentId = st.id
                                    isStudentExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = monthPeriod,
                    onValueChange = { monthPeriod = it },
                    label = { Text("Fee Period / Month") },
                    placeholder = { Text("e.g. August 2026") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = amountDueText,
                        onValueChange = { amountDueText = it },
                        label = { Text("Amount Due (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = amountPaidText,
                        onValueChange = { amountPaidText = it },
                        label = { Text("Amount Paid (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

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
                            val due = amountDueText.toDoubleOrNull() ?: 0.0
                            val paid = amountPaidText.toDoubleOrNull() ?: 0.0
                            val status = when {
                                paid >= due -> "PAID"
                                paid > 0 -> "PARTIAL"
                                else -> "PENDING"
                            }
                            val pDate = if (paid > 0) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) else ""

                            onSave(
                                FeeRecordEntity(
                                    id = record?.id ?: 0L,
                                    studentId = selectedStudentId,
                                    monthPeriod = monthPeriod.trim(),
                                    amountDue = due,
                                    amountPaid = paid,
                                    dueDate = dueDate.trim(),
                                    paymentDate = pDate,
                                    status = status,
                                    paymentMethod = paymentMethod,
                                    remarks = remarks.trim()
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (record == null) "Record Fee" else "Update Fee")
                    }
                }
            }
        }
    }
}
