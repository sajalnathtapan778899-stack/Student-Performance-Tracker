package com.example.data.calculation

import com.example.data.model.AttentionStudent
import com.example.data.model.ExamEntity
import com.example.data.model.ExamSubjectEntity
import com.example.data.model.GradeCutoff
import com.example.data.model.GradeDistribution
import com.example.data.model.MarkEntity
import com.example.data.model.StudentEntity
import com.example.data.model.StudentExamResult
import com.example.data.model.StudentProgressPoint
import com.example.data.model.StudentSubjectAverage
import com.example.data.model.SubjectExamStats
import com.example.data.model.TopPerformer
import org.json.JSONArray
import java.util.Locale

object PerformanceCalculator {

    val DEFAULT_GRADE_CUTOFFS = listOf(
        GradeCutoff("A+", 90.0, "#10B981", 100.0, "Outstanding Performance"),
        GradeCutoff("A", 80.0, "#059669", 89.9, "Excellent Performance"),
        GradeCutoff("B", 70.0, "#2563EB", 79.9, "Very Good Performance"),
        GradeCutoff("C", 60.0, "#D97706", 69.9, "Good Performance"),
        GradeCutoff("D", 40.0, "#F59E0B", 59.9, "Satisfactory / Pass"),
        GradeCutoff("F", 0.0, "#DC2626", 39.9, "Needs Improvement / Fail")
    )

