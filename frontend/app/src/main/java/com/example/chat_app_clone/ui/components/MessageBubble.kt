package com.example.chat_app_clone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.ui.theme.MessengerBlue

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    isOwn: Boolean,
    showAvatar: Boolean = false,
    senderName: String = "",
    onLongClick: () -> Unit = {}
) {
    val bubbleColor = if (isOwn) {
        MessengerBlue
    } else {
        if (MaterialTheme.colorScheme.surface == Color.White) Color(0xFFE4E6EB) else Color(0xFF3E4042)
    }

    val contentColor = if (isOwn) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val bubbleShape = if (isOwn) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = {}
            ),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isOwn) {
            if (showAvatar) {
                UserAvatar(name = senderName, size = 28)
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Spacer(modifier = Modifier.width(36.dp))
            }
        }

        Column(
            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .widthIn(max = 260.dp)
            ) {
                Text(
                    text = if (message.deletedForEveryone) "Message deleted" else message.content,
                    color = if (message.deletedForEveryone) contentColor.copy(alpha = 0.6f) else contentColor,
                    fontSize = 16.sp,
                    fontStyle = if (message.deletedForEveryone) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                    lineHeight = 20.sp
                )
            }
            
            // Only show status icon for own messages if it's the last one (simplification)
            if (isOwn && message.status == "seen") {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp).padding(top = 2.dp),
                    tint = MessengerBlue
                )
            }
        }
    }
}

private fun formatMessageTime(value: String): String {
    if (value == "Sending...") return value
    return value.substringAfter("T", value).take(5).ifBlank { value }
}
