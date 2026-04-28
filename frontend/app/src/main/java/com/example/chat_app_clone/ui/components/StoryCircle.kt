package com.example.chat_app_clone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.ui.theme.MessengerGradientEnd
import com.example.chat_app_clone.ui.theme.MessengerGradientStart

val storyGradient = Brush.linearGradient(
    colors = listOf(MessengerGradientStart, MessengerGradientEnd)
)

@Composable
fun StoryCircle(
    user: User,
    isOwn: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            // Gradient ring
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (!isOwn) Brush.linearGradient(
                        colors = listOf(MessengerGradientStart, MessengerGradientEnd)
                    ) else Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent)))
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp)
                ) {
                    UserAvatar(
                        name = user.displayName,
                        size = 56,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Add button for own story
            if (isOwn) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .offset(x = 2.dp, y = 2.dp)
                        .background(MessengerGradientStart, CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add story",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isOwn) "Your Story" else user.displayName.split(" ").first(),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
