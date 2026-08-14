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
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SampleDataGenerator {

    suspend fun populateRealisticDemoData(db: AppDatabase) = withContext(Dispatchers.IO) {
        // Clear old demo data
        val existingStudents = db.studentDao().getStudentCount()
        if (existingStudents > 0) return@withContext

        // 1. Settings
        val settings = AppSettingsEntity(
            id = 1,
            teacherName = "Prof. Rajesh Sharma",
            teacherEmail = "r.sharma@apexacademy.edu",
            coachingName = "Apex Science & Math Academy",
            defaultPassingPercentage = 40.0
        )
        db.settingsDao().insertOrUpdateSettings(settings)

        // 2. Batches
        val batch1Id = db.batchDao().insertBatch(
            BatchEntity(name = "Class 10 – Science & Math Batch A", academicYear = "2026-2027")
        )
        val batch2Id = db.batchDao().insertBatch(
            BatchEntity(name = "Class 12 – JEE Foundation Evening", academicYear = "2026-2027")
        )
        val batch3Id = db.batchDao().insertBatch(
            BatchEntity(name = "Class 9 – High Achievers Weekend", academicYear = "2026-2027")
        )

        // 3. Subjects for Batch 1
        val math1 = db.subjectDao().insertSubject(SubjectEntity(name = "Mathematics", code = "MATH10", batchId = batch1Id))
        val phys1 = db.subjectDao().insertSubject(SubjectEntity(name = "Physics", code = "PHY10", batchId = batch1Id))
        val chem1 = db.subjectDao().insertSubject(SubjectEntity(name = "Chemistry", code = "CHEM10", batchId = batch1Id))
        val bio1 = db.subjectDao().insertSubject(SubjectEntity(name = "Biology", code = "BIO10", batchId = batch1Id))
        val eng1 = db.subjectDao().insertSubject(SubjectEntity(name = "English", code = "ENG10", batchId = batch1Id))

        // Subjects for Batch 2
        val advMath2 = db.subjectDao().insertSubject(SubjectEntity(name = "Advanced Math", code = "AMATH12", batchId = batch2Id))
        val advPhys2 = db.subjectDao().insertSubject(SubjectEntity(name = "Advanced Physics", code = "APHY12", batchId = batch2Id))
        val advChem2 = db.subjectDao().insertSubject(SubjectEntity(name = "Physical Chemistry", code = "ACHEM12", batchId = batch2Id))

        // 4. Students for Batch 1
        val studentsBatch1 = listOf(
            StudentEntity(rollNumber = "STU-10-01", fullName = "Aarav Patel", gender = "Male", phone = "9876543210", parentName = "Sanjay Patel", parentPhone = "9876543211", address = "Flat 402, Green Meadows", batchId = batch1Id, admissionDate = "2026-04-10", notes = "Excellent mathematical intuition. Regularly solves bonus problems."),
            StudentEntity(rollNumber = "STU-10-02", fullName = "Diya Sengupta", gender = "Female", phone = "9876543212", parentName = "Anirban Sengupta", parentPhone = "9876543213", address = "12 Lake View Road", batchId = batch1Id, admissionDate = "2026-04-12", notes = "Consistently in the top 3. Very disciplined notes."),
            StudentEntity(rollNumber = "STU-10-03", fullName = "Rohan Verma", gender = "Male", phone = "9876543214", parentName = "Vikram Verma", parentPhone = "9876543215", address = "Sector 15, Block C", batchId = batch1Id, admissionDate = "2026-04-15", notes = "Struggling with Chemistry organic reactions. Needs extra revision."),
            StudentEntity(rollNumber = "STU-10-04", fullName = "Ananya Iyer", gender = "Female", phone = "9876543216", parentName = "Kalyan Iyer", parentPhone = "9876543217", address = "28 Palm Grove", batchId = batch1Id, admissionDate = "2026-04-18", notes = "Strong grasp in Biology & English. Aiming for medical entrance."),
            StudentEntity(rollNumber = "STU-10-05", fullName = "Kabir Deshmukh", gender = "Male", phone = "9876543218", parentName = "Mahesh Deshmukh", parentPhone = "9876543219", address = "78 Sunshine Enclave", batchId = batch1Id, admissionDate = "2026-04-20", notes = "Good effort, scores fluctuate based on regular homework submission."),
            StudentEntity(rollNumber = "STU-10-06", fullName = "Sneha Kulkarni", gender = "Female", phone = "9876543220", parentName = "Prashant Kulkarni", parentPhone = "9876543221", address = "90 Hill Crest", batchId = batch1Id, admissionDate = "2026-04-22", notes = "Recent score drop due to illness. Recovering well."),
            StudentEntity(rollNumber = "STU-10-07", fullName = "Ishaan Gupta", gender = "Male", phone = "9876543222", parentName = "Ramesh Gupta", parentPhone = "9876543223", address = "14 Rose Villa", batchId = batch1Id, admissionDate = "2026-04-25", notes = "Participates actively in classroom discussions."),
            StudentEntity(rollNumber = "STU-10-08", fullName = "Pooja Reddy", gender = "Female", phone = "9876543224", parentName = "Venkat Reddy", parentPhone = "9876543225", address = "56 Cyber City Layout", batchId = batch1Id, admissionDate = "2026-05-01", notes = "Needs practice with numerical physics problems.")
        )

        val insertedStudentsBatch1 = studentsBatch1.map {
            val id = db.studentDao().insertStudent(it)
            it.copy(id = id)
        }

        // Students for Batch 2
        val studentsBatch2 = listOf(
            StudentEntity(rollNumber = "JEE-12-01", fullName = "Aditya Chopra", gender = "Male", phone = "9876543230", parentName = "Rajesh Chopra", parentPhone = "9876543231", address = "602 Tower B, Skyline", batchId = batch2Id, admissionDate = "2026-04-01", notes = "Top candidate for Advanced."),
            StudentEntity(rollNumber = "JEE-12-02", fullName = "Meera Nair", gender = "Female", phone = "9876543232", parentName = "Murali Nair", parentPhone = "9876543233", address = "18 Coastal Heights", batchId = batch2Id, admissionDate = "2026-04-02", notes = "Sharp speed in mathematics problem solving."),
            StudentEntity(rollNumber = "JEE-12-03", fullName = "Tanmay Joshi", gender = "Male", phone = "9876543234", parentName = "Suresh Joshi", parentPhone = "9876543235", address = "45 Heritage Park", batchId = batch2Id, admissionDate = "2026-04-05", notes = "Consistent and calm test taker.")
        )
        val insertedStudentsBatch2 = studentsBatch2.map {
            val id = db.studentDao().insertStudent(it)
            it.copy(id = id)
        }

        // 5. Exams for Batch 1
        // Exam 1: Unit Test 1 (May)
        val exam1Id = db.examDao().insertExam(
            ExamEntity(title = "Unit Test 1 (Foundation)", examType = "Unit Test", date = "2026-05-20", batchId = batch1Id, passingPercentage = 40.0)
        )
        val ex1SubList = listOf(
            ExamSubjectEntity(examId = exam1Id, subjectId = math1, subjectName = "Mathematics", maxMarks = 50.0),
            ExamSubjectEntity(examId = exam1Id, subjectId = phys1, subjectName = "Physics", maxMarks = 50.0),
            ExamSubjectEntity(examId = exam1Id, subjectId = chem1, subjectName = "Chemistry", maxMarks = 50.0)
        )
        db.examDao().insertExamSubjects(ex1SubList)

        // Marks for Exam 1
        val exam1Marks = listOf(
            // Aarav (Top)
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[0].id, subjectId = math1, marksObtained = 48.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[0].id, subjectId = phys1, marksObtained = 46.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[0].id, subjectId = chem1, marksObtained = 47.0),
            // Diya (High)
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[1].id, subjectId = math1, marksObtained = 46.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[1].id, subjectId = phys1, marksObtained = 48.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[1].id, subjectId = chem1, marksObtained = 45.0),
            // Rohan (Average/Low Chem)
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[2].id, subjectId = math1, marksObtained = 35.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[2].id, subjectId = phys1, marksObtained = 32.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[2].id, subjectId = chem1, marksObtained = 22.0),
            // Ananya
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[3].id, subjectId = math1, marksObtained = 42.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[3].id, subjectId = phys1, marksObtained = 44.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[3].id, subjectId = chem1, marksObtained = 43.0),
            // Kabir
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[4].id, subjectId = math1, marksObtained = 38.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[4].id, subjectId = phys1, marksObtained = 36.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[4].id, subjectId = chem1, marksObtained = 34.0),
            // Sneha (Declining demo)
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[5].id, subjectId = math1, marksObtained = 41.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[5].id, subjectId = phys1, marksObtained = 40.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[5].id, subjectId = chem1, marksObtained = 39.0),
            // Ishaan
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[6].id, subjectId = math1, marksObtained = 30.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[6].id, subjectId = phys1, marksObtained = 28.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[6].id, subjectId = chem1, marksObtained = 26.0),
            // Pooja (Struggling/Fail in physics)
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[7].id, subjectId = math1, marksObtained = 24.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[7].id, subjectId = phys1, marksObtained = 18.0),
            MarkEntity(examId = exam1Id, studentId = insertedStudentsBatch1[7].id, subjectId = chem1, marksObtained = 22.0)
        )
        db.markDao().insertMarks(exam1Marks)

        // Exam 2: Monthly Test – June
        val exam2Id = db.examDao().insertExam(
            ExamEntity(title = "Monthly Test – June", examType = "Monthly Test", date = "2026-06-25", batchId = batch1Id, passingPercentage = 40.0)
        )
        val ex2SubList = listOf(
            ExamSubjectEntity(examId = exam2Id, subjectId = math1, subjectName = "Mathematics", maxMarks = 100.0),
            ExamSubjectEntity(examId = exam2Id, subjectId = phys1, subjectName = "Physics", maxMarks = 100.0),
            ExamSubjectEntity(examId = exam2Id, subjectId = chem1, subjectName = "Chemistry", maxMarks = 100.0),
            ExamSubjectEntity(examId = exam2Id, subjectId = bio1, subjectName = "Biology", maxMarks = 100.0)
        )
        db.examDao().insertExamSubjects(ex2SubList)

        val exam2Marks = listOf(
            // Aarav
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[0].id, subjectId = math1, marksObtained = 96.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[0].id, subjectId = phys1, marksObtained = 94.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[0].id, subjectId = chem1, marksObtained = 92.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[0].id, subjectId = bio1, marksObtained = 90.0),
            // Diya
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[1].id, subjectId = math1, marksObtained = 94.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[1].id, subjectId = phys1, marksObtained = 95.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[1].id, subjectId = chem1, marksObtained = 91.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[1].id, subjectId = bio1, marksObtained = 96.0),
            // Rohan
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[2].id, subjectId = math1, marksObtained = 72.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[2].id, subjectId = phys1, marksObtained = 68.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[2].id, subjectId = chem1, marksObtained = 50.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[2].id, subjectId = bio1, marksObtained = 75.0),
            // Ananya
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[3].id, subjectId = math1, marksObtained = 88.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[3].id, subjectId = phys1, marksObtained = 86.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[3].id, subjectId = chem1, marksObtained = 89.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[3].id, subjectId = bio1, marksObtained = 94.0),
            // Sneha (Declining demo step 2)
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[5].id, subjectId = math1, marksObtained = 70.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[5].id, subjectId = phys1, marksObtained = 68.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[5].id, subjectId = chem1, marksObtained = 65.0),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[5].id, subjectId = bio1, marksObtained = 69.0),
            // Pooja (Absent demo)
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[7].id, subjectId = math1, marksObtained = 0.0, isAbsent = true),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[7].id, subjectId = phys1, marksObtained = 0.0, isAbsent = true),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[7].id, subjectId = chem1, marksObtained = 0.0, isAbsent = true),
            MarkEntity(examId = exam2Id, studentId = insertedStudentsBatch1[7].id, subjectId = bio1, marksObtained = 0.0, isAbsent = true)
        )
        db.markDao().insertMarks(exam2Marks)

        // Exam 3: Midterm Evaluation – July
        val exam3Id = db.examDao().insertExam(
            ExamEntity(title = "Midterm Evaluation – July", examType = "Midterm", date = "2026-07-28", batchId = batch1Id, passingPercentage = 40.0)
        )
        val ex3SubList = listOf(
            ExamSubjectEntity(examId = exam3Id, subjectId = math1, subjectName = "Mathematics", maxMarks = 100.0),
            ExamSubjectEntity(examId = exam3Id, subjectId = phys1, subjectName = "Physics", maxMarks = 100.0),
            ExamSubjectEntity(examId = exam3Id, subjectId = chem1, subjectName = "Chemistry", maxMarks = 100.0)
        )
        db.examDao().insertExamSubjects(ex3SubList)

        val exam3Marks = listOf(
            // Aarav
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[0].id, subjectId = math1, marksObtained = 98.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[0].id, subjectId = phys1, marksObtained = 96.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[0].id, subjectId = chem1, marksObtained = 95.0),
            // Diya
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[1].id, subjectId = math1, marksObtained = 96.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[1].id, subjectId = phys1, marksObtained = 97.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[1].id, subjectId = chem1, marksObtained = 94.0),
            // Rohan (Chem failed)
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[2].id, subjectId = math1, marksObtained = 68.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[2].id, subjectId = phys1, marksObtained = 55.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[2].id, subjectId = chem1, marksObtained = 34.0, remarks = "Did not revise organic chemistry chapters"),
            // Sneha (Declining demo step 3 -> triggers attention alert!)
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[5].id, subjectId = math1, marksObtained = 58.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[5].id, subjectId = phys1, marksObtained = 54.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[5].id, subjectId = chem1, marksObtained = 52.0),
            // Pooja (Below passing 38% -> triggers attention alert!)
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[7].id, subjectId = math1, marksObtained = 42.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[7].id, subjectId = phys1, marksObtained = 32.0),
            MarkEntity(examId = exam3Id, studentId = insertedStudentsBatch1[7].id, subjectId = chem1, marksObtained = 36.0, remarks = "Parent consultation advised")
        )
        db.markDao().insertMarks(exam3Marks)

        // Upcoming Exam (Scheduled ahead)
        db.examDao().insertExam(
            ExamEntity(title = "Full Syllabus Mock Test 1", examType = "Mock Test", date = "2026-08-25", batchId = batch1Id, passingPercentage = 40.0)
        )

        // 6. Attendance records for Batch 1 (Sample past 5 sessions)
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val attendanceList = mutableListOf<AttendanceEntity>()

        for (dayOffset in 1..7) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -dayOffset)
            val dateStr = sdf.format(cal.time)

            insertedStudentsBatch1.forEachIndexed { idx, st ->
                // Make student 7 absent on some days
                val isPresent = !(idx == 7 && dayOffset % 2 == 0) && !(idx == 2 && dayOffset == 3)
                attendanceList.add(
                    AttendanceEntity(batchId = batch1Id, studentId = st.id, date = dateStr, isPresent = isPresent)
                )
            }
        }
        db.attendanceDao().insertAttendanceList(attendanceList)

        // 7. Fee Records for August & July
        val feeRecords = mutableListOf<FeeRecordEntity>()
        insertedStudentsBatch1.forEachIndexed { idx, st ->
            if (idx % 3 == 0) {
                // Paid
                feeRecords.add(FeeRecordEntity(studentId = st.id, monthPeriod = "August 2026", amountDue = 2000.0, amountPaid = 2000.0, dueDate = "2026-08-10", paymentDate = "2026-08-05", status = "PAID", paymentMethod = "UPI"))
            } else if (idx % 3 == 1) {
                // Pending
                feeRecords.add(FeeRecordEntity(studentId = st.id, monthPeriod = "August 2026", amountDue = 2000.0, amountPaid = 0.0, dueDate = "2026-08-15", status = "PENDING"))
            } else {
                // Overdue
                feeRecords.add(FeeRecordEntity(studentId = st.id, monthPeriod = "July 2026", amountDue = 2000.0, amountPaid = 500.0, dueDate = "2026-07-15", status = "OVERDUE", paymentMethod = "Cash"))
            }
        }
        db.feeDao().insertFeeRecords(feeRecords)
    }
}
