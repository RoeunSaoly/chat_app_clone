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
    onClick: () -> Unit
) {
    val title = conversation.displayName(currentUserId)
    val hasUnread = conversation.unreadCount > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online indicator
        Box(modifier = Modifier.size(56.dp)) {
            UserAvatar(name = title, size = 56)

            if (conversation.isOtherUserOnline(currentUserId)) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(OnlineGreen)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and message preview
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = conversation.lastMessage ?: "No messages yet",
                fontSize = 14.sp,
                fontWeight = if (hasUnread) FontWeight.SemiBold else FontWeight.Normal,
                color = if (hasUnread) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Time and unread badge
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = conversation.updatedAt?.let { formatChatTime(it) }.orEmpty(),
                fontSize = 12.sp,
                color = if (hasUnread) MessengerBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (hasUnread) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MessengerBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (conversation.unreadCount > 9) "9+" else conversation.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(20.dp))
            }
        }
    }
}

private fun formatChatTime(value: String): String {
    return value.substringAfter("T", value).take(5).ifBlank { value }
}
