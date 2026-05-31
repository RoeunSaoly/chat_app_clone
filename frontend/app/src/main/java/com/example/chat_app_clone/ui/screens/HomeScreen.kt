package com.example.chat_app_clone.ui.Screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.chat_app_clone.data.SampleData
import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.ConversationMember
import com.example.chat_app_clone.ui.components.*
import com.example.chat_app_clone.ui.theme.MessengerBlue
import com.example.chat_app_clone.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onConversationClick: (Conversation) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCreateGroupClick: () -> Unit = {},
    onCallsTabClick: () -> Unit = {},
    onSettingTabClick: () -> Unit = {},
    onPeopleTabClick: () -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val conversations = uiState.conversations
    val isLoading = uiState.isLoading
    val error = uiState.error
    val currentUserId = com.example.chat_app_clone.MainActivity.getCurrentUserId(
        androidx.compose.ui.platform.LocalContext.current
    ).toLongOrNull() ?: 0L

    var selectedTab by remember { mutableIntStateOf(0) }

    // Refresh on first composition
    LaunchedEffect(Unit) {
        if (conversations.isEmpty()) {
            viewModel.loadConversations()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chats",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (MaterialTheme.colorScheme.surface == Color.White) Color(
                                    0xFFF0F2F5
                                ) else Color(0xFF3E4042)
                            )
                            .clickable { onSettingTabClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Settings",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (MaterialTheme.colorScheme.surface == Color.White) Color(
                                    0xFFF0F2F5
                                ) else Color(0xFF3E4042)
                            )
                            .clickable { onCreateGroupClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "New chat",
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            MessengerBottomNavBar(
                selectedIndex = selectedTab,
                onItemSelected = { index ->
                    selectedTab = index
                    when (index) {
                        1 -> onPeopleTabClick()
                        2 -> onSettingTabClick()
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
            if (isLoading && conversations.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search bar
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (MaterialTheme.colorScheme.surface == Color.White) Color(
                                    0xFFF0F2F5
                                ) else Color(0xFF3E4042)
                            )
                            .clickable(onClick = onSearchClick)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Search",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Pinned / Chats header
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Chats",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Requests",
                            fontSize = 14.sp,
                            color = MessengerBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onPeopleTabClick() }
                        )
                    }
                }

                // Friends row
                if (uiState.friends.isNotEmpty()) {
                    items(uiState.friends) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onConversationClick(
                                        Conversation(
                                            id = 0,
                                            type = "private",
                                            name = friend.displayName,
                                            members = emptyList(),
                                            otherUser = ConversationMember(
                                                userId = friend.id,
                                                username = friend.username,
                                                avatar = friend.avatar,
                                                isOnline = friend.isOnline,
                                                lastSeen = friend.lastSeen
                                            ),
                                            lastMessage = null,
                                            updatedAt = null,
                                            unreadCount = 0
                                        )
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar with online dot
                            Box(modifier = Modifier.size(56.dp)) {
                                UserAvatar(
                                    name = friend.displayName,
                                    size = 56,
                                    isOnline = false
                                )
                                if (friend.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(Color(0xFF44C553))
                                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Name + "Say hi!" or last seen
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = friend.displayName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (friend.isOnline) "Active now" else "Say hi!",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Error snackbar
            error?.let {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
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


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
