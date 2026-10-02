package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Faculty
import com.example.data.model.Student
import com.example.ui.screens.*
import com.example.ui.theme.CampusCoreTheme
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.viewmodel.CampusNavDestination
import com.example.ui.viewmodel.CampusViewModel
import com.example.ui.viewmodel.UserRole

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusCoreTheme {
                CampusApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusApp(viewModel: CampusViewModel = viewModel()) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    val students by viewModel.filteredStudents.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val facultyList by viewModel.allFaculty.collectAsStateWithLifecycle()
    val allCourses by viewModel.allCourses.collectAsStateWithLifecycle()
    val dayCourses by viewModel.dayCourses.collectAsStateWithLifecycle()
    val notices by viewModel.allNotices.collectAsStateWithLifecycle()
    val fees by viewModel.allFees.collectAsStateWithLifecycle()

    val searchQuery by viewModel.studentSearchQuery.collectAsStateWithLifecycle()
    val selectedDept by viewModel.selectedDepartment.collectAsStateWithLifecycle()
    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()

    val selectedAttendanceCourse by viewModel.selectedAttendanceCourse.collectAsStateWithLifecycle()
    val attendanceMap by viewModel.attendanceMap.collectAsStateWithLifecycle()
    val selectedStudent by viewModel.selectedStudent.collectAsStateWithLifecycle()

    // Dialog and Modal state
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var showAddFacultyDialog by remember { mutableStateOf(false) }
    var facultyToEdit by remember { mutableStateOf<Faculty?>(null) }
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showAddNoticeDialog by remember { mutableStateOf(false) }
    var showAddFeeDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }

    // BackHandler: return to dashboard if on sub-screens
    if (currentDestination != CampusNavDestination.DASHBOARD) {
        BackHandler {
            viewModel.selectDestination(CampusNavDestination.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_campus_icon),
                                    contentDescription = "Campus Crest",
                                    modifier = Modifier.size(28.dp).clip(CircleShape)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "CampusCore",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentDestination.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (currentDestination != CampusNavDestination.DASHBOARD) {
                        IconButton(
                            onClick = { viewModel.selectDestination(CampusNavDestination.DASHBOARD) },
                            modifier = Modifier.testTag("back_to_dashboard_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Overview")
                        }
                    }
                },
                actions = {
                    // Role Switcher Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { showRoleDialog = true }
                            .padding(end = 12.dp)
                            .testTag("role_switcher_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                when (currentRole) {
                                    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    UserRole.FACULTY -> Icons.Default.School
                                    UserRole.STUDENT -> Icons.Default.Person
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentRole.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("bottom_nav_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // 1. Overview
                NavigationBarItem(
                    selected = currentDestination == CampusNavDestination.DASHBOARD,
                    onClick = { viewModel.selectDestination(CampusNavDestination.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                    label = { Text("Overview") },
                    modifier = Modifier.testTag("nav_overview")
                )

                // 2. Students
                NavigationBarItem(
                    selected = currentDestination == CampusNavDestination.STUDENTS,
                    onClick = { viewModel.selectDestination(CampusNavDestination.STUDENTS) },
                    icon = { Icon(Icons.Default.School, contentDescription = "Students") },
                    label = { Text("Students") },
                    modifier = Modifier.testTag("nav_students")
                )

                // 3. Timetable
                NavigationBarItem(
                    selected = currentDestination == CampusNavDestination.COURSES,
                    onClick = { viewModel.selectDestination(CampusNavDestination.COURSES) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Timetable") },
                    label = { Text("Timetable") },
                    modifier = Modifier.testTag("nav_timetable")
                )

                // 4. Attendance
                NavigationBarItem(
                    selected = currentDestination == CampusNavDestination.ATTENDANCE,
                    onClick = { viewModel.selectDestination(CampusNavDestination.ATTENDANCE) },
                    icon = { Icon(Icons.Default.FactCheck, contentDescription = "Attendance") },
                    label = { Text("Attendance") },
                    modifier = Modifier.testTag("nav_attendance")
                )

                // 5. Campus Hub
                NavigationBarItem(
                    selected = currentDestination == CampusNavDestination.FINANCE_NOTICES || currentDestination == CampusNavDestination.FACULTY,
                    onClick = { viewModel.selectDestination(CampusNavDestination.FINANCE_NOTICES) },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Campus Hub") },
                    label = { Text("Campus Hub") },
                    modifier = Modifier.testTag("nav_campus_hub")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                CampusNavDestination.DASHBOARD -> {
                    DashboardScreen(
                        students = allStudents,
                        facultyList = facultyList,
                        courses = allCourses,
                        notices = notices,
                        fees = fees,
                        currentRole = currentRole,
                        onNavigate = { dest -> viewModel.selectDestination(dest) },
                        onOpenAddStudent = { showAddStudentDialog = true },
                        onOpenAddNotice = { showAddNoticeDialog = true },
                        onOpenAddFee = { showAddFeeDialog = true },
                        onSelectStudent = { s -> viewModel.selectStudent(s) }
                    )
                }

                CampusNavDestination.STUDENTS -> {
                    StudentsScreen(
                        students = students,
                        searchQuery = searchQuery,
                        selectedDepartment = selectedDept,
                        onSearchChange = { viewModel.setStudentSearchQuery(it) },
                        onDepartmentSelect = { viewModel.setSelectedDepartment(it) },
                        onStudentClick = { s -> viewModel.selectStudent(s) },
                        onAddStudentClick = { showAddStudentDialog = true }
                    )
                }

                CampusNavDestination.FACULTY -> {
                    FacultyScreen(
                        facultyList = facultyList,
                        onAddFacultyClick = { showAddFacultyDialog = true },
                        onEditFaculty = { f -> facultyToEdit = f },
                        onDeleteFaculty = { f -> viewModel.deleteFaculty(f) }
                    )
                }

                CampusNavDestination.COURSES -> {
                    TimetableScreen(
                        coursesForDay = dayCourses,
                        allCourses = allCourses,
                        selectedDay = selectedDay,
                        onSelectDay = { viewModel.setSelectedDay(it) },
                        onAddCourseClick = { showAddCourseDialog = true },
                        onDeleteCourse = { c -> viewModel.deleteCourse(c) }
                    )
                }

                CampusNavDestination.ATTENDANCE -> {
                    AttendanceScreen(
                        students = allStudents,
                        courses = allCourses,
                        selectedCourseCode = selectedAttendanceCourse,
                        attendanceMap = attendanceMap,
                        dateStr = viewModel.currentDateStr,
                        onSelectCourse = { viewModel.setAttendanceCourse(it) },
                        onSetStatus = { studentId, status -> viewModel.setStudentAttendanceStatus(studentId, status) },
                        onMarkAllPresent = { list -> viewModel.markAllPresent(list) },
                        onSaveSession = { list -> viewModel.saveAttendanceSession(list) }
                    )
                }

                CampusNavDestination.FINANCE_NOTICES -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Quick switch to Faculty directory
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectDestination(CampusNavDestination.FACULTY) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Faculty & Staff Directory (${facultyList.size} professors)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }

                        FinanceNoticesScreen(
                            fees = fees,
                            notices = notices,
                            students = allStudents,
                            onAddFeeClick = { showAddFeeDialog = true },
                            onAddNoticeClick = { showAddNoticeDialog = true },
                            onMarkFeePaid = { fee -> viewModel.markFeePaid(fee) },
                            onDeleteFee = { fee -> viewModel.deleteFeeRecord(fee) },
                            onDeleteNotice = { notice -> viewModel.deleteNotice(notice) }
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet for inspecting Student
    selectedStudent?.let { student ->
        StudentDetailModal(
            student = student,
            onDismiss = { viewModel.selectStudent(null) },
            onEdit = { s ->
                studentToEdit = s
                viewModel.selectStudent(null)
            },
            onDelete = { s ->
                viewModel.deleteStudent(s)
            }
        )
    }

    // Add Student Dialog
    if (showAddStudentDialog) {
        AddEditStudentDialog(
            initialStudent = null,
            onDismiss = { showAddStudentDialog = false },
            onConfirm = { newStudent ->
                viewModel.addStudent(newStudent)
                showAddStudentDialog = false
            }
        )
    }

    // Edit Student Dialog
    studentToEdit?.let { s ->
        AddEditStudentDialog(
            initialStudent = s,
            onDismiss = { studentToEdit = null },
            onConfirm = { updated ->
                viewModel.updateStudent(updated)
                studentToEdit = null
            }
        )
    }

    // Add Faculty Dialog
    if (showAddFacultyDialog) {
        AddEditFacultyDialog(
            initialFaculty = null,
            onDismiss = { showAddFacultyDialog = false },
            onConfirm = { faculty ->
                viewModel.addFaculty(faculty)
                showAddFacultyDialog = false
            }
        )
    }

    // Edit Faculty Dialog
    facultyToEdit?.let { f ->
        AddEditFacultyDialog(
            initialFaculty = f,
            onDismiss = { facultyToEdit = null },
            onConfirm = { updated ->
                viewModel.updateFaculty(updated)
                facultyToEdit = null
            }
        )
    }

    // Add Course Dialog
    if (showAddCourseDialog) {
        AddCourseDialog(
            onDismiss = { showAddCourseDialog = false },
            onConfirm = { course ->
                viewModel.addCourse(course)
                showAddCourseDialog = false
            }
        )
    }

    // Add Notice Dialog
    if (showAddNoticeDialog) {
        AddNoticeDialog(
            onDismiss = { showAddNoticeDialog = false },
            onConfirm = { notice ->
                viewModel.addNotice(notice)
                showAddNoticeDialog = false
            }
        )
    }

    // Add Fee Dialog
    if (showAddFeeDialog) {
        AddFeeDialog(
            students = allStudents,
            onDismiss = { showAddFeeDialog = false },
            onConfirm = { fee ->
                viewModel.addFeeRecord(fee)
                showAddFeeDialog = false
            }
        )
    }

    // Role Selection Dialog
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Select User Perspective", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    UserRole.values().forEach { role ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectRole(role)
                                    showRoleDialog = false
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentRole == role) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    when (role) {
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                        UserRole.FACULTY -> Icons.Default.School
                                        UserRole.STUDENT -> Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    tint = if (currentRole == role) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Column {
                                    Text(
                                        role.label,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        when (role) {
                                            UserRole.ADMIN -> "Full access to campus governance, finance, and records"
                                            UserRole.FACULTY -> "Manage classes, student attendance, and circulars"
                                            UserRole.STUDENT -> "Personal academic timetable, dues, and announcements"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRoleDialog = false }) { Text("Close") }
            }
        )
    }
}
