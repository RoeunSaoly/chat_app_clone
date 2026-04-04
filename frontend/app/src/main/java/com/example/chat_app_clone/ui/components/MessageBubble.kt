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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.data.model.MessageStatus

@Composable
fun MessageBubble(
    message: Message,
    isOwn: Boolean,
    showAvatar: Boolean = false,
    senderName: String = ""
) {
    val bubbleColor = if (isOwn) {
        Color.White
    } else {
        Color(0xFFFDE8ED) // Light pastel pink like the background
    }

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
        if (!isOwn && showAvatar) {
            Text(
                text = senderName,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 42.dp, bottom = 2.dp)
            )
        }

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

            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleColor)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = message.content,
                        color = Color.Black,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // Inline Timestamp + read receipt
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp) // Push it down slightly relative to text
                    ) {
                        Text(
                            text = message.timestamp,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        if (isOwn) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = if (message.status == MessageStatus.READ)
                                    Icons.Default.DoneAll else Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (message.status == MessageStatus.READ)
                                    Color(0xFF4CAF50) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
