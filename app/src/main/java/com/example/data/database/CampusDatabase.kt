package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AttendanceDao
import com.example.data.dao.CourseDao
import com.example.data.dao.FacultyDao
import com.example.data.dao.FeeDao
import com.example.data.dao.NoticeDao
import com.example.data.dao.StudentDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.FeeRecord
import com.example.data.model.Notice
import com.example.data.model.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Student::class,
        Faculty::class,
        Course::class,
        AttendanceRecord::class,
        Notice::class,
        FeeRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun facultyDao(): FacultyDao
    abstract fun courseDao(): CourseDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun noticeDao(): NoticeDao
    abstract fun feeDao(): FeeDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "campus_core_database"
                )
                    .addCallback(CampusDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class CampusDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: CampusDatabase) {
            val studentDao = database.studentDao()
            val facultyDao = database.facultyDao()
            val courseDao = database.courseDao()
            val noticeDao = database.noticeDao()
            val feeDao = database.feeDao()
            val attendanceDao = database.attendanceDao()

            val students = listOf(
                Student(
                    rollNo = "CS-2024-001",
                    name = "Aarav Sharma",
                    department = "Computer Science",
                    semester = 6,
                    email = "aarav.sharma@campus.edu",
                    phone = "+1 (555) 234-5678",
                    cgpa = 3.92,
                    feeStatus = "Paid",
                    attendanceRate = 94,
                    guardianName = "Vikram Sharma",
                    guardianPhone = "+1 (555) 987-1122",
                    address = "Oak Ridge Hall, Room 304"
                ),
                Student(
                    rollNo = "CS-2024-014",
                    name = "Elena Rostova",
                    department = "Computer Science",
                    semester = 6,
                    email = "elena.r@campus.edu",
                    phone = "+1 (555) 345-6789",
                    cgpa = 3.85,
                    feeStatus = "Paid",
                    attendanceRate = 91,
                    guardianName = "Natalia Rostova",
                    guardianPhone = "+1 (555) 876-2233",
                    address = "Elm Court, Apt 12B"
                ),
                Student(
                    rollNo = "EC-2024-008",
                    name = "Marcus Vance",
                    department = "Electronics & Comm",
                    semester = 4,
                    email = "m.vance@campus.edu",
                    phone = "+1 (555) 456-7890",
                    cgpa = 3.42,
                    feeStatus = "Pending",
                    attendanceRate = 78,
                    guardianName = "David Vance",
                    guardianPhone = "+1 (555) 765-3344",
                    address = "Pine Hall, Room 112"
                ),
                Student(
                    rollNo = "BA-2024-022",
                    name = "Sophia Chen",
                    department = "Business Admin",
                    semester = 4,
                    email = "sophia.chen@campus.edu",
                    phone = "+1 (555) 567-8901",
                    cgpa = 3.78,
                    feeStatus = "Paid",
                    attendanceRate = 89,
                    guardianName = "Hao Chen",
                    guardianPhone = "+1 (555) 654-4455",
                    address = "Maple Residences, Room 405"
                ),
                Student(
                    rollNo = "ME-2024-031",
                    name = "Liam O'Connor",
                    department = "Mechanical Eng",
                    semester = 2,
                    email = "liam.oc@campus.edu",
                    phone = "+1 (555) 678-9012",
                    cgpa = 2.95,
                    feeStatus = "Overdue",
                    attendanceRate = 71, // Low attendance flag
                    guardianName = "Sean O'Connor",
                    guardianPhone = "+1 (555) 543-5566",
                    address = "Cedar Hall, Room 201"
                ),
                Student(
                    rollNo = "BT-2024-005",
                    name = "Ananya Patel",
                    department = "Biotechnology",
                    semester = 6,
                    email = "ananya.p@campus.edu",
                    phone = "+1 (555) 789-0123",
                    cgpa = 3.88,
                    feeStatus = "Paid",
                    attendanceRate = 96,
                    guardianName = "Ramesh Patel",
                    guardianPhone = "+1 (555) 432-6677",
                    address = "Oak Ridge Hall, Room 218"
                ),
                Student(
                    rollNo = "CS-2024-049",
                    name = "Devon Miller",
                    department = "Computer Science",
                    semester = 4,
                    email = "devon.m@campus.edu",
                    phone = "+1 (555) 890-1234",
                    cgpa = 3.15,
                    feeStatus = "Pending",
                    attendanceRate = 82,
                    guardianName = "Karen Miller",
                    guardianPhone = "+1 (555) 321-7788",
                    address = "Off Campus - North St"
                )
            )
            studentDao.insertStudents(students)

            val facultyList = listOf(
                Faculty(
                    empId = "FAC-101",
                    name = "Dr. Alan Turing",
                    department = "Computer Science",
                    designation = "Professor & HOD",
                    email = "a.turing@campus.edu",
                    phone = "+1 (555) 100-2001",
                    officeRoom = "Turing Tech Wing, Room 401",
                    subjects = "Distributed Systems, Algorithms"
                ),
                Faculty(
                    empId = "FAC-102",
                    name = "Dr. Margaret Hamilton",
                    department = "Computer Science",
                    designation = "Professor",
                    email = "m.hamilton@campus.edu",
                    phone = "+1 (555) 100-2002",
                    officeRoom = "Software Eng Lab, Room 310",
                    subjects = "Operating Systems, Software Architecture"
                ),
                Faculty(
                    empId = "FAC-103",
                    name = "Dr. Katherine Johnson",
                    department = "Electronics & Comm",
                    designation = "Associate Professor",
                    email = "k.johnson@campus.edu",
                    phone = "+1 (555) 100-2003",
                    officeRoom = "Signals & Comms Block, Room 204",
                    subjects = "Digital Signal Processing, Microprocessors"
                ),
                Faculty(
                    empId = "FAC-104",
                    name = "Prof. Robert Sterling",
                    department = "Business Admin",
                    designation = "Associate Professor",
                    email = "r.sterling@campus.edu",
                    phone = "+1 (555) 100-2004",
                    officeRoom = "Management Tower, Room 102",
                    subjects = "Strategic Marketing, Corporate Finance"
                ),
                Faculty(
                    empId = "FAC-105",
                    name = "Dr. Rosalind Franklin",
                    department = "Biotechnology",
                    designation = "Assistant Professor",
                    email = "r.franklin@campus.edu",
                    phone = "+1 (555) 100-2005",
                    officeRoom = "Bio-Nano Sciences, Room 115",
                    subjects = "Genomics, Cellular Microbiology"
                )
            )
            facultyDao.insertAll(facultyList)

            val courses = listOf(
                Course(
                    code = "CS-301",
                    title = "Distributed Systems & Cloud",
                    department = "Computer Science",
                    credits = 4,
                    facultyName = "Dr. Alan Turing",
                    scheduleDay = "Monday",
                    timeSlot = "09:00 AM - 10:30 AM",
                    room = "Hall A-102"
                ),
                Course(
                    code = "CS-302",
                    title = "Database Architecture & SQL",
                    department = "Computer Science",
                    credits = 3,
                    facultyName = "Dr. Margaret Hamilton",
                    scheduleDay = "Monday",
                    timeSlot = "11:00 AM - 12:30 PM",
                    room = "Lab C-301"
                ),
                Course(
                    code = "EC-204",
                    title = "Digital Signal Processing",
                    department = "Electronics & Comm",
                    credits = 4,
                    facultyName = "Dr. Katherine Johnson",
                    scheduleDay = "Tuesday",
                    timeSlot = "10:00 AM - 11:30 AM",
                    room = "Engineering Block 2"
                ),
                Course(
                    code = "BA-201",
                    title = "Organizational Leadership",
                    department = "Business Admin",
                    credits = 3,
                    facultyName = "Prof. Robert Sterling",
                    scheduleDay = "Wednesday",
                    timeSlot = "01:30 PM - 03:00 PM",
                    room = "Management Hall 1"
                ),
                Course(
                    code = "BT-305",
                    title = "Molecular Genetics & Bio-Informatics",
                    department = "Biotechnology",
                    credits = 4,
                    facultyName = "Dr. Rosalind Franklin",
                    scheduleDay = "Thursday",
                    timeSlot = "09:30 AM - 11:00 AM",
                    room = "Bio Research Lab 4"
                ),
                Course(
                    code = "ME-202",
                    title = "Fluid Mechanics & Thermodynamics",
                    department = "Mechanical Eng",
                    credits = 4,
                    facultyName = "Dr. Alan Turing",
                    scheduleDay = "Friday",
                    timeSlot = "02:00 PM - 03:30 PM",
                    room = "Mech Workshop 3"
                )
            )
            courseDao.insertAll(courses)

            val notices = listOf(
                Notice(
                    title = "Fall Semester Midterm Examination Schedule Published",
                    content = "The mid-term examination timetable for all undergraduate and postgraduate engineering & business programs has been published on the student portal. Exams commence from October 15th. Check hall permits.",
                    category = "Exams",
                    date = "Oct 02, 2026",
                    isUrgent = true,
                    author = "Dean of Academic Affairs"
                ),
                Notice(
                    title = "Annual University Tech Symposium - 'Innovate 2026'",
                    content = "Registration is now open for project showcase, robotic obstacle course, and AI hackathon. Grand prize pool of $15,000 sponsored by tech partners. Submit project abstracts before Oct 10th.",
                    category = "Events",
                    date = "Sep 29, 2026",
                    isUrgent = false,
                    author = "Student Council & Tech Society"
                ),
                Notice(
                    title = "Campus Placement Drive: Top Tier Tech Companies",
                    content = "Pre-placement talks and mock interview sessions for final year and pre-final year students will be conducted this Friday in the Main Auditorium starting at 10 AM. Formal dress code mandatory.",
                    category = "Placement",
                    date = "Sep 28, 2026",
                    isUrgent = true,
                    author = "Career Development Center"
                ),
                Notice(
                    title = "Library System Maintenance & Digital Portal Upgrade",
                    content = "The university digital library portal will undergo scheduled maintenance this Sunday from 01:00 AM to 06:00 AM. Physical library study rooms remain operational 24/7.",
                    category = "General",
                    date = "Sep 25, 2026",
                    isUrgent = false,
                    author = "Campus IT Services"
                )
            )
            noticeDao.insertAll(notices)

            val fees = listOf(
                FeeRecord(
                    studentId = 1,
                    studentName = "Aarav Sharma",
                    rollNo = "CS-2024-001",
                    feeType = "Tuition Fee - Sem 6",
                    amount = 3200.0,
                    dueDate = "Sep 15, 2026",
                    status = "Paid",
                    receiptNo = "REC-94821",
                    paidDate = "Sep 12, 2026"
                ),
                FeeRecord(
                    studentId = 2,
                    studentName = "Elena Rostova",
                    rollNo = "CS-2024-014",
                    feeType = "Tuition Fee - Sem 6",
                    amount = 3200.0,
                    dueDate = "Sep 15, 2026",
                    status = "Paid",
                    receiptNo = "REC-94850",
                    paidDate = "Sep 14, 2026"
                ),
                FeeRecord(
                    studentId = 3,
                    studentName = "Marcus Vance",
                    rollNo = "EC-2024-008",
                    feeType = "Laboratory & Equipment Fee",
                    amount = 850.0,
                    dueDate = "Oct 10, 2026",
                    status = "Pending"
                ),
                FeeRecord(
                    studentId = 5,
                    studentName = "Liam O'Connor",
                    rollNo = "ME-2024-031",
                    feeType = "Tuition Fee - Sem 2",
                    amount = 3000.0,
                    dueDate = "Aug 30, 2026",
                    status = "Overdue"
                ),
                FeeRecord(
                    studentId = 4,
                    studentName = "Sophia Chen",
                    rollNo = "BA-2024-022",
                    feeType = "Hostel & Dining Plan",
                    amount = 1450.0,
                    dueDate = "Sep 20, 2026",
                    status = "Paid",
                    receiptNo = "REC-95104",
                    paidDate = "Sep 18, 2026"
                )
            )
            feeDao.insertAll(fees)

            val attendance = listOf(
                AttendanceRecord(
                    studentId = 1,
                    studentName = "Aarav Sharma",
                    rollNo = "CS-2024-001",
                    courseCode = "CS-301",
                    date = "2026-10-02",
                    status = "Present"
                ),
                AttendanceRecord(
                    studentId = 2,
                    studentName = "Elena Rostova",
                    rollNo = "CS-2024-014",
                    courseCode = "CS-301",
                    date = "2026-10-02",
                    status = "Present"
                ),
                AttendanceRecord(
                    studentId = 7,
                    studentName = "Devon Miller",
                    rollNo = "CS-2024-049",
                    courseCode = "CS-301",
                    date = "2026-10-02",
                    status = "Late"
                )
            )
            attendanceDao.insertAll(attendance)
        }
    }
}
