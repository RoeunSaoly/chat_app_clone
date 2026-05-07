package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.chat_app_clone.ui.components.MessageBubble
import com.example.chat_app_clone.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    userId: String,
    currentUserId: Long,
    onBack: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(currentUserId)
    )

    val uiState by viewModel.uiState.collectAsState()
    val messages = uiState.messages
    val isLoading = uiState.isLoading
    val error = uiState.error
    val typingUsers = uiState.typingUsers

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Set conversation when screen opens
    LaunchedEffect(conversationId) {
        conversationId.toLongOrNull()?.let { viewModel.openConversation(it) }
    }

    // Scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Beautiful Pastel Gradient Background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFDE8ED),
            Color(0xFFE2C4D3)
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Chat",
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            if (typingUsers.isNotEmpty()) {
                                Text(
                                    text = typingUsers.joinToString(", ") + " is typing...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onProfileClick) {
                            Icon(
                                Icons.Default.MoreHoriz,
                                contentDescription = "More",
                                tint = Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                ChatInputBar(
                    text = inputText,
                    onTextChange = {
                        inputText = it
                        viewModel.onTyping()
                    },
                    onSend = {
                        if (inputText.isNotEmpty()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                            viewModel.stopTyping()
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
                if (isLoading && messages.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        val isOwn = message.senderId == currentUserId
                        MessageBubble(
                            message = message,
                            isOwn = isOwn,
                            showAvatar = !isOwn,
                            senderName = if (isOwn) "You" else "User"
                        )
                    }

                    if (typingUsers.isNotEmpty()) {
                        item {
                            TypingIndicator(typingUsers = typingUsers)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // Error snackbar
                error?.let {
                    Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                        action = {
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Dismiss")
                            }
                        }
                    ) {
                        Text(it)
                    }
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator(typingUsers: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = typingUsers.joinToString(", ") + " is typing...",
            fontSize = 13.sp,
            color = Color.Gray,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Attach",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (text.isEmpty()) {
                Text("Type a message here...", color = Color.Gray.copy(alpha = 0.7f), fontSize = 15.sp)
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 15.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { if (text.isNotEmpty()) onSend() }
        ) {
            Icon(
                imageVector = if (text.isNotEmpty()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                contentDescription = if (text.isNotEmpty()) "Send" else "Voice",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

class ChatViewModelFactory(private val currentUserId: Long) :
    androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(currentUserId = currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(conversationId = "1", userId = "2", currentUserId = 1L)
}
