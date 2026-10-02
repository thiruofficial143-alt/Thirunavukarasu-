package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Student
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusPaid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    students: List<Student>,
    searchQuery: String,
    selectedDepartment: String,
    onSearchChange: (String) -> Unit,
    onDepartmentSelect: (String) -> Unit,
    onStudentClick: (Student) -> Unit,
    onAddStudentClick: () -> Unit
) {
    val departments = listOf(
        "All",
        "Computer Science",
        "Electronics & Comm",
        "Mechanical Eng",
        "Business Admin",
        "Biotechnology"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddStudentClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_student_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Enroll New Student")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("students_screen")
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("student_search_bar"),
                placeholder = { Text("Search by name, roll no, or department...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Department Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(departments) { dept ->
                    FilterChip(
                        selected = selectedDepartment == dept,
                        onClick = { onDepartmentSelect(dept) },
                        label = { Text(dept) },
                        leadingIcon = if (selectedDepartment == dept) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            // Results count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${students.size} enrolled students",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Student List
            if (students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "No students found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Try clearing your search query or department filter, or enroll a new student.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(students, key = { it.id }) { student ->
                        StudentCard(
                            student = student,
                            onClick = { onStudentClick(student) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(
    student: Student,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("student_card_${student.rollNo}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Initial circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = student.name.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")
                    Text(
                        initials,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${student.rollNo} • Sem ${student.semester}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = student.department,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // CGPA Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        student.cgpa >= 3.5 -> EmeraldTertiary.copy(alpha = 0.15f)
                        student.cgpa >= 3.0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else -> StatusLate.copy(alpha = 0.15f)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format("%.2f", student.cgpa),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                student.cgpa >= 3.5 -> EmeraldTertiary
                                student.cgpa >= 3.0 -> MaterialTheme.colorScheme.primary
                                else -> StatusLate
                            }
                        )
                        Text(
                            "CGPA",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Footer with Attendance Rate and Fee Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attendance
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (student.attendanceRate >= 75) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (student.attendanceRate >= 75) EmeraldTertiary else StatusAbsent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Attendance: ${student.attendanceRate}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (student.attendanceRate >= 75) MaterialTheme.colorScheme.onSurface else StatusAbsent
                    )
                }

                // Fee Status Tag
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (student.feeStatus) {
                        "Paid" -> StatusPaid.copy(alpha = 0.15f)
                        "Pending" -> StatusLate.copy(alpha = 0.15f)
                        else -> StatusAbsent.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = "Fees: ${student.feeStatus}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (student.feeStatus) {
                            "Paid" -> StatusPaid
                            "Pending" -> StatusLate
                            else -> StatusAbsent
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
