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
import com.example.data.model.Course
import com.example.data.model.Student
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusPresent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    students: List<Student>,
    courses: List<Course>,
    selectedCourseCode: String,
    attendanceMap: Map<Long, String>,
    dateStr: String,
    onSelectCourse: (String) -> Unit,
    onSetStatus: (Long, String) -> Unit,
    onMarkAllPresent: (List<Student>) -> Unit,
    onSaveSession: (List<Student>) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val courseOptions = remember(courses) {
        if (courses.isNotEmpty()) courses.map { it.code } else listOf("CS-301", "CS-302", "EC-204", "BA-201")
    }

    val presentCount = students.count { (attendanceMap[it.id] ?: "Present") == "Present" }
    val absentCount = students.count { (attendanceMap[it.id] ?: "Present") == "Absent" }
    val lateCount = students.count { (attendanceMap[it.id] ?: "Present") == "Late" }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onMarkAllPresent(students) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("All Present")
                    }

                    Button(
                        onClick = {
                            onSaveSession(students)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Attendance session saved for $selectedCourseCode ($dateStr)!"
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("save_attendance_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Session")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("attendance_screen")
        ) {
            // Course selection bar
            Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                Text(
                    text = "Select Course Lecture Session:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(courseOptions) { code ->
                        FilterChip(
                            selected = selectedCourseCode == code,
                            onClick = { onSelectCourse(code) },
                            label = { Text(code, fontWeight = FontWeight.Bold) },
                            leadingIcon = if (selectedCourseCode == code) {
                                { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            // Session Stats Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Date: $dateStr",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total Roll Call: ${students.size} students",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusPresent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "P: $presentCount",
                                color = StatusPresent,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusLate.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "L: $lateCount",
                                color = StatusLate,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusAbsent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "A: $absentCount",
                                color = StatusAbsent,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Student attendance list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students, key = { it.id }) { student ->
                    val status = attendanceMap[student.id] ?: "Present"
                    AttendanceStudentRow(
                        student = student,
                        status = status,
                        onStatusChange = { newStatus -> onSetStatus(student.id, newStatus) }
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceStudentRow(
    student: Student,
    status: String,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = student.name.firstOrNull()?.toString() ?: "S"
                    Text(initial, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                Column {
                    Text(student.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${student.rollNo} • ${student.department.take(15)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3-state toggle chips
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Present
                FilterChip(
                    selected = status == "Present",
                    onClick = { onStatusChange("Present") },
                    label = { Text("P", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusPresent.copy(alpha = 0.2f),
                        selectedLabelColor = StatusPresent
                    )
                )

                // Late
                FilterChip(
                    selected = status == "Late",
                    onClick = { onStatusChange("Late") },
                    label = { Text("L", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusLate.copy(alpha = 0.2f),
                        selectedLabelColor = StatusLate
                    )
                )

                // Absent
                FilterChip(
                    selected = status == "Absent",
                    onClick = { onStatusChange("Absent") },
                    label = { Text("A", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusAbsent.copy(alpha = 0.2f),
                        selectedLabelColor = StatusAbsent
                    )
                )
            }
        }
    }
}
