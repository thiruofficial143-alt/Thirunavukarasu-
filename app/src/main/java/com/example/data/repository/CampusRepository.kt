package com.example.data.repository

import com.example.data.database.CampusDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.FeeRecord
import com.example.data.model.Notice
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

class CampusRepository(private val database: CampusDatabase) {
    private val studentDao = database.studentDao()
    private val facultyDao = database.facultyDao()
    private val courseDao = database.courseDao()
    private val attendanceDao = database.attendanceDao()
    private val noticeDao = database.noticeDao()
    private val feeDao = database.feeDao()

    // Students
    fun getAllStudents(): Flow<List<Student>> = studentDao.getAllStudents()
    fun searchStudents(query: String): Flow<List<Student>> = studentDao.searchStudents(query)
    suspend fun insertStudent(student: Student): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: Student) = studentDao.deleteStudent(student)

    // Faculty
    fun getAllFaculty(): Flow<List<Faculty>> = facultyDao.getAllFaculty()
    suspend fun insertFaculty(faculty: Faculty): Long = facultyDao.insertFaculty(faculty)
    suspend fun updateFaculty(faculty: Faculty) = facultyDao.updateFaculty(faculty)
    suspend fun deleteFaculty(faculty: Faculty) = facultyDao.deleteFaculty(faculty)

    // Courses & Timetable
    fun getAllCourses(): Flow<List<Course>> = courseDao.getAllCourses()
    fun getCoursesByDay(day: String): Flow<List<Course>> = courseDao.getCoursesByDay(day)
    suspend fun insertCourse(course: Course): Long = courseDao.insertCourse(course)
    suspend fun updateCourse(course: Course) = courseDao.updateCourse(course)
    suspend fun deleteCourse(course: Course) = courseDao.deleteCourse(course)

    // Attendance
    fun getAttendanceByCourseAndDate(courseCode: String, date: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceByCourseAndDate(courseCode, date)
    suspend fun markAttendance(record: AttendanceRecord) = attendanceDao.insertRecord(record)
    suspend fun batchMarkAttendance(records: List<AttendanceRecord>) = attendanceDao.insertAll(records)

    // Notices
    fun getAllNotices(): Flow<List<Notice>> = noticeDao.getAllNotices()
    suspend fun insertNotice(notice: Notice): Long = noticeDao.insertNotice(notice)
    suspend fun deleteNotice(notice: Notice) = noticeDao.deleteNotice(notice)

    // Fees
    fun getAllFees(): Flow<List<FeeRecord>> = feeDao.getAllFees()
    fun getFeesByStatus(status: String): Flow<List<FeeRecord>> = feeDao.getFeesByStatus(status)
    suspend fun insertFee(fee: FeeRecord): Long = feeDao.insertFee(fee)
    suspend fun updateFee(fee: FeeRecord) = feeDao.updateFee(fee)
    suspend fun deleteFee(fee: FeeRecord) = feeDao.deleteFee(fee)
}
