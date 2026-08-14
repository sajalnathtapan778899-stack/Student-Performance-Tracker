package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val academicYear: String = "2026-2027",
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["batchId"])]
)
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String = "",
    val batchId: Long
)

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["batchId"]), Index(value = ["rollNumber"], unique = true)]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rollNumber: String,
    val fullName: String,
    val photoUri: String? = null,
    val dob: String = "",
    val gender: String = "Male",
    val phone: String = "",
    val parentName: String = "",
    val parentPhone: String = "",
    val address: String = "",
    val batchId: Long,
    val admissionDate: String = "",
    val isActive: Boolean = true,
    val notes: String = ""
)

@Entity(
    tableName = "exams",
    foreignKeys = [
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["batchId"])]
)
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val examType: String = "Monthly Test",
    val date: String,
    val batchId: Long,
    val passingPercentage: Double = 40.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "exam_subjects",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["examId"]), Index(value = ["subjectId"])]
)
data class ExamSubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val subjectId: Long,
    val subjectName: String,
    val maxMarks: Double = 100.0
)

@Entity(
    tableName = "marks",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["examId", "studentId", "subjectId"], unique = true),
        Index(value = ["studentId"]),
        Index(value = ["examId"]),
        Index(value = ["subjectId"])
    ]
)
data class MarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val studentId: Long,
    val subjectId: Long,
    val marksObtained: Double = 0.0,
    val isAbsent: Boolean = false,
    val remarks: String = ""
)

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["batchId", "studentId", "date"], unique = true),
        Index(value = ["studentId"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val batchId: Long,
    val studentId: Long,
    val date: String,
    val isPresent: Boolean = true
)

@Entity(
    tableName = "fee_records",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"])]
)
data class FeeRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val monthPeriod: String = "",
    val amountDue: Double = 1500.0,
    val amountPaid: Double = 0.0,
    val dueDate: String = "",
    val paymentDate: String? = null,
    val status: String = "PENDING", // "PAID", "PENDING", "OVERDUE", "PARTIAL"
    val paymentMethod: String = "Cash",
    val remarks: String = ""
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val teacherName: String = "Prof. R. Sharma",
    val teacherEmail: String = "teacher@apexcoaching.com",
    val coachingName: String = "Apex Coaching & Tuition Centre",
    val defaultPassingPercentage: Double = 40.0,
    val gradingScaleJson: String = """
        [
            {"grade": "A+", "minPercentage": 90.0, "colorHex": "#10B981"},
            {"grade": "A", "minPercentage": 80.0, "colorHex": "#059669"},
            {"grade": "B", "minPercentage": 70.0, "colorHex": "#2563EB"},
            {"grade": "C", "minPercentage": 60.0, "colorHex": "#D97706"},
            {"grade": "D", "minPercentage": 40.0, "colorHex": "#F59E0B"},
            {"grade": "F", "minPercentage": 0.0, "colorHex": "#DC2626"}
        ]
    """.trimIndent()
)
