package com.example.chat_app_clone.ui.screens

import android.R.attr.isLightTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.ui.components.MessengerBottomNavBar
import com.example.chat_app_clone.ui.components.UserAvatar
import com.example.chat_app_clone.viewmodel.NotificationViewModel
import com.example.chat_app_clone.viewmodel.PeopleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    onBack: () -> Unit = {},
    onChatClick: (Long) -> Unit = {},
    onProfileClick: (Long) -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onSettingTabClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsTapsClick: () -> Unit = {}
) {
    val viewModel: PeopleViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val notificationUiState by notificationViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(1) }


    val isLightTheme = MaterialTheme.colorScheme.surface == Color.White
    val chipBg = if (isLightTheme) Color(0xFFF0F2F5) else Color(0xFF3E4042)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "People",
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
                            .background(chipBg)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            MessengerBottomNavBar(
                selectedIndex = selectedTab,
                notificationBadgeCount = notificationUiState.unreadCount,
                onItemSelected = { index ->
                    selectedTab = index
                    when (index) {
                        0 -> onHomeTabClick()
                        2 -> onNotificationsTapsClick()
                        3 -> onSettingTabClick()
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
            if (uiState.isLoading && uiState.friends.isEmpty() && uiState.friendRequests.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Search bar — matches HomeScreen exactly
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


                // Friend Requests section
                if (uiState.friendRequests.isNotEmpty()) {
                    item { PeopleSectionHeader(title = "Friend Requests") }
                    items(uiState.friendRequests) { user ->
                        FriendRequestItem(
                            user = user,
                            onAccept = { viewModel.acceptFriendRequest(user.id) },
                            onReject = { viewModel.rejectFriendRequest(user.id) },
                            onProfileClick = { onProfileClick(user.id) }
                        )
                    }
                }

                // Friends section
                if (uiState.friends.isNotEmpty()) {
                    item { PeopleSectionHeader(title = "Your Friends") }
                    items(uiState.friends) { user ->
                        FriendItem(
                            user = user,
                            onUnfriend = { viewModel.unfriend(user.id) },
                            onChatClick = { onChatClick(user.id) },
                            onProfileClick = { onProfileClick(user.id) }
                        )
                    }
                }

                // Recommended Friends section
                if (uiState.recommendedFriends.isNotEmpty()) {
                    item { PeopleSectionHeader(title = "Recommended Friends") }
                    items(uiState.recommendedFriends) { user ->
                        RecommendedFriendItem(
                            user = user,
                            onAddFriend = { viewModel.sendFriendRequest(user.id) },
                            onProfileClick = { onProfileClick(user.id) }
                        )
                    }
                }

                if (!uiState.isLoading &&
                    uiState.friends.isEmpty() &&
                    uiState.friendRequests.isEmpty() &&
                    uiState.recommendedFriends.isEmpty()
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No suggestions or friends found.", color = Color.Gray)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Error snackbar — matches HomeScreen style
            uiState.error?.let { error ->
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
                    Text(error)
                }
            }
        }
    }
}

// Section header — uses onSurface like HomeScreen's "Chats" label
@Composable
fun PeopleSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

// Matches HomeScreen friend row: 56dp avatar, online dot, same padding
@Composable
fun FriendRequestItem(
    user: User,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProfileClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp)) {
            UserAvatar(name = user.displayName, size = 56, isOnline = false)
            if (user.isOnline) {
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.displayName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Sent you a friend request",
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onAccept,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Confirm", fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        OutlinedButton(
            onClick = onReject,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Delete", fontSize = 13.sp)
        }
    }
}

@Composable
fun FriendItem(
    user: User,
    onUnfriend: () -> Unit,
    onChatClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProfileClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp)) {
            UserAvatar(name = user.displayName, size = 56, isOnline = false)
            if (user.isOnline) {
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.displayName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (user.isOnline) "Active now" else "Tap to view profile",
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
        IconButton(onClick = onChatClick) {
            Icon(
                Icons.Default.Chat,
                contentDescription = "Message",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onUnfriend) {
            Icon(
                Icons.Default.PersonRemove,
                contentDescription = "Unfriend",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun RecommendedFriendItem(
    user: User,
    onAddFriend: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProfileClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp)) {
            UserAvatar(name = user.displayName, size = 56, isOnline = false)
            if (user.isOnline) {
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.displayName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "People you may know",
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
        Button(
            onClick = onAddFriend,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Icon(
                Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PeopleScreenPreview() {
    PeopleScreen()
}