package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.ui.components.MessengerBottomNavBar
import com.example.chat_app_clone.ui.components.UserAvatar
import com.example.chat_app_clone.ui.theme.MessengerBlue

// ─── Data model ────────────────────────────────────────────────────────────────

enum class NotificationType {
    MESSAGE, FRIEND_REQUEST, FRIEND_ACCEPTED, MENTION, GROUP_INVITE
}

data class NotificationItem(
    val id: Long,
    val type: NotificationType,
    val senderName: String,
    val senderInitials: String,
    val isOnline: Boolean = false,
    val previewText: String,
    val timeLabel: String,
    val isRead: Boolean = false,
    /** For FRIEND_REQUEST — actions still pending */
    val isPendingRequest: Boolean = false
)

// ─── Sample data ────────────────────────────────────────────────────────────────

private val sampleNotifications = listOf(
    NotificationItem(
        id = 1,
        type = NotificationType.MESSAGE,
        senderName = "Alice Johnson",
        senderInitials = "AJ",
        isOnline = true,
        previewText = "Hey, are you free this weekend?",
        timeLabel = "2m ago",
        isRead = false
    ),
    NotificationItem(
        id = 2,
        type = NotificationType.FRIEND_REQUEST,
        senderName = "Bob Martinez",
        senderInitials = "BM",
        isOnline = false,
        previewText = "Sent you a friend request",
        timeLabel = "15m ago",
        isRead = false,
        isPendingRequest = true
    ),
    NotificationItem(
        id = 3,
        type = NotificationType.FRIEND_ACCEPTED,
        senderName = "Clara Nguyen",
        senderInitials = "CN",
        isOnline = true,
        previewText = "Accepted your friend request",
        timeLabel = "1h ago",
        isRead = false
    ),
    NotificationItem(
        id = 4,
        type = NotificationType.MENTION,
        senderName = "Dev Team",
        senderInitials = "DT",
        isOnline = false,
        previewText = "Mentioned you in a message",
        timeLabel = "3h ago",
        isRead = true
    ),
    NotificationItem(
        id = 5,
        type = NotificationType.GROUP_INVITE,
        senderName = "Sara Kim",
        senderInitials = "SK",
        isOnline = false,
        previewText = "Invited you to Weekend Hikers",
        timeLabel = "Yesterday",
        isRead = true
    ),
    NotificationItem(
        id = 6,
        type = NotificationType.MESSAGE,
        senderName = "James Lee",
        senderInitials = "JL",
        isOnline = false,
        previewText = "Can you send me those files?",
        timeLabel = "Yesterday",
        isRead = true
    ),
    NotificationItem(
        id = 7,
        type = NotificationType.FRIEND_REQUEST,
        senderName = "Priya Patel",
        senderInitials = "PP",
        isOnline = true,
        previewText = "Sent you a friend request",
        timeLabel = "2 days ago",
        isRead = true,
        isPendingRequest = true
    ),
)

// ─── Screen ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit = {},
    onNotificationClick: (NotificationItem) -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onPeopleTabClick: () -> Unit = {},
    onSettingTabClick: () -> Unit = {}
) {
    var notifications by remember { mutableStateOf(sampleNotifications) }
    var selectedTab by remember { mutableIntStateOf(2) } // adjust index to match your nav

    val searchBarBg = if (MaterialTheme.colorScheme.surface == Color.White)
        Color(0xFFF0F2F5) else Color(0xFF3E4042)

    val unread = notifications.filter { !it.isRead }
    val earlier = notifications.filter { it.isRead }

    fun markAllRead() {
        notifications = notifications.map { it.copy(isRead = true) }
    }

    fun markRead(id: Long) {
        notifications = notifications.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    fun acceptRequest(id: Long) {
        notifications = notifications.map {
            if (it.id == id) it.copy(isPendingRequest = false, isRead = true) else it
        }
    }

    fun dismissRequest(id: Long) {
        notifications = notifications.filter { it.id != id }
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
                actions = {
                    if (unread.isNotEmpty()) {
                        TextButton(
                            onClick = { markAllRead() },
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
            if (notifications.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
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
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    // ── New / Unread ─────────────────────────────────────────
                    if (unread.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "New",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        items(unread, key = { it.id }) { notif ->
                            NotificationRow(
                                item = notif,
                                isUnread = true,
                                searchBarBg = searchBarBg,
                                onClick = {
                                    markRead(notif.id)
                                    onNotificationClick(notif)
                                },
                                onAccept = { acceptRequest(notif.id) },
                                onDismiss = { dismissRequest(notif.id) }
                            )
                        }
                    }

                    // ── Earlier / Read ───────────────────────────────────────
                    if (earlier.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.padding(
                                    start = 16.dp, end = 16.dp,
                                    top = if (unread.isNotEmpty()) 12.dp else 4.dp,
                                    bottom = 4.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Earlier",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        items(earlier, key = { it.id }) { notif ->
                            NotificationRow(
                                item = notif,
                                isUnread = false,
                                searchBarBg = searchBarBg,
                                onClick = { onNotificationClick(notif) },
                                onAccept = { acceptRequest(notif.id) },
                                onDismiss = { dismissRequest(notif.id) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

// ─── Row ────────────────────────────────────────────────────────────────────────

@Composable
private fun NotificationRow(
    item: NotificationItem,
    isUnread: Boolean,
    searchBarBg: Color,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    val rowBg = if (isUnread)
        if (MaterialTheme.colorScheme.surface == Color.White) Color(0xFFE8F0FE) else Color(0xFF1E2A3A)
    else
        Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar + type badge
        Box(modifier = Modifier.size(56.dp)) {
            UserAvatar(name = item.senderInitials, size = 56, isOnline = false)

            // Online dot — same style as HomeScreen
            if (item.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF44C553))
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }

            // Notification type badge — bottom-start
            NotificationTypeBadge(
                type = item.type,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Name + time on same line
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.senderName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.timeLabel,
                    fontSize = 12.sp,
                    color = if (isUnread) MessengerBlue else Color.Gray,
                    fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.previewText,
                fontSize = 13.sp,
                color = if (isUnread) MaterialTheme.colorScheme.onSurface else Color.Gray,
                fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Action buttons for pending friend requests
            if (item.isPendingRequest) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(
                        onClick = onAccept,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Confirm", fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onDismiss,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Delete", fontSize = 13.sp)
                    }
                }
            }
        }

        // Unread blue dot
        if (isUnread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MessengerBlue)
            )
        }
    }
}

// ─── Type badge ──────────────────────────────────────────────────────────────────

@Composable
private fun NotificationTypeBadge(type: NotificationType, modifier: Modifier = Modifier) {
    val (icon, badgeColor) = when (type) {
        NotificationType.MESSAGE       -> Icons.Default.Chat          to MessengerBlue
        NotificationType.FRIEND_REQUEST,
        NotificationType.FRIEND_ACCEPTED -> Icons.Default.PersonAdd   to Color(0xFF44C553)
        NotificationType.MENTION       -> Icons.Default.AlternateEmail to Color(0xFFFF9500)
        NotificationType.GROUP_INVITE  -> Icons.Default.Group          to Color(0xFF9B59B6)
    }

    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(badgeColor)
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
    }
}

// ─── Preview ────────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    NotificationScreen()
}