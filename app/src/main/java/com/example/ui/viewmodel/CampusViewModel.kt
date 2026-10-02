package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.CampusDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.FeeRecord
import com.example.data.model.Notice
import com.example.data.model.Student
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CampusNavDestination(val label: String) {
    DASHBOARD("Overview"),
    STUDENTS("Students"),
    FACULTY("Faculty"),
    COURSES("Timetable"),
    ATTENDANCE("Attendance"),
    FINANCE_NOTICES("Campus Hub")
}

enum class UserRole(val label: String) {
    ADMIN("Administrator"),
    FACULTY("Faculty Member"),
    STUDENT("Student Portal")
}

class CampusViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CampusRepository

    init {
        val db = CampusDatabase.getDatabase(application, viewModelScope)
        repository = CampusRepository(db)
    }

    // Navigation and Role
    private val _currentDestination = MutableStateFlow(CampusNavDestination.DASHBOARD)
    val currentDestination: StateFlow<CampusNavDestination> = _currentDestination.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    fun selectDestination(dest: CampusNavDestination) {
        _currentDestination.value = dest
    }

    fun selectRole(role: UserRole) {
        _currentRole.value = role
    }

    // Search and Filters
    private val _studentSearchQuery = MutableStateFlow("")
    val studentSearchQuery: StateFlow<String> = _studentSearchQuery.asStateFlow()

    private val _selectedDepartment = MutableStateFlow("All")
    val selectedDepartment: StateFlow<String> = _selectedDepartment.asStateFlow()

    fun setStudentSearchQuery(query: String) {
        _studentSearchQuery.value = query
    }

    fun setSelectedDepartment(dept: String) {
        _selectedDepartment.value = dept
    }

    // Raw Data Streams
    val allStudents: StateFlow<List<Student>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFaculty: StateFlow<List<Faculty>> = repository.getAllFaculty()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCourses: StateFlow<List<Course>> = repository.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotices: StateFlow<List<Notice>> = repository.getAllNotices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFees: StateFlow<List<FeeRecord>> = repository.getAllFees()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Students
    val filteredStudents: StateFlow<List<Student>> = combine(
        allStudents,
        _studentSearchQuery,
        _selectedDepartment
    ) { students, query, dept ->
        students.filter { student ->
            val matchesQuery = query.isBlank() ||
                    student.name.contains(query, ignoreCase = true) ||
                    student.rollNo.contains(query, ignoreCase = true) ||
                    student.department.contains(query, ignoreCase = true)
            val matchesDept = dept == "All" || student.department == dept
            matchesQuery && matchesDept
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Timetable state
    private val _selectedDay = MutableStateFlow("Monday")
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
    }

    val dayCourses: StateFlow<List<Course>> = combine(allCourses, _selectedDay) { courses, day ->
        courses.filter { it.scheduleDay.equals(day, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Attendance State
    private val _selectedAttendanceCourse = MutableStateFlow("CS-301")
    val selectedAttendanceCourse: StateFlow<String> = _selectedAttendanceCourse.asStateFlow()

    val currentDateStr: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _attendanceMap = MutableStateFlow<Map<Long, String>>(emptyMap())
    val attendanceMap: StateFlow<Map<Long, String>> = _attendanceMap.asStateFlow()

    fun setAttendanceCourse(code: String) {
        _selectedAttendanceCourse.value = code
    }

    fun setStudentAttendanceStatus(studentId: Long, status: String) {
        val current = _attendanceMap.value.toMutableMap()
        current[studentId] = status
        _attendanceMap.value = current
    }

    fun markAllPresent(students: List<Student>) {
        val map = students.associate { it.id to "Present" }
        _attendanceMap.value = map
    }

    fun saveAttendanceSession(students: List<Student>) {
        viewModelScope.launch {
            val records = students.map { student ->
                val status = _attendanceMap.value[student.id] ?: "Present"
                AttendanceRecord(
                    studentId = student.id,
                    studentName = student.name,
                    rollNo = student.rollNo,
                    courseCode = _selectedAttendanceCourse.value,
                    date = currentDateStr,
                    status = status
                )
            }
            repository.batchMarkAttendance(records)
        }
    }

    // Detail & Dialog States
    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> = _selectedStudent.asStateFlow()

    fun selectStudent(student: Student?) {
        _selectedStudent.value = student
    }

    // Student CRUD
    fun addStudent(student: Student) {
        viewModelScope.launch {
            repository.insertStudent(student)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
            if (_selectedStudent.value?.id == student.id) {
                _selectedStudent.value = student
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            if (_selectedStudent.value?.id == student.id) {
                _selectedStudent.value = null
            }
        }
    }

    // Faculty CRUD
    fun addFaculty(faculty: Faculty) {
        viewModelScope.launch {
            repository.insertFaculty(faculty)
        }
    }

    fun updateFaculty(faculty: Faculty) {
        viewModelScope.launch {
            repository.updateFaculty(faculty)
        }
    }

    fun deleteFaculty(faculty: Faculty) {
        viewModelScope.launch {
            repository.deleteFaculty(faculty)
        }
    }

    // Course CRUD
    fun addCourse(course: Course) {
        viewModelScope.launch {
            repository.insertCourse(course)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
        }
    }

    // Fee CRUD
    fun addFeeRecord(fee: FeeRecord) {
        viewModelScope.launch {
            repository.insertFee(fee)
        }
    }

    fun markFeePaid(fee: FeeRecord) {
        viewModelScope.launch {
            val updated = fee.copy(
                status = "Paid",
                receiptNo = "REC-${(10000..99999).random()}",
                paidDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            )
            repository.updateFee(updated)
        }
    }

    fun deleteFeeRecord(fee: FeeRecord) {
        viewModelScope.launch {
            repository.deleteFee(fee)
        }
    }

    // Notice CRUD
    fun addNotice(notice: Notice) {
        viewModelScope.launch {
            repository.insertNotice(notice)
        }
    }

    fun deleteNotice(notice: Notice) {
        viewModelScope.launch {
            repository.deleteNotice(notice)
        }
    }
}
