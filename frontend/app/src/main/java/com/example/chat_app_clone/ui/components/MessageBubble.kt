package com.example.chat_app_clone.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.data.model.MessageStatus
import com.example.chat_app_clone.ui.theme.MessengerGradientEnd
import com.example.chat_app_clone.ui.theme.MessengerGradientStart

@Composable
fun MessageBubble(
    message: Message,
    isOwn: Boolean,
    showAvatar: Boolean = false,
    senderName: String = ""
) {
    val sentGradient = Brush.linearGradient(
        colors = listOf(MessengerGradientStart, MessengerGradientEnd)
    )
    val receivedColor = MaterialTheme.colorScheme.surfaceVariant

    val bubbleShape = if (isOwn) {
        RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
    } else {
        RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isOwn) 64.dp else 8.dp,
                end = if (isOwn) 8.dp else 64.dp,
                top = 2.dp,
                bottom = 2.dp
            ),
        horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
        ) {
            // Avatar for received messages
            if (!isOwn) {
                if (showAvatar) {
                    UserAvatar(name = senderName, size = 32)
                } else {
                    Spacer(modifier = Modifier.width(32.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            Column(horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start) {
                Box(
                    modifier = Modifier
                        .clip(bubbleShape)
                        .then(
                            if (isOwn) Modifier.background(sentGradient)
                            else Modifier.background(receivedColor)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message.content,
                        color = if (isOwn) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                }

                // Timestamp + read receipt
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                ) {
                    Text(
                        text = message.timestamp,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isOwn) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (message.status == MessageStatus.READ)
                                Icons.Default.DoneAll else Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (message.status == MessageStatus.READ)
                                MessengerGradientStart else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
