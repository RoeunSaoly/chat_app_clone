package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_app_clone.ui.components.UserAvatar
import com.example.chat_app_clone.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    onBack: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    var isEditing by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf("") }

    LaunchedEffect(user?.id) {
        user?.let {
            username = it.username
            avatar = it.avatar.orEmpty()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isEditing) viewModel.updateProfile(username, avatar.takeIf { it.isNotBlank() })
                            isEditing = !isEditing
                        },
                        enabled = !uiState.isSaving
                    ) {
                        Icon(
                            if (isEditing) Icons.Default.Check else Icons.Outlined.Edit,
                            contentDescription = if (isEditing) "Save profile" else "Edit profile"
                        )
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    UserAvatar(name = username.ifBlank { "User" }, size = 112)
                    Spacer(modifier = Modifier.height(20.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = avatar,
                            onValueChange = { avatar = it },
                            label = { Text("Avatar URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = user?.displayName ?: "Loading...",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = user?.email.orEmpty(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onMessageClick, modifier = Modifier.fillMaxWidth()) {
                        Text("Message")
                    }
                }
            }

            if (uiState.isLoading || uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error?.let {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(onClick = viewModel::clearError) { Text("Dismiss") }
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
fun ProfileScreenPreview() {
    ProfileScreen(userId = "1")
}
