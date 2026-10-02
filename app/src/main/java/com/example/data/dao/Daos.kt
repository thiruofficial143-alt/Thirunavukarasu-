package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.FeeRecord
import com.example.data.model.Notice
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Long): Flow<Student?>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' OR rollNo LIKE '%' || :query || '%' OR department LIKE '%' || :query || '%'")
    fun searchStudents(query: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE department = :dept ORDER BY name ASC")
    fun getStudentsByDepartment(dept: String): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}

@Dao
interface FacultyDao {
    @Query("SELECT * FROM faculty ORDER BY name ASC")
    fun getAllFaculty(): Flow<List<Faculty>>

    @Query("SELECT COUNT(*) FROM faculty")
    fun getFacultyCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaculty(faculty: Faculty): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facultyList: List<Faculty>)

    @Update
    suspend fun updateFaculty(faculty: Faculty)

    @Delete
    suspend fun deleteFaculty(faculty: Faculty)
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY code ASC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE scheduleDay = :day ORDER BY timeSlot ASC")
    fun getCoursesByDay(day: String): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<Course>)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE courseCode = :courseCode AND date = :date")
    fun getAttendanceByCourseAndDate(courseCode: String, date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AttendanceRecord>)

    @Update
    suspend fun updateRecord(record: AttendanceRecord)
}

@Dao
interface NoticeDao {
    @Query("SELECT * FROM notices ORDER BY isUrgent DESC, id DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notices: List<Notice>)

    @Delete
    suspend fun deleteNotice(notice: Notice)
}

@Dao
interface FeeDao {
    @Query("SELECT * FROM fee_records ORDER BY id DESC")
    fun getAllFees(): Flow<List<FeeRecord>>

    @Query("SELECT * FROM fee_records WHERE status = :status ORDER BY dueDate ASC")
    fun getFeesByStatus(status: String): Flow<List<FeeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFee(fee: FeeRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fees: List<FeeRecord>)

    @Update
    suspend fun updateFee(fee: FeeRecord)

    @Delete
    suspend fun deleteFee(fee: FeeRecord)
}
