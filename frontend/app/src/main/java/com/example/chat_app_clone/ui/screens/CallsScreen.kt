package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.data.SampleData
import com.example.chat_app_clone.ui.components.UserAvatar
import com.example.chat_app_clone.ui.theme.ErrorRed
import com.example.chat_app_clone.ui.theme.MessengerBlue
import com.example.chat_app_clone.ui.theme.OnlineGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsScreen(onBack: () -> Unit = {}) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calls",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search calls",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MessengerBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "New call")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(
                    "Recent",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            items(SampleData.callRecords) { call ->
                CallRecordItem(call)
            }
        }
    }
}

@Composable
private fun CallRecordItem(call: SampleData.CallRecord) {
    val isMissed = call.direction == "missed"
    val isVideo = call.callType == "video"
    val isOutgoing = call.direction == "outgoing"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(name = call.user.name, size = 52)
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.user.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isOutgoing) Icons.Default.CallMade else Icons.Default.CallReceived,
                    contentDescription = null,
                    tint = if (isMissed) ErrorRed else OnlineGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = buildString {
                        append(when (call.direction) {
                            "outgoing" -> "Outgoing"
                            "incoming" -> "Incoming"
                            else -> "Missed"
                        })
                        append(" · ")
                        append(call.timestamp)
                        if (call.duration.isNotEmpty()) append(" · ${call.duration}")
                    },
                    fontSize = 13.sp,
                    color = if (isMissed) ErrorRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(onClick = {}) {
            Icon(
                imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = if (isVideo) "Video call" else "Audio call",
                tint = MessengerBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
