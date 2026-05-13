package com.example.chat_app_clone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.ui.theme.MessengerGradientEnd
import com.example.chat_app_clone.ui.theme.MessengerGradientStart

val avatarGradients = listOf(
    listOf(Color(0xFF0084FF), Color(0xFFA033FF)),
    listOf(Color(0xFFFF6B6B), Color(0xFFFFD93D)),
    listOf(Color(0xFF6BCB77), Color(0xFF4D96FF)),
    listOf(Color(0xFFFF922B), Color(0xFFFF6B6B)),
    listOf(Color(0xFF845EF7), Color(0xFFDA77F2)),
    listOf(Color(0xFF20C997), Color(0xFF339AF0)),
)

@Composable
fun UserAvatar(
    name: String,
    size: Int = 48,
    isOnline: Boolean = false,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    val gradientIndex = (name.hashCode().absoluteValue) % avatarGradients.size
    val gradient = avatarGradients[gradientIndex]

    Box(modifier = modifier.size(size.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size * 0.35).sp
            )
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size((size * 0.3).dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.White, CircleShape)
                    .padding(2.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
            )
        }
    }
}

private val Int.absoluteValue: Int get() = if (this < 0) -this else this
