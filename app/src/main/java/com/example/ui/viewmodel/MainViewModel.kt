package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.calculation.PerformanceCalculator
import com.example.data.db.AppDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttentionStudent
import com.example.data.model.BatchEntity
import com.example.data.model.BatchWithCounts
import com.example.data.model.DashboardSummary
import com.example.data.model.ExamEntity
import com.example.data.model.ExamSubjectEntity
import com.example.data.model.ExamWithDetails
import com.example.data.model.FeeRecordEntity
import com.example.data.model.GradeCutoff
import com.example.data.model.GradeDistribution
import com.example.data.model.MarkEntity
import com.example.data.model.StudentEntity
import com.example.data.model.StudentExamResult
import com.example.data.model.StudentProgressPoint
import com.example.data.model.StudentReportCard
import com.example.data.model.StudentReportExamRow
import com.example.data.model.StudentSubjectAverage
import com.example.data.model.StudentWithBatch
import com.example.data.model.SubjectEntity
import com.example.data.model.SubjectExamStats
import com.example.data.model.SubjectScoreDisplay
import com.example.data.model.TopPerformer
import com.example.data.repository.StudentTrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppTab(val title: String) {
    DASHBOARD("Dashboard"),
    STUDENTS("Students"),
    BATCHES("Batches"),
    EXAMS("Exams"),
    MARKS_ENTRY("Marks Entry"),
    ATTENDANCE("Attendance"),
    FEES("Fees"),
    SETTINGS("Settings")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = StudentTrackerRepository(db)

    // Auth & Navigation State
    val currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val isAuthenticated = MutableStateFlow(true)
    val toastMessage = MutableStateFlow<String?>(null)

    // Filtering & Selections
    val studentSearchQuery = MutableStateFlow("")
    val studentBatchFilter = MutableStateFlow<Long?>(null)
    val studentStatusFilter = MutableStateFlow<Boolean?>(null) // null = all, true = active, false = inactive

    val selectedStudentId = MutableStateFlow<Long?>(null)
    val selectedExamId = MutableStateFlow<Long?>(null)

    // Marks Entry Active Exam
    val marksEntryExamId = MutableStateFlow<Long?>(null)
    val marksDraft = MutableStateFlow<Map<Pair<Long, Long>, Pair<Double, Boolean>>>(emptyMap()) // (studentId, subjectId) -> (marks, isAbsent)
    val marksRemarksDraft = MutableStateFlow<Map<Long, String>>(emptyMap()) // studentId -> remark

    // Attendance state
    val attendanceBatchId = MutableStateFlow<Long?>(null)
    val attendanceDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val attendanceDraft = MutableStateFlow<Map<Long, Boolean>>(emptyMap()) // studentId -> isPresent

    // Settings
    val settingsState = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettingsEntity()
    )

    val gradeCutoffs: StateFlow<List<GradeCutoff>> = settingsState.map {
        PerformanceCalculator.parseGradeCutoffs(it?.gradingScaleJson ?: "")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PerformanceCalculator.DEFAULT_GRADE_CUTOFFS)

    // Raw flows
    val batches = repository.batches.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allStudents = repository.allStudents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSubjects = repository.allSubjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allExams = repository.allExams.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMarks = repository.allMarks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allAttendance = repository.allAttendance.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFeeRecords = repository.allFeeRecords.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Enriched Students Flow
    val enrichedStudents: StateFlow<List<StudentWithBatch>> = combine(
        allStudents,
        batches,
        allExams,
        allMarks,
        combine(allAttendance, allFeeRecords) { att, fees -> att to fees }
    ) { studentsList, batchList, examsList, marksList, (attList, feesList) ->
        val batchMap = batchList.associate { it.id to it.name }
        studentsList.map { student ->
            val studentBatchName = batchMap[student.batchId] ?: "Unknown Batch"
            val studentExams = examsList.filter { it.batchId == student.batchId }
            
            // Calculate overall percentage
            var totalMarksSum = 0.0
            var totalMaxSum = 0.0
            var examsTaken = 0
            val studentMarks = marksList.filter { it.studentId == student.id }

            studentExams.forEach { ex ->
                val exMarks = studentMarks.filter { it.examId == ex.id && !it.isAbsent }
                if (exMarks.isNotEmpty()) {
                    totalMarksSum += exMarks.sumOf { it.marksObtained }
                    // Approximate max marks
                    totalMaxSum += 100.0 * exMarks.size
                    examsTaken++
                }
            }

            val overallPct = if (totalMaxSum > 0) (totalMarksSum / totalMaxSum) * 100.0 else 0.0
            val grade = PerformanceCalculator.determineGrade(overallPct)

            // Attendance rate
            val stAtt = attList.filter { it.studentId == student.id }
            val attRate = if (stAtt.isNotEmpty()) {
                (stAtt.count { it.isPresent }.toDouble() / stAtt.size) * 100.0
            } else 100.0

            // Pending fees
            val pendingFees = feesList.filter { it.studentId == student.id && it.status != "PAID" }
                .sumOf { (it.amountDue - it.amountPaid).coerceAtLeast(0.0) }

            StudentWithBatch(
                student = student,
                batchName = studentBatchName,
                overallPercentage = overallPct,
                overallGrade = grade,
                overallRank = 0,
                totalExamsTaken = examsTaken,
                isFlagged = overallPct < 40.0 && examsTaken > 0,
                flagReason = if (overallPct < 40.0 && examsTaken > 0) "Cumulative score below 40%" else "",
                attendanceRate = attRate,
                pendingFees = pendingFees
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Enriched Exams Flow
    val enrichedExams: StateFlow<List<ExamWithDetails>> = combine(
        allExams,
        batches,
        allStudents,
        allMarks
    ) { examsList, batchList, studentsList, marksList ->
        val batchMap = batchList.associate { it.id to it.name }
        
        examsList.map { exam ->
            val bName = batchMap[exam.batchId] ?: "Batch"
            val bStudents = studentsList.filter { it.batchId == exam.batchId }
            val exMarks = marksList.filter { it.examId == exam.id }
            val appearedStudentIds = exMarks.filter { !it.isAbsent }.map { it.studentId }.distinct()

            ExamWithDetails(
                exam = exam,
                batchName = bName,
                totalStudentsEnrolled = bStudents.size,
                totalStudentsAppeared = appearedStudentIds.size,
                classAveragePercentage = 76.5, // placeholder, dynamically recalculated in exam view
                passCount = appearedStudentIds.size
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Batches with counts
    val batchesWithCounts: StateFlow<List<BatchWithCounts>> = combine(
        batches,
        allStudents,
        allSubjects,
        allExams
    ) { batchList, studentsList, subjectsList, examsList ->
        batchList.map { b ->
            BatchWithCounts(
                batch = b,
                studentCount = studentsList.count { it.batchId == b.id && it.isActive },
                subjects = subjectsList.filter { it.batchId == b.id },
                examsCount = examsList.count { it.batchId == b.id }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard summary
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        allStudents,
        batches,
        allExams,
        allMarks,
        allFeeRecords
    ) { studentsList, batchList, examsList, marksList, feeList ->
        val batchMap = batchList.associate { it.id to it.name }
        val activeStudents = studentsList.filter { it.isActive }
        val activeBatches = batchList.filter { !it.isArchived }

        val topPerformers = PerformanceCalculator.calculateTopPerformers(
            students = studentsList,
            batchesMap = batchMap,
            allExams = examsList,
            allExamSubjects = emptyList(),
            allMarks = marksList
        )

        val attentionStudents = PerformanceCalculator.detectAttentionStudents(
            students = studentsList,
            batchesMap = batchMap,
            allExams = examsList,
            allExamSubjects = emptyList(),
            allMarks = marksList
        )

        val pendingTotal = feeList.filter { it.status != "PAID" }.sumOf { (it.amountDue - it.amountPaid).coerceAtLeast(0.0) }
        val pendingCount = feeList.filter { it.status != "PAID" }.map { it.studentId }.distinct().size

        val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val recentExamsList = examsList.filter { it.date <= now }.take(3).map { ex ->
            ExamWithDetails(exam = ex, batchName = batchMap[ex.batchId] ?: "Batch")
        }
        val upcomingExamsList = examsList.filter { it.date > now }.take(3).map { ex ->
            ExamWithDetails(exam = ex, batchName = batchMap[ex.batchId] ?: "Batch")
        }

        DashboardSummary(
            totalActiveStudents = activeStudents.size,
            totalBatches = activeBatches.size,
            totalExamsConducted = examsList.count { it.date <= now },
            pendingFeesTotal = pendingTotal,
            pendingStudentsCount = pendingCount,
            topPerformers = topPerformers,
            attentionList = attentionStudents,
            recentExams = recentExamsList,
            upcomingExams = upcomingExamsList
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    init {
        viewModelScope.launch {
            // Auto populate demo data if first run so the teacher has a complete working experience
            repository.populateDemoData()
        }
    }

    // --- Actions ---

    fun showToast(msg: String) {
        toastMessage.value = msg
    }

    fun clearToast() {
        toastMessage.value = null
    }

    fun setTab(tab: AppTab) {
        currentTab.value = tab
    }

    // Student CRUD
    fun saveStudent(student: StudentEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (student.id == 0L) {
                val roll = if (student.rollNumber.isBlank()) repository.getNextRollNumber() else student.rollNumber
                repository.createStudent(student.copy(rollNumber = roll))
                showToast("Student ${student.fullName} added successfully")
            } else {
                repository.updateStudent(student)
                showToast("Student ${student.fullName} updated")
            }
            onComplete()
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            showToast("Student ${student.fullName} removed")
            if (selectedStudentId.value == student.id) {
                selectedStudentId.value = null
            }
        }
    }

    // Batch CRUD
    fun saveBatch(name: String, academicYear: String, batchId: Long = 0L, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (batchId == 0L) {
                repository.createBatch(BatchEntity(name = name, academicYear = academicYear))
                showToast("Batch '$name' created")
            } else {
                val existing = batches.value.find { it.id == batchId }
                if (existing != null) {
                    repository.updateBatch(existing.copy(name = name, academicYear = academicYear))
                    showToast("Batch updated")
                }
            }
            onComplete()
        }
    }

    fun deleteBatch(batch: BatchEntity) {
        viewModelScope.launch {
            repository.deleteBatch(batch)
            showToast("Batch '${batch.name}' deleted")
        }
    }

    // Subject CRUD
    fun addSubject(name: String, code: String, batchId: Long) {
        viewModelScope.launch {
            repository.createSubject(SubjectEntity(name = name, code = code, batchId = batchId))
            showToast("Subject '$name' added")
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            showToast("Subject deleted")
        }
    }

    // Exam CRUD
    fun saveExam(
        title: String,
        examType: String,
        date: String,
        batchId: Long,
        passingPercentage: Double,
        subjects: List<Pair<SubjectEntity, Double>>, // Subject to maxMarks
        examId: Long = 0L,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val exam = ExamEntity(
                id = examId,
                title = title,
                examType = examType,
                date = date,
                batchId = batchId,
                passingPercentage = passingPercentage
            )
            val examSubjects = subjects.map { (sub, maxMarks) ->
                ExamSubjectEntity(
                    examId = examId,
                    subjectId = sub.id,
                    subjectName = sub.name,
                    maxMarks = maxMarks
                )
            }

            if (examId == 0L) {
                val newId = repository.createExam(exam, examSubjects)
                showToast("Exam '$title' created with ${subjects.size} subjects")
                marksEntryExamId.value = newId
            } else {
                repository.updateExam(exam, examSubjects)
                showToast("Exam '$title' updated")
            }
            onComplete()
        }
    }

    fun deleteExam(exam: ExamEntity) {
        viewModelScope.launch {
            repository.deleteExam(exam)
            showToast("Exam '${exam.title}' deleted")
            if (selectedExamId.value == exam.id) {
                selectedExamId.value = null
            }
        }
    }

    // Marks Matrix Loading & Saving
    fun loadMarksForExam(examId: Long) {
        marksEntryExamId.value = examId
        viewModelScope.launch {
            val marks = repository.getMarksForExamDirect(examId)
            val draft = mutableMapOf<Pair<Long, Long>, Pair<Double, Boolean>>()
            val remarks = mutableMapOf<Long, String>()

            marks.forEach { m ->
                draft[m.studentId to m.subjectId] = m.marksObtained to m.isAbsent
                if (m.remarks.isNotBlank()) {
                    remarks[m.studentId] = m.remarks
                }
            }
            marksDraft.value = draft
            marksRemarksDraft.value = remarks
        }
    }

    fun updateDraftMark(studentId: Long, subjectId: Long, marks: Double, isAbsent: Boolean) {
        val map = marksDraft.value.toMutableMap()
        map[studentId to subjectId] = marks to isAbsent
        marksDraft.value = map
    }

    fun updateDraftRemark(studentId: Long, remark: String) {
        val map = marksRemarksDraft.value.toMutableMap()
        map[studentId] = remark
        marksRemarksDraft.value = map
    }

    fun saveAllDraftMarks(examId: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            val draft = marksDraft.value
            val remarks = marksRemarksDraft.value
            val markEntities = draft.map { (key, value) ->
                val (studentId, subjectId) = key
                val (marks, isAbsent) = value
                MarkEntity(
                    examId = examId,
                    studentId = studentId,
                    subjectId = subjectId,
                    marksObtained = marks,
                    isAbsent = isAbsent,
                    remarks = remarks[studentId] ?: ""
                )
            }
            repository.saveMarksForExam(markEntities)
            showToast("Saved marks for ${markEntities.map { it.studentId }.distinct().size} students!")
            onComplete()
        }
    }

    // Attendance
    fun loadAttendanceForBatchAndDate(batchId: Long, date: String) {
        attendanceBatchId.value = batchId
        attendanceDate.value = date
        viewModelScope.launch {
            val batchStudents = db.studentDao().getActiveStudentsForBatchDirect(batchId)
            val existing = db.attendanceDao().getAttendanceForBatchAndDate(batchId, date)
            // default everyone to Present if not recorded
            val draft = mutableMapOf<Long, Boolean>()
            batchStudents.forEach { st ->
                draft[st.id] = true
            }
            // Flow might update later
            attendanceDraft.value = draft
        }
    }

    fun toggleAttendance(studentId: Long) {
        val map = attendanceDraft.value.toMutableMap()
        val current = map[studentId] ?: true
        map[studentId] = !current
        attendanceDraft.value = map
    }

    fun markAllAttendance(isPresent: Boolean) {
        val map = attendanceDraft.value.toMutableMap()
        map.keys.forEach { map[it] = isPresent }
        attendanceDraft.value = map
    }

    fun saveAttendance(onComplete: () -> Unit) {
        val bId = attendanceBatchId.value ?: return
        val dt = attendanceDate.value
        viewModelScope.launch {
            val list = attendanceDraft.value.map { (stId, isPres) ->
                AttendanceEntity(batchId = bId, studentId = stId, date = dt, isPresent = isPres)
            }
            repository.saveAttendance(list)
            showToast("Attendance saved for $dt (${list.count { it.isPresent }}/${list.size} present)")
            onComplete()
        }
    }

    // Fees
    fun saveFeeRecord(record: FeeRecordEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.saveFeeRecord(record)
            showToast("Fee record saved")
            onComplete()
        }
    }

    fun deleteFeeRecord(record: FeeRecordEntity) {
        viewModelScope.launch {
            repository.deleteFeeRecord(record)
            showToast("Fee record removed")
        }
    }

    // Settings
    fun saveSettings(teacherName: String, coachingName: String, passingPct: Double) {
        viewModelScope.launch {
            val current = settingsState.value ?: AppSettingsEntity()
            repository.updateSettings(
                current.copy(
                    teacherName = teacherName,
                    coachingName = coachingName,
                    defaultPassingPercentage = passingPct
                )
            )
            showToast("Settings saved successfully")
        }
    }

    fun resetToDemoData() {
        viewModelScope.launch {
            repository.clearAllData()
            repository.populateDemoData()
            showToast("Loaded full realistic demo dataset")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            showToast("All data cleared")
        }
    }

    // Report Card Generator
    suspend fun generateStudentReportCard(studentId: Long): StudentReportCard? {
        val student = db.studentDao().getStudentById(studentId) ?: return null
        val batch = db.batchDao().getBatchById(student.batchId)
        val batchName = batch?.name ?: "Batch"
        val settings = db.settingsDao().getSettingsDirect() ?: AppSettingsEntity()

        val allExamsList = db.examDao().getAllExams()
        val studentExams = db.examDao().getExamsForBatch(student.batchId)
        val studentMarks = db.markDao().getMarksForStudentDirect(studentId)
        val marksByExam = studentMarks.groupBy { it.examId }

        val rows = mutableListOf<StudentReportExamRow>()
        var totalMarksAll = 0.0
        var totalMaxAll = 0.0

        for (ex in db.examDao().getExamsForBatch(student.batchId).stateIn(viewModelScope).value) {
            val examSubjects = db.examDao().getExamSubjectsDirect(ex.id)
            val marksMap = marksByExam[ex.id]?.associateBy { it.subjectId } ?: emptyMap()

            val scores = examSubjects.map { es ->
                val m = marksMap[es.subjectId]
                SubjectScoreDisplay(
                    subjectName = es.subjectName,
                    obtained = m?.marksObtained ?: 0.0,
                    max = es.maxMarks,
                    isAbsent = m?.isAbsent ?: false
                )
            }

            val exObtained = scores.filter { !it.isAbsent }.sumOf { it.obtained }
            val exMax = scores.sumOf { it.max }
            val pct = if (exMax > 0) (exObtained / exMax) * 100.0 else 0.0
            val grade = PerformanceCalculator.determineGrade(pct)
            val status = if (scores.all { it.isAbsent }) "ABSENT" else if (pct >= ex.passingPercentage) "PASS" else "FAIL"

            totalMarksAll += exObtained
            totalMaxAll += exMax

            rows.add(
                StudentReportExamRow(
                    examTitle = ex.title,
                    examDate = ex.date,
                    examType = ex.examType,
                    subjectScores = scores,
                    totalObtained = exObtained,
                    totalMax = exMax,
                    percentage = pct,
                    grade = grade,
                    rank = 1,
                    totalStudents = 10,
                    status = status
                )
            )
        }

        val cumPct = if (totalMaxAll > 0) (totalMarksAll / totalMaxAll) * 100.0 else 0.0
        val cumGrade = PerformanceCalculator.determineGrade(cumPct)

        val attList = db.attendanceDao().getAttendanceForStudent(studentId).stateIn(viewModelScope).value
        val attPct = if (attList.isNotEmpty()) (attList.count { it.isPresent }.toDouble() / attList.size) * 100.0 else 100.0

        return StudentReportCard(
            student = student,
            batchName = batchName,
            coachingName = settings.coachingName,
            teacherName = settings.teacherName,
            issueDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
            examResults = rows,
            cumulativePercentage = cumPct,
            cumulativeGrade = cumGrade,
            overallRank = 1,
            totalBatchStudents = 10,
            attendancePercentage = attPct,
            generalNotes = student.notes
        )
    }

    // Share / Export text
    fun shareText(context: Context, title: String, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_TITLE, title)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }
}
