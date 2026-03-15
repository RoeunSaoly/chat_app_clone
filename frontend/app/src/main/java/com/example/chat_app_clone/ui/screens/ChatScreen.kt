package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.data.SampleData
import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.ui.components.MessageBubble
import com.example.chat_app_clone.ui.components.UserAvatar
import com.example.chat_app_clone.ui.theme.MessengerBlue
import com.example.chat_app_clone.ui.theme.MessengerGradientEnd
import com.example.chat_app_clone.ui.theme.MessengerGradientStart
import com.example.chat_app_clone.ui.theme.OnlineGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    userId: String,
    onBack: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val conversation = SampleData.conversations.find { it.id == conversationId }
    val user = SampleData.users.find { it.id == userId } ?: SampleData.users.first()
    val messages = remember { SampleData.getMessagesForConversation(conversationId) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onProfileClick)
                    ) {
                        Box {
                            UserAvatar(name = user.name, size = 40)
                            if (user.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = user.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (user.isOnline) "Active now" else "Active ${user.lastSeen}",
                                fontSize = 12.sp,
                                color = if (user.isOnline) OnlineGreen
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MessengerBlue
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Voice call",
                            tint = MessengerBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = "Video call",
                            tint = MessengerBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MessengerBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = { inputText = "" }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(messages) { message ->
                val isOwn = message.senderId == "me"
                val showAvatar = !isOwn
                MessageBubble(
                    message = message,
                    isOwn = isOwn,
                    showAvatar = showAvatar,
                    senderName = user.name
                )
            }
            item { Spacer(modifier = Modifier.height(4.dp)) }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment icons
            IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Attach",
                    tint = MessengerBlue, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera",
                    tint = MessengerBlue, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Mic, contentDescription = "Voice",
                    tint = MessengerBlue, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Image, contentDescription = "Gallery",
                    tint = MessengerBlue, modifier = Modifier.size(22.dp))
            }

            // Text input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text("Aa", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp)
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Send button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (text.isNotEmpty()) MessengerBlue
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { if (text.isNotEmpty()) onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (text.isNotEmpty()) Icons.Default.Send else Icons.Default.ThumbUp,
                    contentDescription = if (text.isNotEmpty()) "Send" else "Like",
                    tint = if (text.isNotEmpty()) Color.White else MessengerBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
