package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.FeeRecord
import com.example.data.model.Notice
import com.example.data.model.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentDialog(
    initialStudent: Student? = null,
    onDismiss: () -> Unit,
    onConfirm: (Student) -> Unit
) {
    var rollNo by remember { mutableStateOf(initialStudent?.rollNo ?: "") }
    var name by remember { mutableStateOf(initialStudent?.name ?: "") }
    var department by remember { mutableStateOf(initialStudent?.department ?: "Computer Science") }
    var semesterText by remember { mutableStateOf(initialStudent?.semester?.toString() ?: "1") }
    var cgpaText by remember { mutableStateOf(initialStudent?.cgpa?.toString() ?: "3.50") }
    var email by remember { mutableStateOf(initialStudent?.email ?: "") }
    var phone by remember { mutableStateOf(initialStudent?.phone ?: "") }
    var feeStatus by remember { mutableStateOf(initialStudent?.feeStatus ?: "Paid") }
    var attendanceText by remember { mutableStateOf(initialStudent?.attendanceRate?.toString() ?: "85") }
    var guardianName by remember { mutableStateOf(initialStudent?.guardianName ?: "") }
    var guardianPhone by remember { mutableStateOf(initialStudent?.guardianPhone ?: "") }
    var address by remember { mutableStateOf(initialStudent?.address ?: "") }

    var deptExpanded by remember { mutableStateOf(false) }
    val departments = listOf(
        "Computer Science",
        "Electronics & Comm",
        "Mechanical Eng",
        "Business Admin",
        "Biotechnology"
    )

    var feeExpanded by remember { mutableStateOf(false) }
    val feeStatuses = listOf("Paid", "Pending", "Overdue")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initialStudent == null) "Add New Student" else "Edit Student Profile",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("student_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = rollNo,
                    onValueChange = { rollNo = it },
                    label = { Text("Roll Number * (e.g. CS-2024-055)") },
                    modifier = Modifier.fillMaxWidth().testTag("student_roll_input"),
                    singleLine = true
                )

                // Department Exposed Dropdown
                ExposedDropdownMenuBox(
                    expanded = deptExpanded,
                    onExpandedChange = { deptExpanded = !deptExpanded }
                ) {
                    OutlinedTextField(
                        value = department,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = deptExpanded,
                        onDismissRequest = { deptExpanded = false }
                    ) {
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text(dept) },
                                onClick = {
                                    department = dept
                                    deptExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = semesterText,
                        onValueChange = { semesterText = it },
                        label = { Text("Semester (1-8)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cgpaText,
                        onValueChange = { cgpaText = it },
                        label = { Text("CGPA (0-4.0)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = attendanceText,
                        onValueChange = { attendanceText = it },
                        label = { Text("Attendance %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    ExposedDropdownMenuBox(
                        expanded = feeExpanded,
                        onExpandedChange = { feeExpanded = !feeExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = feeStatus,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fee Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = feeExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = feeExpanded,
                            onDismissRequest = { feeExpanded = false }
                        ) {
                            feeStatuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        feeStatus = status
                                        feeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Campus Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = guardianName,
                    onValueChange = { guardianName = it },
                    label = { Text("Guardian / Parent Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Hostel / Residential Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && rollNo.isNotBlank()) {
                        val student = Student(
                            id = initialStudent?.id ?: 0,
                            rollNo = rollNo.trim(),
                            name = name.trim(),
                            department = department,
                            semester = semesterText.toIntOrNull() ?: 1,
                            email = if (email.isBlank()) "${name.lowercase().replace(" ", ".")}@campus.edu" else email.trim(),
                            phone = if (phone.isBlank()) "+1 (555) 000-0000" else phone.trim(),
                            cgpa = cgpaText.toDoubleOrNull() ?: 3.0,
                            feeStatus = feeStatus,
                            attendanceRate = attendanceText.toIntOrNull() ?: 80,
                            guardianName = guardianName.trim(),
                            guardianPhone = guardianPhone.trim(),
                            address = address.trim()
                        )
                        onConfirm(student)
                    }
                },
                modifier = Modifier.testTag("save_student_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFacultyDialog(
    initialFaculty: Faculty? = null,
    onDismiss: () -> Unit,
    onConfirm: (Faculty) -> Unit
) {
    var name by remember { mutableStateOf(initialFaculty?.name ?: "") }
    var empId by remember { mutableStateOf(initialFaculty?.empId ?: "") }
    var department by remember { mutableStateOf(initialFaculty?.department ?: "Computer Science") }
    var designation by remember { mutableStateOf(initialFaculty?.designation ?: "Assistant Professor") }
    var email by remember { mutableStateOf(initialFaculty?.email ?: "") }
    var phone by remember { mutableStateOf(initialFaculty?.phone ?: "") }
    var officeRoom by remember { mutableStateOf(initialFaculty?.officeRoom ?: "") }
    var subjects by remember { mutableStateOf(initialFaculty?.subjects ?: "") }

    var deptExpanded by remember { mutableStateOf(false) }
    val departments = listOf(
        "Computer Science",
        "Electronics & Comm",
        "Mechanical Eng",
        "Business Admin",
        "Biotechnology"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialFaculty == null) "Add Faculty Member" else "Edit Faculty", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name * (e.g. Dr. Alan Turing)") },
                    modifier = Modifier.fillMaxWidth().testTag("faculty_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = empId,
                    onValueChange = { empId = it },
                    label = { Text("Faculty Employee ID * (e.g. FAC-109)") },
                    modifier = Modifier.fillMaxWidth().testTag("faculty_id_input"),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = deptExpanded,
                    onExpandedChange = { deptExpanded = !deptExpanded }
                ) {
                    OutlinedTextField(
                        value = department,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = deptExpanded,
                        onDismissRequest = { deptExpanded = false }
                    ) {
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text(dept) },
                                onClick = {
                                    department = dept
                                    deptExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("Designation (e.g. Professor & HOD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Campus Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = officeRoom,
                    onValueChange = { officeRoom = it },
                    label = { Text("Office Room / Cabin") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = subjects,
                    onValueChange = { subjects = it },
                    label = { Text("Subjects Taught (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val faculty = Faculty(
                            id = initialFaculty?.id ?: 0,
                            empId = if (empId.isBlank()) "FAC-${(100..999).random()}" else empId.trim(),
                            name = name.trim(),
                            department = department,
                            designation = if (designation.isBlank()) "Assistant Professor" else designation.trim(),
                            email = if (email.isBlank()) "${name.lowercase().replace(" ", ".")}@campus.edu" else email.trim(),
                            phone = if (phone.isBlank()) "+1 (555) 100-2099" else phone.trim(),
                            officeRoom = if (officeRoom.isBlank()) "Academic Block A-101" else officeRoom.trim(),
                            subjects = if (subjects.isBlank()) "Core Curriculum" else subjects.trim()
                        )
                        onConfirm(faculty)
                    }
                },
                modifier = Modifier.testTag("save_faculty_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onConfirm: (Course) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Computer Science") }
    var creditsText by remember { mutableStateOf("3") }
    var facultyName by remember { mutableStateOf("") }
    var scheduleDay by remember { mutableStateOf("Monday") }
    var timeSlot by remember { mutableStateOf("09:00 AM - 10:30 AM") }
    var room by remember { mutableStateOf("Lecture Hall 101") }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    var dayExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule New Course / Lecture", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course Code * (e.g. CS-401)") },
                    modifier = Modifier.fillMaxWidth().testTag("course_code_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Course Title * (e.g. Artificial Intelligence)") },
                    modifier = Modifier.fillMaxWidth().testTag("course_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = facultyName,
                    onValueChange = { facultyName = it },
                    label = { Text("Faculty In-Charge") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = dayExpanded,
                    onExpandedChange = { dayExpanded = !dayExpanded }
                ) {
                    OutlinedTextField(
                        value = scheduleDay,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Day of Week") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false }
                    ) {
                        days.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    scheduleDay = day
                                    dayExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = timeSlot,
                        onValueChange = { timeSlot = it },
                        label = { Text("Time Slot") },
                        modifier = Modifier.weight(1.4f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = creditsText,
                        onValueChange = { creditsText = it },
                        label = { Text("Credits") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.6f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Classroom / Lab") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && title.isNotBlank()) {
                        val course = Course(
                            code = code.trim().uppercase(),
                            title = title.trim(),
                            department = department,
                            credits = creditsText.toIntOrNull() ?: 3,
                            facultyName = if (facultyName.isBlank()) "Department Faculty" else facultyName.trim(),
                            scheduleDay = scheduleDay,
                            timeSlot = timeSlot.trim(),
                            room = room.trim()
                        )
                        onConfirm(course)
                    }
                },
                modifier = Modifier.testTag("save_course_button")
            ) {
                Text("Add Course")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddNoticeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Notice) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Academic") }
    var isUrgent by remember { mutableStateOf(false) }

    val categories = listOf("Academic", "Exams", "Events", "Placement", "General")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Campus Notice", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Headline *") },
                    modifier = Modifier.fillMaxWidth().testTag("notice_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Notice Content / Circular Details *") },
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("notice_content_input"),
                    maxLines = 5
                )

                Text("Category:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mark as High Priority / Urgent")
                    Switch(
                        checked = isUrgent,
                        onCheckedChange = { isUrgent = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                        val notice = Notice(
                            title = title.trim(),
                            content = content.trim(),
                            category = category,
                            date = currentDate,
                            isUrgent = isUrgent,
                            author = "Office of the Dean"
                        )
                        onConfirm(notice)
                    }
                },
                modifier = Modifier.testTag("save_notice_button")
            ) {
                Text("Publish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddFeeDialog(
    students: List<Student>,
    onDismiss: () -> Unit,
    onConfirm: (FeeRecord) -> Unit
) {
    var selectedStudentIndex by remember { mutableStateOf(0) }
    var feeType by remember { mutableStateOf("Tuition Fee") }
    var amountText by remember { mutableStateOf("3000.00") }
    var dueDate by remember { mutableStateOf("Nov 15, 2026") }

    val feeTypes = listOf("Tuition Fee", "Lab & Equipment", "Hostel & Dining", "Exam Fee", "Library Deposit")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Student Fee Invoice", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (students.isNotEmpty()) {
                    Text("Select Student:", style = MaterialTheme.typography.labelMedium)
                    val student = students.getOrElse(selectedStudentIndex) { students.first() }
                    Text(
                        "${student.name} (${student.rollNo}) - ${student.department}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = feeType,
                    onValueChange = { feeType = it },
                    label = { Text("Fee Category / Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("fee_amount_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (e.g. Nov 15, 2026)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (students.isNotEmpty()) {
                        val s = students[selectedStudentIndex]
                        val fee = FeeRecord(
                            studentId = s.id,
                            studentName = s.name,
                            rollNo = s.rollNo,
                            feeType = feeType.trim(),
                            amount = amountText.toDoubleOrNull() ?: 500.0,
                            dueDate = dueDate.trim(),
                            status = "Pending"
                        )
                        onConfirm(fee)
                    }
                },
                modifier = Modifier.testTag("save_fee_button")
            ) {
                Text("Generate Invoice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
