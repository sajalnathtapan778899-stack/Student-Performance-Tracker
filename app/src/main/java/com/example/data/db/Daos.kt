package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.BatchEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ExamSubjectEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.MarkEntity
import com.example.data.model.StudentEntity
import com.example.data.model.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchDao {
    @Query("SELECT * FROM batches ORDER BY name ASC")
    fun getAllBatches(): Flow<List<BatchEntity>>

    @Query("SELECT * FROM batches WHERE isArchived = 0 ORDER BY name ASC")
    fun getActiveBatches(): Flow<List<BatchEntity>>

    @Query("SELECT * FROM batches WHERE id = :id")
    suspend fun getBatchById(id: Long): BatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity): Long

    @Update
    suspend fun updateBatch(batch: BatchEntity)

    @Delete
    suspend fun deleteBatch(batch: BatchEntity)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE batchId = :batchId ORDER BY name ASC")
    fun getSubjectsForBatch(batchId: Long): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE batchId = :batchId ORDER BY name ASC")
    suspend fun getSubjectsForBatchDirect(batchId: Long): List<SubjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY rollNumber ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE batchId = :batchId ORDER BY rollNumber ASC")
    fun getStudentsForBatch(batchId: Long): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE batchId = :batchId AND isActive = 1 ORDER BY rollNumber ASC")
    suspend fun getActiveStudentsForBatchDirect(batchId: Long): List<StudentEntity>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentByIdFlow(id: Long): Flow<StudentEntity?>

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)
}

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY date DESC, id DESC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE batchId = :batchId ORDER BY date DESC")
    fun getExamsForBatch(batchId: Long): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE id = :id")
    suspend fun getExamById(id: Long): ExamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Delete
    suspend fun deleteExam(exam: ExamEntity)

    // Exam Subjects
    @Query("SELECT * FROM exam_subjects WHERE examId = :examId")
    fun getExamSubjects(examId: Long): Flow<List<ExamSubjectEntity>>

    @Query("SELECT * FROM exam_subjects WHERE examId = :examId")
    suspend fun getExamSubjectsDirect(examId: Long): List<ExamSubjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamSubjects(subjects: List<ExamSubjectEntity>)

    @Query("DELETE FROM exam_subjects WHERE examId = :examId")
    suspend fun deleteExamSubjects(examId: Long)
}

@Dao
interface MarkDao {
    @Query("SELECT * FROM marks WHERE examId = :examId")
    fun getMarksForExam(examId: Long): Flow<List<MarkEntity>>

    @Query("SELECT * FROM marks WHERE examId = :examId")
    suspend fun getMarksForExamDirect(examId: Long): List<MarkEntity>

    @Query("SELECT * FROM marks WHERE studentId = :studentId")
    fun getMarksForStudent(studentId: Long): Flow<List<MarkEntity>>

    @Query("SELECT * FROM marks WHERE studentId = :studentId")
    suspend fun getMarksForStudentDirect(studentId: Long): List<MarkEntity>

    @Query("SELECT * FROM marks")
    fun getAllMarks(): Flow<List<MarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarks(marks: List<MarkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMark(mark: MarkEntity)

    @Query("DELETE FROM marks WHERE examId = :examId")
    suspend fun deleteMarksForExam(examId: Long)

    @Query("DELETE FROM marks WHERE studentId = :studentId")
    suspend fun deleteMarksForStudent(studentId: Long)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE batchId = :batchId AND date = :date")
    fun getAttendanceForBatchAndDate(batchId: Long, date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)
}

@Dao
interface FeeDao {
    @Query("SELECT * FROM fee_records ORDER BY dueDate DESC")
    fun getAllFeeRecords(): Flow<List<FeeRecordEntity>>

    @Query("SELECT * FROM fee_records WHERE studentId = :studentId ORDER BY dueDate DESC")
    fun getFeeRecordsForStudent(studentId: Long): Flow<List<FeeRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecord(record: FeeRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecords(records: List<FeeRecordEntity>)

    @Update
    suspend fun updateFeeRecord(record: FeeRecordEntity)

    @Delete
    suspend fun deleteFeeRecord(record: FeeRecordEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettingsDirect(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AppSettingsEntity)
}
