package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_app_clone.network.NotificationResponse
import com.example.chat_app_clone.ui.components.MessengerBottomNavBar
import com.example.chat_app_clone.ui.theme.MessengerBlue
import com.example.chat_app_clone.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNotificationClick: (NotificationResponse) -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onPeopleTabClick: () -> Unit = {},
    onSettingTabClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(2) }

    val notifications = uiState.notifications
    val unread = notifications.filter { !it.isRead }

    val isLightTheme = MaterialTheme.colorScheme.surface == Color.White
    val chipBg = if (isLightTheme) Color(0xFFF0F2F5) else Color(0xFF3E4042)

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(chipBg)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (unread.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.markAllAsRead() },
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Text(
                                "Mark all read",
                                fontSize = 14.sp,
                                color = MessengerBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            MessengerBottomNavBar(
                selectedIndex = selectedTab,
                notificationBadgeCount = unread.size,
                onItemSelected = { index ->
                    selectedTab = index
                    when (index) {
                        0 -> onHomeTabClick()
                        1 -> onPeopleTabClick()
                        3 -> onSettingTabClick()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && notifications.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (notifications.isEmpty()) {
                EmptyNotificationsView()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notifications, key = { it.id }) { item ->
                        NotificationRow(
                            item = item,
                            onClick = {
                                viewModel.markAsRead(item.id)
                                onNotificationClick(item)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationRow(
    item: NotificationResponse,
    onClick: () -> Unit
) {
    val rowBg = if (!item.isRead)
        if (MaterialTheme.colorScheme.surface == Color.White) Color(0xFFE8F0FE) else Color(0xFF1E2A3A)
    else
        Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when (item.type) {
            "friend_request" -> Icons.Default.PersonAdd
            "friend_accepted" -> Icons.Default.Person
            "message" -> Icons.Default.Message
            else -> Icons.Default.Notifications
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MessengerBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MessengerBlue)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.content,
                fontSize = 14.sp,
                color = if (item.isRead) Color.Gray else MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatNotificationDate(item.createdAt),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (!item.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MessengerBlue)
            )
        }
    }
}

fun formatNotificationDate(dateStr: String): String {
    return try {
        if (dateStr.contains("T")) {
            dateStr.substringBefore("T")
        } else if (dateStr.contains(" ")) {
            dateStr.substringBefore(" ")
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun EmptyNotificationsView() {
    val searchBarBg = if (MaterialTheme.colorScheme.surface == Color.White)
        Color(0xFFF0F2F5) else Color(0xFF3E4042)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(searchBarBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No notifications yet",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "You're all caught up!",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotifScreenPreview() {
    NotificationScreen()
}