package com.example.data.model

data class GradeCutoff(
    val grade: String,
    val minPercentage: Double,
    val colorHex: String,
    val maxPercentage: Double = 100.0,
    val description: String = ""
)

data class StudentWithBatch(
    val student: StudentEntity,
    val batchName: String,
    val overallPercentage: Double = 0.0,
    val overallGrade: String = "N/A",
    val overallRank: Int = 0,
    val totalExamsTaken: Int = 0,
    val isFlagged: Boolean = false,
    val flagReason: String = "",
    val attendanceRate: Double = 100.0,
    val pendingFees: Double = 0.0
)

data class ExamWithDetails(
    val exam: ExamEntity,
    val batchName: String,
    val subjects: List<ExamSubjectEntity> = emptyList(),
    val totalMarksPossible: Double = 0.0,
    val totalStudentsEnrolled: Int = 0,
    val totalStudentsAppeared: Int = 0,
    val classAveragePercentage: Double = 0.0,
    val highestPercentage: Double = 0.0,
    val lowestPercentage: Double = 0.0,
    val passCount: Int = 0,
    val failCount: Int = 0
)

data class StudentExamResult(
    val student: StudentEntity,
    val subjectMarks: Map<Long, MarkEntity>, // subjectId -> Mark
    val totalMarksObtained: Double,
    val totalMaxMarks: Double,
    val percentage: Double,
    val grade: String,
    val isPassed: Boolean,
    val rank: Int,
    val isAllAbsent: Boolean = false,
    val remarks: String = ""
)

data class SubjectExamStats(
    val subjectId: Long,
    val subjectName: String,
    val maxMarks: Double,
    val averageMarks: Double,
    val averagePercentage: Double,
    val highestMarks: Double,
    val lowestMarks: Double,
    val passCount: Int,
    val failCount: Int
)

data class StudentProgressPoint(
    val examId: Long,
    val examTitle: String,
    val examDate: String,
    val percentage: Double,
    val rank: Int,
    val totalStudents: Int,
    val grade: String
)

data class StudentSubjectAverage(
    val subjectId: Long,
    val subjectName: String,
    val averagePercentage: Double,
    val examsCount: Int
)

data class GradeDistribution(
    val grade: String,
    val count: Int,
    val percentageOfClass: Double,
    val colorHex: String
)

data class TopPerformer(
    val student: StudentEntity,
    val batchName: String,
    val averagePercentage: Double,
    val rank: Int,
    val grade: String,
    val totalExams: Int
)

data class AttentionStudent(
    val student: StudentEntity,
    val batchName: String,
    val latestPercentage: Double,
    val reason: String, // "Failed recent exam (35%)" or "Score dropped 2 exams in a row"
    val previousScores: List<Double> = emptyList()
)

data class DashboardSummary(
    val totalActiveStudents: Int = 0,
    val totalBatches: Int = 0,
    val totalExamsConducted: Int = 0,
    val pendingFeesTotal: Double = 0.0,
    val pendingStudentsCount: Int = 0,
    val topPerformers: List<TopPerformer> = emptyList(),
    val attentionList: List<AttentionStudent> = emptyList(),
    val recentExams: List<ExamWithDetails> = emptyList(),
    val upcomingExams: List<ExamWithDetails> = emptyList()
)

data class BatchWithCounts(
    val batch: BatchEntity,
    val studentCount: Int = 0,
    val subjects: List<SubjectEntity> = emptyList(),
    val examsCount: Int = 0,
    val averagePerformance: Double = 0.0
)

data class StudentReportCard(
    val student: StudentEntity,
    val batchName: String,
    val coachingName: String,
    val teacherName: String,
    val issueDate: String,
    val examResults: List<StudentReportExamRow>,
    val cumulativePercentage: Double,
    val cumulativeGrade: String,
    val overallRank: Int,
    val totalBatchStudents: Int,
    val attendancePercentage: Double,
    val generalNotes: String
)

data class StudentReportExamRow(
    val examTitle: String,
    val examDate: String,
    val examType: String,
    val subjectScores: List<SubjectScoreDisplay>,
    val totalObtained: Double,
    val totalMax: Double,
    val percentage: Double,
    val grade: String,
    val rank: Int,
    val totalStudents: Int,
    val status: String // "PASS", "FAIL", "ABSENT"
)

data class SubjectScoreDisplay(
    val subjectName: String,
    val obtained: Double,
    val max: Double,
    val isAbsent: Boolean
)
