package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.ui.components.MessengerBottomNavBar
import com.example.chat_app_clone.ui.components.UserAvatar
import com.example.chat_app_clone.viewmodel.PeopleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    onBack: () -> Unit = {},
    onChatClick: (Long) -> Unit = {},
    onProfileClick: (Long) -> Unit = {},
    onNavigateToTab: (Int) -> Unit = {}
) {
    val viewModel: PeopleViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("People", fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            MessengerBottomNavBar(
                selectedIndex = 1,
                onItemSelected = onNavigateToTab
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isLoading && uiState.friends.isEmpty() && uiState.friendRequests.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Search bar
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (MaterialTheme.colorScheme.surface == Color.White) Color(0xFFF0F2F5) else Color(0xFF3E4042))
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


                // Friend Requests Section
                if (uiState.friendRequests.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Friend Requests")
                    }
                    items(uiState.friendRequests) { user ->
                        FriendRequestItem(
                            user = user,
                            onAccept = { viewModel.acceptFriendRequest(user.id) },
                            onReject = { viewModel.rejectFriendRequest(user.id) },
                            onProfileClick = { onProfileClick(user.id) }
                        )
                    }
                }

                // Current Friends Section
                if (uiState.friends.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Your Friends")
                    }
                    items(uiState.friends) { user ->
                        FriendItem(
                            user = user,
                            onUnfriend = { viewModel.unfriend(user.id) },
                            onChatClick = { onChatClick(user.id) },
                            onProfileClick = { onProfileClick(user.id) }
                        )
                    }
                }

                // Recommended Friends Section
                if (uiState.recommendedFriends.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Recommended Friends")
                    }
                    items(uiState.recommendedFriends) { user ->
                        RecommendedFriendItem(
                            user = user,
                            onAddFriend = { viewModel.sendFriendRequest(user.id) },
                            onProfileClick = { onProfileClick(user.id) }
                        )
                    }
                }

                if (!uiState.isLoading && uiState.friends.isEmpty() && uiState.friendRequests.isEmpty() && uiState.recommendedFriends.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No suggestions or friends found.", color = Color.Gray)
                        }
                    }
                }
            }

            // Error snackbar
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
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

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

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
        UserAvatar(name = user.displayName, size = 50, isOnline = user.isOnline)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.displayName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(text = "Sent you a friend request", fontSize = 14.sp, color = Color.Gray)
        }
        Row {
            Button(
                onClick = onAccept,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Confirm", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onReject,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Delete", fontSize = 14.sp)
            }
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
        UserAvatar(name = user.displayName, size = 50, isOnline = user.isOnline)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = user.displayName,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onChatClick) {
            Icon(Icons.Default.Chat, contentDescription = "Message", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = onUnfriend) {
            Icon(Icons.Default.PersonRemove, contentDescription = "Unfriend", tint = Color.Gray)
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
        UserAvatar(name = user.displayName, size = 50, isOnline = user.isOnline)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = user.displayName,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = onAddFriend,
            shape = CircleShape,
            modifier = Modifier.height(36.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", fontSize = 14.sp)
        }
    }
}
