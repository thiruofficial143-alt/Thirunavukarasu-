package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.FeeRecord
import com.example.data.model.Notice
import com.example.data.model.Student
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusPaid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceNoticesScreen(
    fees: List<FeeRecord>,
    notices: List<Notice>,
    students: List<Student>,
    onAddFeeClick: () -> Unit,
    onAddNoticeClick: () -> Unit,
    onMarkFeePaid: (FeeRecord) -> Unit,
    onDeleteFee: (FeeRecord) -> Unit,
    onDeleteNotice: (Notice) -> Unit
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Fees, 1: Notices
    var feeStatusFilter by remember { mutableStateOf("All") }
    var noticeCategoryFilter by remember { mutableStateOf("All") }

    val filteredFees = remember(fees, feeStatusFilter) {
        if (feeStatusFilter == "All") fees else fees.filter { it.status == feeStatusFilter }
    }

    val filteredNotices = remember(notices, noticeCategoryFilter) {
        if (noticeCategoryFilter == "All") notices else notices.filter { it.category == noticeCategoryFilter }
    }

    val totalCollected = remember(fees) { fees.filter { it.status == "Paid" }.sumOf { it.amount } }
    val totalPending = remember(fees) { fees.filter { it.status != "Paid" }.sumOf { it.amount } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedSubTab == 0) onAddFeeClick() else onAddNoticeClick()
                },
                containerColor = if (selectedSubTab == 0) EmeraldTertiary else MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("hub_fab")
            ) {
                Icon(
                    if (selectedSubTab == 0) Icons.Default.AddCard else Icons.Default.Campaign,
                    contentDescription = if (selectedSubTab == 0) "Add Fee" else "Add Notice"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("finance_notices_screen")
        ) {
            // Segmented Sub-tab Switcher
            TabRow(
                selectedTabIndex = selectedSubTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fees & Accounts", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Notices & Circulars", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }

            if (selectedSubTab == 0) {
                // FEES VIEW
                // Metrics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Collected Revenue", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "$${String.format("%,.0f", totalCollected)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldTertiary
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Pending Receivables", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "$${String.format("%,.0f", totalPending)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = StatusLate
                            )
                        }
                    }
                }

                // Filter chips
                val feeStatuses = listOf("All", "Pending", "Overdue", "Paid")
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(feeStatuses) { status ->
                        FilterChip(
                            selected = feeStatusFilter == status,
                            onClick = { feeStatusFilter = status },
                            label = { Text(status) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredFees.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No fee invoices match filter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredFees, key = { it.id }) { fee ->
                            FeeCard(
                                fee = fee,
                                onMarkPaid = { onMarkFeePaid(fee) },
                                onDelete = { onDeleteFee(fee) }
                            )
                        }
                    }
                }
            } else {
                // NOTICES VIEW
                val noticeCategories = listOf("All", "Exams", "Events", "Placement", "Academic", "General")
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(noticeCategories) { cat ->
                        FilterChip(
                            selected = noticeCategoryFilter == cat,
                            onClick = { noticeCategoryFilter = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                if (filteredNotices.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No campus notices in this category.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredNotices, key = { it.id }) { notice ->
                            NoticeCard(
                                notice = notice,
                                onDelete = { onDeleteNotice(notice) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeeCard(
    fee: FeeRecord,
    onMarkPaid: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(fee.studentName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Roll No: ${fee.rollNo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (fee.status) {
                        "Paid" -> StatusPaid.copy(alpha = 0.15f)
                        "Pending" -> StatusLate.copy(alpha = 0.15f)
                        else -> StatusAbsent.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = fee.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (fee.status) {
                            "Paid" -> StatusPaid
                            "Pending" -> StatusLate
                            else -> StatusAbsent
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(fee.feeType, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "$${String.format("%,.2f", fee.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                if (fee.status == "Paid") "Receipt: ${fee.receiptNo ?: "N/A"} • Paid: ${fee.paidDate ?: fee.dueDate}" else "Due Date: ${fee.dueDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (fee.status != "Paid") {
                    FilledTonalButton(
                        onClick = onMarkPaid,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Payment", style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusPaid, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Settled & Cleared", style = MaterialTheme.typography.labelMedium, color = StatusPaid)
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Invoice", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun NoticeCard(
    notice: Notice,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(14.dp)
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(notice.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(notice.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(notice.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Issued by: ${notice.author}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}
