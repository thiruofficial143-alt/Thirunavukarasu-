package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rollNo: String,
    val name: String,
    val department: String,
    val semester: Int,
    val email: String,
    val phone: String,
    val cgpa: Double,
    val feeStatus: String = "Paid", // Paid, Pending, Overdue
    val attendanceRate: Int = 85, // percentage e.g. 85
    val guardianName: String = "",
    val guardianPhone: String = "",
    val address: String = ""
)

@Entity(tableName = "faculty")
data class Faculty(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val empId: String,
    val name: String,
    val department: String,
    val designation: String, // e.g., "Professor & HOD", "Associate Professor"
    val email: String,
    val phone: String,
    val officeRoom: String,
    val subjects: String // comma separated list
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String, // e.g. "CS301"
    val title: String, // e.g. "Data Structures & Algorithms"
    val department: String,
    val credits: Int,
    val facultyName: String,
    val scheduleDay: String, // "Monday", "Tuesday", etc.
    val timeSlot: String, // "09:00 AM - 10:30 AM"
    val room: String // "Lecture Hall 101"
)

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val rollNo: String,
    val courseCode: String,
    val date: String, // "YYYY-MM-DD"
    val status: String // "Present", "Absent", "Late"
)

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // "Academic", "Exams", "Events", "Placement", "General"
    val date: String,
    val isUrgent: Boolean = false,
    val author: String = "Administration"
)

@Entity(tableName = "fee_records")
data class FeeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val rollNo: String,
    val feeType: String, // "Tuition Fee", "Lab & Library", "Hostel Fee", "Exam Fee"
    val amount: Double,
    val dueDate: String,
    val status: String = "Pending", // "Paid", "Pending", "Overdue"
    val receiptNo: String? = null,
    val paidDate: String? = null
)
