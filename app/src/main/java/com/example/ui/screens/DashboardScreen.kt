package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.FeeRecord
import com.example.data.model.Notice
import com.example.data.model.Student
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusLate
import com.example.ui.viewmodel.CampusNavDestination
import com.example.ui.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    students: List<Student>,
    facultyList: List<Faculty>,
    courses: List<Course>,
    notices: List<Notice>,
    fees: List<FeeRecord>,
    currentRole: UserRole,
    onNavigate: (CampusNavDestination) -> Unit,
    onOpenAddStudent: () -> Unit,
    onOpenAddNotice: () -> Unit,
    onOpenAddFee: () -> Unit,
    onSelectStudent: (Student) -> Unit
) {
    val lowAttendanceStudents = remember(students) {
        students.filter { it.attendanceRate < 75 }
    }

    val avgAttendance = remember(students) {
        if (students.isEmpty()) 0 else students.sumOf { it.attendanceRate } / students.size
    }

    val totalPendingFees = remember(fees) {
        fees.filter { it.status != "Paid" }.sumOf { it.amount }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Campus Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_campus_hero),
                    contentDescription = "Campus Quad & Library",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient scrim overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Fall Session 2026 • ${currentRole.label}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "CampusCore University Portal",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Excellence in Higher Education & Academic Governance",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Quick Stats Row
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Institutional Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Students",
                        value = "${students.size}",
                        subtitle = "Active Enrolled",
                        icon = Icons.Default.People,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(CampusNavDestination.STUDENTS) }
                    )
                    StatCard(
                        title = "Faculty",
                        value = "${facultyList.size}",
                        subtitle = "Professors & HODs",
                        icon = Icons.Default.Badge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(CampusNavDestination.FACULTY) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Avg Attendance",
                        value = "$avgAttendance%",
                        subtitle = if (avgAttendance >= 75) "Optimal State" else "Needs Attention",
                        icon = Icons.Default.EventNote,
                        color = if (avgAttendance >= 75) EmeraldTertiary else StatusAbsent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(CampusNavDestination.ATTENDANCE) }
                    )
                    StatCard(
                        title = "Pending Fees",
                        value = "$${String.format("%,.0f", totalPendingFees)}",
                        subtitle = "${fees.count { it.status != "Paid" }} Invoices Due",
                        icon = Icons.Default.AccountBalance,
                        color = if (totalPendingFees > 0) StatusLate else EmeraldTertiary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(CampusNavDestination.FINANCE_NOTICES) }
                    )
                }
            }
        }

        // Quick Action Shortcuts
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = "Quick Administrative Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        ActionChipButton(
                            label = "Enroll Student",
                            icon = Icons.Default.PersonAdd,
                            onClick = onOpenAddStudent
                        )
                    }
                    item {
                        ActionChipButton(
                            label = "Take Attendance",
                            icon = Icons.Default.FactCheck,
                            onClick = { onNavigate(CampusNavDestination.ATTENDANCE) }
                        )
                    }
                    item {
                        ActionChipButton(
                            label = "Publish Notice",
                            icon = Icons.Default.Campaign,
                            onClick = onOpenAddNotice
                        )
                    }
                    item {
                        ActionChipButton(
                            label = "Issue Fee Invoice",
                            icon = Icons.Default.ReceiptLong,
                            onClick = onOpenAddFee
                        )
                    }
                    item {
                        ActionChipButton(
                            label = "Manage Schedule",
                            icon = Icons.Default.Schedule,
                            onClick = { onNavigate(CampusNavDestination.COURSES) }
                        )
                    }
                }
            }
        }

        // Low Attendance Warning Alert (if any)
        if (lowAttendanceStudents.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Academic Alert: Low Attendance (< 75%)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${lowAttendanceStudents.size} student(s) risk exam ineligibility. Immediate faculty advisor notification recommended.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        lowAttendanceStudents.forEach { student ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onSelectStudent(student) },
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(student.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                        Text("${student.rollNo} • ${student.department}", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ) {
                                        Text("${student.attendanceRate}%", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Timetable Preview
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Class Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigate(CampusNavDestination.COURSES) }) {
                        Text("View Full Timetable")
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                val previewCourses = courses.take(3)
                if (previewCourses.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            "No lectures scheduled for today.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    previewCourses.forEach { course ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        course.code.take(2),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(course.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "${course.timeSlot} • ${course.room}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Faculty: ${course.facultyName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("${course.credits} Cr") }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Campus Circulars & Bulletins Preview
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Campus Bulletins",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigate(CampusNavDestination.FINANCE_NOTICES) }) {
                        Text("All Notices")
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                notices.take(2).forEach { notice ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (notice.isUrgent) {
                                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                                            Text("URGENT", color = Color.White)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(notice.category) }
                                    )
                                }
                                Text(
                                    notice.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(notice.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                notice.content,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ActionChipButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}
