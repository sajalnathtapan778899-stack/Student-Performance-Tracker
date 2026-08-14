package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.BatchEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ExamSubjectEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.MarkEntity
import com.example.data.model.StudentEntity
import com.example.data.model.SubjectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StudentTrackerRepository(private val db: AppDatabase) {

    val batches: Flow<List<BatchEntity>> = db.batchDao().getAllBatches()
    val activeBatches: Flow<List<BatchEntity>> = db.batchDao().getActiveBatches()
    val allStudents: Flow<List<StudentEntity>> = db.studentDao().getAllStudents()
    val allSubjects: Flow<List<SubjectEntity>> = db.subjectDao().getAllSubjects()
    val allExams: Flow<List<ExamEntity>> = db.examDao().getAllExams()
    val allMarks: Flow<List<MarkEntity>> = db.markDao().getAllMarks()
    val allAttendance: Flow<List<AttendanceEntity>> = db.attendanceDao().getAllAttendance()
    val allFeeRecords: Flow<List<FeeRecordEntity>> = db.feeDao().getAllFeeRecords()
    val settings: Flow<AppSettingsEntity?> = db.settingsDao().getSettings()

    // Batches
    suspend fun createBatch(batch: BatchEntity): Long = withContext(Dispatchers.IO) {
        db.batchDao().insertBatch(batch)
    }

    suspend fun updateBatch(batch: BatchEntity) = withContext(Dispatchers.IO) {
        db.batchDao().updateBatch(batch)
    }

    suspend fun deleteBatch(batch: BatchEntity) = withContext(Dispatchers.IO) {
        db.batchDao().deleteBatch(batch)
    }

    // Subjects
    fun getSubjectsForBatch(batchId: Long): Flow<List<SubjectEntity>> = db.subjectDao().getSubjectsForBatch(batchId)

    suspend fun createSubject(subject: SubjectEntity): Long = withContext(Dispatchers.IO) {
        db.subjectDao().insertSubject(subject)
    }

    suspend fun deleteSubject(subject: SubjectEntity) = withContext(Dispatchers.IO) {
        db.subjectDao().deleteSubject(subject)
    }

    // Students
    suspend fun getNextRollNumber(): String = withContext(Dispatchers.IO) {
        val count = db.studentDao().getStudentCount()
        String.format("STU-%03d", count + 1)
    }

    suspend fun createStudent(student: StudentEntity): Long = withContext(Dispatchers.IO) {
        db.studentDao().insertStudent(student)
    }

    suspend fun updateStudent(student: StudentEntity) = withContext(Dispatchers.IO) {
        db.studentDao().updateStudent(student)
    }

    suspend fun deleteStudent(student: StudentEntity) = withContext(Dispatchers.IO) {
        db.studentDao().deleteStudent(student)
    }

    fun getStudentFlow(studentId: Long): Flow<StudentEntity?> = db.studentDao().getStudentByIdFlow(studentId)

    // Exams
    suspend fun createExam(exam: ExamEntity, subjects: List<ExamSubjectEntity>): Long = withContext(Dispatchers.IO) {
        val examId = db.examDao().insertExam(exam)
        val examSubjectsWithId = subjects.map { it.copy(examId = examId) }
        db.examDao().insertExamSubjects(examSubjectsWithId)
        examId
    }

    suspend fun updateExam(exam: ExamEntity, subjects: List<ExamSubjectEntity>) = withContext(Dispatchers.IO) {
        db.examDao().updateExam(exam)
        db.examDao().deleteExamSubjects(exam.id)
        val examSubjectsWithId = subjects.map { it.copy(examId = exam.id) }
        db.examDao().insertExamSubjects(examSubjectsWithId)
    }

    suspend fun deleteExam(exam: ExamEntity) = withContext(Dispatchers.IO) {
        db.examDao().deleteExam(exam)
    }

    fun getExamSubjects(examId: Long): Flow<List<ExamSubjectEntity>> = db.examDao().getExamSubjects(examId)

    suspend fun getExamSubjectsDirect(examId: Long): List<ExamSubjectEntity> = withContext(Dispatchers.IO) {
        db.examDao().getExamSubjectsDirect(examId)
    }

    // Marks
    fun getMarksForExam(examId: Long): Flow<List<MarkEntity>> = db.markDao().getMarksForExam(examId)

    suspend fun getMarksForExamDirect(examId: Long): List<MarkEntity> = withContext(Dispatchers.IO) {
        db.markDao().getMarksForExamDirect(examId)
    }

    suspend fun saveMarksForExam(marks: List<MarkEntity>) = withContext(Dispatchers.IO) {
        db.markDao().insertMarks(marks)
    }

    // Attendance
    suspend fun saveAttendance(records: List<AttendanceEntity>) = withContext(Dispatchers.IO) {
        db.attendanceDao().insertAttendanceList(records)
    }

    // Fees
    suspend fun saveFeeRecord(record: FeeRecordEntity) = withContext(Dispatchers.IO) {
        if (record.id == 0L) {
            db.feeDao().insertFeeRecord(record)
        } else {
            db.feeDao().updateFeeRecord(record)
        }
    }

    suspend fun deleteFeeRecord(record: FeeRecordEntity) = withContext(Dispatchers.IO) {
        db.feeDao().deleteFeeRecord(record)
    }

    // Settings
    suspend fun updateSettings(settings: AppSettingsEntity) = withContext(Dispatchers.IO) {
        db.settingsDao().insertOrUpdateSettings(settings)
    }

    // Demo Data
    suspend fun populateDemoData() = withContext(Dispatchers.IO) {
        SampleDataGenerator.populateRealisticDemoData(db)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        db.clearAllTables()
    }
}
