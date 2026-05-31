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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.ui.components.MessageBubble
import com.example.chat_app_clone.ui.components.UserAvatar
import com.example.chat_app_clone.ui.theme.MessengerBlue
import com.example.chat_app_clone.ui.theme.OnlineGreen
import com.example.chat_app_clone.viewmodel.ChatViewModel
import com.example.chat_app_clone.viewmodel.ChatViewModelFactory

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
    val conversation = uiState.conversation
    val displayName = conversation?.displayName(currentUserId) ?: "Chat"
    val displayAvatar = conversation?.displayAvatar(currentUserId)
    val isOtherUserOnline = conversation?.isOtherUserOnline(currentUserId) ?: false

    var inputText by remember { mutableStateOf("") }
    var editingMessage by remember { mutableStateOf<Message?>(null) }
    val listState = rememberLazyListState()

    // Set conversation when screen opens
    LaunchedEffect(conversationId) {
        val convId = conversationId.toLongOrNull() ?: return@LaunchedEffect
        val userIdParam = userId.toLongOrNull()
        viewModel.openConversation(convId, userIdParam)
    }

    // Scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserAvatar(name = displayName, size = 36)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when {
                                        typingUsers.isNotEmpty() -> "is typing..."
                                        isOtherUserOnline -> "Active now"
                                        else -> "Active"
                                    },
                                    fontSize = 12.sp,
                                    color = if (typingUsers.isNotEmpty()) OnlineGreen else Color.Gray
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MessengerBlue
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
                    isEditing = editingMessage != null,
                    onTextChange = {
                        inputText = it
                        viewModel.onTyping()
                    },
                    onSend = {
                        if (inputText.isNotEmpty()) {
                            val editing = editingMessage
                            if (editing != null) {
                                viewModel.editMessage(editing.id, inputText)
                                editingMessage = null
                            } else {
                                viewModel.sendMessage(inputText)
                            }
                            inputText = ""
                            viewModel.stopTyping()
                        }
                    },
                    onCancelEdit = {
                        editingMessage = null
                        inputText = ""
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
                        var showMenu by remember { mutableStateOf(false) }
                        var showDeleteDialog by remember { mutableStateOf(false) }

                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text("Delete message?") },
                                text = { Text("Do you want to delete this message for everyone or just for you?") },
                                confirmButton = {
                                    if (isOwn) {
                                        TextButton(onClick = {
                                            viewModel.deleteMessage(message.id, true)
                                            showDeleteDialog = false
                                        }) { Text("Delete for everyone", color = Color.Red) }
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        viewModel.deleteMessage(message.id, false)
                                        showDeleteDialog = false
                                    }) { Text("Delete for me") }
                                }
                            )
                        }

                        Box {
                            MessageBubble(
                                message = message,
                                isOwn = isOwn,
                                showAvatar = !isOwn,
                                senderName = message.senderUsername ?: "Unknown",
                                onLongClick = { if (isOwn && !message.deletedForEveryone && !message.deletedForMe) showMenu = true }
                            )

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        showMenu = false
                                        editingMessage = message
                                        inputText = message.content
                                    },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete", color = Color.Red) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                                )
                            }
                        }
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
    isEditing: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onCancelEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        if (isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MessengerBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Editing message",
                    fontSize = 12.sp,
                    color = MessengerBlue,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onCancelEdit,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cancel edit",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (MaterialTheme.colorScheme.surface == Color.White) Color(0xFFF0F2F5) else Color(0xFF3E4042))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text("Aa", color = Color.Gray, fontSize = 16.sp)
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(
                onClick = { if (text.isNotEmpty()) onSend() }
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.AutoMirrored.Filled.Send,
                    contentDescription = if (isEditing) "Save" else "Send",
                    tint = MessengerBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(conversationId = "1", userId = "2", currentUserId = 1L)
}
