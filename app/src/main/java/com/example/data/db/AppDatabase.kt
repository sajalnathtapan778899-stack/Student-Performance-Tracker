package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.BatchEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ExamSubjectEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.MarkEntity
import com.example.data.model.StudentEntity
import com.example.data.model.SubjectEntity

@Database(
    entities = [
        BatchEntity::class,
        SubjectEntity::class,
        StudentEntity::class,
        ExamEntity::class,
        ExamSubjectEntity::class,
        MarkEntity::class,
        AttendanceEntity::class,
        FeeRecordEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun batchDao(): BatchDao
    abstract fun subjectDao(): SubjectDao
    abstract fun studentDao(): StudentDao
    abstract fun examDao(): ExamDao
    abstract fun markDao(): MarkDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun feeDao(): FeeDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_tracker.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
