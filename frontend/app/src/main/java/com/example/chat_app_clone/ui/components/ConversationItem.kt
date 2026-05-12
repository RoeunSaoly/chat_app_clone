package com.example.chat_app_clone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.ui.theme.MessengerBlue
import com.example.chat_app_clone.ui.theme.OnlineGreen

@Composable
fun ConversationItem(
    conversation: Conversation,
    currentUserId: Long,
    isTyping: Boolean = false,
    onClick: () -> Unit
) {
    val title = conversation.displayName(currentUserId)
    val hasUnread = conversation.unreadCount > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online indicator
        UserAvatar(
            name = title,
            size = 60,
            isOnline = conversation.isOtherUserOnline(currentUserId)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Name and message preview
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isTyping) {
                    Text(
                        text = "Typing...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MessengerBlue,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = conversation.lastMessage ?: "No messages yet",
                        fontSize = 14.sp,
                        fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Normal,
                        color = if (hasUnread) MaterialTheme.colorScheme.onSurface
                        else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = " · ${conversation.updatedAt?.let { formatChatTime(it) }.orEmpty()}",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
        }

        if (hasUnread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MessengerBlue)
            )
        }
    }
}

private fun formatChatTime(value: String): String {
    // Basic smart formatting
    if (value.isBlank()) return ""
    try {
        val timePart = value.substringAfter("T", "").take(5)
        if (timePart.isNotBlank()) return timePart
    } catch (e: Exception) {}
    return value.take(5)
}