    fun parseGradeCutoffs(jsonString: String): List<GradeCutoff> {
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<GradeCutoff>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    GradeCutoff(
                        grade = obj.optString("grade", "N/A"),
                        minPercentage = obj.optDouble("minPercentage", 0.0),
                        colorHex = obj.optString("colorHex", "#3B82F6"),
                        maxPercentage = obj.optDouble("maxPercentage", 100.0),
                        description = obj.optString("description", "")
                    )
                )
            }
            if (list.isNotEmpty()) list.sortedByDescending { it.minPercentage } else DEFAULT_GRADE_CUTOFFS
        } catch (e: Exception) {
            DEFAULT_GRADE_CUTOFFS
        }
    }

    fun determineGrade(percentage: Double, cutoffs: List<GradeCutoff> = DEFAULT_GRADE_CUTOFFS): String {
        val sorted = cutoffs.sortedByDescending { it.minPercentage }
        for (cutoff in sorted) {
            if (percentage >= cutoff.minPercentage) {
                return cutoff.grade
            }
        }
        return cutoffs.lastOrNull()?.grade ?: "F"
    }

    fun calculateExamResults(
        exam: ExamEntity,
        subjects: List<ExamSubjectEntity>,
        students: List<StudentEntity>,
        marks: List<MarkEntity>,
        cutoffs: List<GradeCutoff> = DEFAULT_GRADE_CUTOFFS
    ): List<StudentExamResult> {
        val totalMax = subjects.sumOf { it.maxMarks }
        val marksByStudent = marks.groupBy { it.studentId }

        val rawResults = students.map { student ->
            val studentMarks = marksByStudent[student.id] ?: emptyList()
            val marksMap = studentMarks.associateBy { it.subjectId }
            
            var totalObtained = 0.0
            var allAbsent = subjects.isNotEmpty()

            for (subject in subjects) {
                val mark = marksMap[subject.subjectId]
                if (mark != null && !mark.isAbsent) {
                    totalObtained += mark.marksObtained.coerceIn(0.0, subject.maxMarks)
                    allAbsent = false
                }
            }

            val percentage = if (totalMax > 0 && !allAbsent) {
                (totalObtained / totalMax) * 100.0
            } else 0.0

            val grade = if (allAbsent) "ABS" else determineGrade(percentage, cutoffs)
            val isPassed = !allAbsent && percentage >= exam.passingPercentage

            val remarkText = studentMarks.firstOrNull { it.remarks.isNotBlank() }?.remarks ?: ""

            StudentExamResult(
                student = student,
                subjectMarks = marksMap,
                totalMarksObtained = totalObtained,
                totalMaxMarks = totalMax,
                percentage = percentage,
                grade = grade,
                isPassed = isPassed,
                rank = 0, // Assigned below
                isAllAbsent = allAbsent,
                remarks = remarkText
            )
        }

        // Calculate standard competition ranking with tie-handling
        // Non-absentees first, sorted by percentage descending
        val presentStudents = rawResults.filter { !it.isAllAbsent }.sortedByDescending { it.percentage }
        val absentStudents = rawResults.filter { it.isAllAbsent }

        val rankedResults = mutableListOf<StudentExamResult>()
        var currentRank = 1
        var previousPercentage: Double? = null
        var tiedCount = 0

        for ((index, item) in presentStudents.withIndex()) {
            if (previousPercentage != null && item.percentage == previousPercentage) {
                tiedCount++
            } else {
                currentRank = index + 1
                tiedCount = 0
            }
            previousPercentage = item.percentage
            rankedResults.add(item.copy(rank = currentRank))
        }

        // Absent students assigned 0 rank
        absentStudents.forEach {
            rankedResults.add(it.copy(rank = 0))
        }

        return rankedResults
    }

    fun calculateSubjectStats(
        subjects: List<ExamSubjectEntity>,
        marks: List<MarkEntity>,
        passingPercentage: Double = 40.0
    ): List<SubjectExamStats> {
        return subjects.map { examSubject ->
            val subjectMarks = marks.filter { it.subjectId == examSubject.subjectId && !it.isAbsent }
            val scores = subjectMarks.map { it.marksObtained.coerceIn(0.0, examSubject.maxMarks) }
            
            val avg = if (scores.isNotEmpty()) scores.average() else 0.0
            val max = if (scores.isNotEmpty()) scores.maxOrNull() ?: 0.0 else 0.0
            val min = if (scores.isNotEmpty()) scores.minOrNull() ?: 0.0 else 0.0
            val avgPct = if (examSubject.maxMarks > 0) (avg / examSubject.maxMarks) * 100.0 else 0.0

            val passMarks = (passingPercentage / 100.0) * examSubject.maxMarks
            val passCount = scores.count { it >= passMarks }
            val failCount = scores.count { it < passMarks }

            SubjectExamStats(
                subjectId = examSubject.subjectId,
                subjectName = examSubject.subjectName,
                maxMarks = examSubject.maxMarks,
                averageMarks = avg,
                averagePercentage = avgPct,
                highestMarks = max,
                lowestMarks = min,
                passCount = passCount,
                failCount = failCount
            )
        }
    }

    fun calculateGradeDistribution(
        results: List<StudentExamResult>,
        cutoffs: List<GradeCutoff> = DEFAULT_GRADE_CUTOFFS
    ): List<GradeDistribution> {
        val presentResults = results.filter { !it.isAllAbsent }
        val total = presentResults.size.coerceAtLeast(1)

        return cutoffs.map { cutoff ->
            val count = presentResults.count { it.grade == cutoff.grade }
            GradeDistribution(
                grade = cutoff.grade,
                count = count,
                percentageOfClass = (count.toDouble() / total) * 100.0,
                colorHex = cutoff.colorHex
            )
        }
    }

    fun calculateStudentProgress(
        studentId: Long,
        allExams: List<ExamEntity>,
        allExamSubjects: List<ExamSubjectEntity>,
        allStudents: List<StudentEntity>,
        allMarks: List<MarkEntity>,
        cutoffs: List<GradeCutoff> = DEFAULT_GRADE_CUTOFFS
    ): List<StudentProgressPoint> {
        val sortedExams = allExams.sortedBy { it.date }
        val progressPoints = mutableListOf<StudentProgressPoint>()

        for (exam in sortedExams) {
            val examSubjects = allExamSubjects.filter { it.examId == exam.id }
            val batchStudents = allStudents.filter { it.batchId == exam.batchId }
            val examMarks = allMarks.filter { it.examId == exam.id }

            val results = calculateExamResults(exam, examSubjects, batchStudents, examMarks, cutoffs)
            val studentResult = results.find { it.student.id == studentId }

            if (studentResult != null && !studentResult.isAllAbsent) {
                progressPoints.add(
                    StudentProgressPoint(
                        examId = exam.id,
                        examTitle = exam.title,
                        examDate = exam.date,
                        percentage = studentResult.percentage,
                        rank = studentResult.rank,
                        totalStudents = results.count { !it.isAllAbsent },
                        grade = studentResult.grade
                    )
                )
            }
        }
        return progressPoints
    }

    fun calculateStudentSubjectAverages(
        studentId: Long,
        allExamSubjects: List<ExamSubjectEntity>,
        allMarks: List<MarkEntity>
    ): List<StudentSubjectAverage> {
        val studentMarks = allMarks.filter { it.studentId == studentId && !it.isAbsent }
        val marksBySubject = studentMarks.groupBy { it.subjectId }

        return marksBySubject.mapNotNull { (subjectId, marks) ->
            val examSubjectMap = allExamSubjects.associateBy { it.examId to it.subjectId }
            val percentages = marks.mapNotNull { mark ->
                val examSub = examSubjectMap[mark.examId to mark.subjectId]
                if (examSub != null && examSub.maxMarks > 0) {
                    (mark.marksObtained / examSub.maxMarks) * 100.0
                } else null
            }

            if (percentages.isNotEmpty()) {
                val subjectName = allExamSubjects.firstOrNull { it.subjectId == subjectId }?.subjectName ?: "Subject $subjectId"
                StudentSubjectAverage(
                    subjectId = subjectId,
                    subjectName = subjectName,
                    averagePercentage = percentages.average(),
                    examsCount = percentages.size
                )
            } else null
        }.sortedByDescending { it.averagePercentage }
    }

    fun detectAttentionStudents(
        students: List<StudentEntity>,
        batchesMap: Map<Long, String>,
        allExams: List<ExamEntity>,
        allExamSubjects: List<ExamSubjectEntity>,
        allMarks: List<MarkEntity>,
        cutoffs: List<GradeCutoff> = DEFAULT_GRADE_CUTOFFS
    ): List<AttentionStudent> {
        val attentionList = mutableListOf<AttentionStudent>()
        val sortedExams = allExams.sortedByDescending { it.date }

        for (student in students.filter { it.isActive }) {
            val studentExams = sortedExams.filter { it.batchId == student.batchId }
            if (studentExams.isEmpty()) continue

            val progressList = mutableListOf<Double>()
            var latestFailed = false
            var latestPercentage = 0.0

            for ((idx, exam) in studentExams.take(4).withIndex()) {
                val examSubjects = allExamSubjects.filter { it.examId == exam.id }
                val examMarks = allMarks.filter { it.examId == exam.id }
                val results = calculateExamResults(exam, examSubjects, listOf(student), examMarks, cutoffs)
                val res = results.firstOrNull()

                if (res != null && !res.isAllAbsent) {
                    if (idx == 0) {
                        latestPercentage = res.percentage
                        if (!res.isPassed) latestFailed = true
                    }
                    progressList.add(res.percentage)
                }
            }

            if (latestFailed) {
                attentionList.add(
                    AttentionStudent(
                        student = student,
                        batchName = batchesMap[student.batchId] ?: "Batch",
                        latestPercentage = latestPercentage,
                        reason = String.format(Locale.getDefault(), "Failed latest exam (%.1f%%)", latestPercentage),
                        previousScores = progressList
                    )
                )
            } else if (progressList.size >= 3) {
                // Check if dropping for 2+ consecutive exams (progressList is newest first)
                // e.g. p[0] < p[1] and p[1] < p[2]
                if (progressList[0] < progressList[1] && progressList[1] < progressList[2]) {
                    val dropAmount = progressList[2] - progressList[0]
                    attentionList.add(
                        AttentionStudent(
                            student = student,
                            batchName = batchesMap[student.batchId] ?: "Batch",
                            latestPercentage = progressList[0],
                            reason = String.format(Locale.getDefault(), "Declining trend (-%.1f%% over 3 tests)", dropAmount),
                            previousScores = progressList
                        )
                    )
                }
            }
        }
        return attentionList
    }

    fun calculateTopPerformers(
        students: List<StudentEntity>,
        batchesMap: Map<Long, String>,
        allExams: List<ExamEntity>,
        allExamSubjects: List<ExamSubjectEntity>,
        allMarks: List<MarkEntity>,
        cutoffs: List<GradeCutoff> = DEFAULT_GRADE_CUTOFFS,
        limit: Int = 5
    ): List<TopPerformer> {
        val studentAverages = students.filter { it.isActive }.mapNotNull { student ->
            val studentExams = allExams.filter { it.batchId == student.batchId }
            if (studentExams.isEmpty()) return@mapNotNull null

            var totalPct = 0.0
            var count = 0

            for (exam in studentExams) {
                val examSubjects = allExamSubjects.filter { it.examId == exam.id }
                val examMarks = allMarks.filter { it.examId == exam.id }
                val res = calculateExamResults(exam, examSubjects, listOf(student), examMarks, cutoffs).firstOrNull()
                if (res != null && !res.isAllAbsent) {
                    totalPct += res.percentage
                    count++
                }
            }

            if (count > 0) {
                val avg = totalPct / count
                TopPerformer(
                    student = student,
                    batchName = batchesMap[student.batchId] ?: "Batch",
                    averagePercentage = avg,
                    rank = 0,
                    grade = determineGrade(avg, cutoffs),
                    totalExams = count
                )
            } else null
        }.sortedByDescending { it.averagePercentage }

        return studentAverages.mapIndexed { index, performer ->
            performer.copy(rank = index + 1)
        }.take(limit)
    }
}
