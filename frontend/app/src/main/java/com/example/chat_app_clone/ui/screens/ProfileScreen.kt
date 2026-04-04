package com.example.chat_app_clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.data.SampleData
import com.example.chat_app_clone.ui.components.ProfileActionButton
import com.example.chat_app_clone.ui.components.ProfileEditableSettingsRow
import com.example.chat_app_clone.ui.components.ProfileSectionHeader
import com.example.chat_app_clone.ui.components.ProfileSettingsRow
import com.example.chat_app_clone.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    onBack: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    val user = SampleData.users.find { it.id == userId } ?: SampleData.users.first()
    
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(user.name) }
    var editedPhone by remember { mutableStateOf("+1 (555) 000-0000") }
    var editedUsername by remember { mutableStateOf("@${user.username}") }
    var editedBio by remember { mutableStateOf("Avid coffee drinker and code writer. Let's chat!") }
    
    // Beautiful Pastel Gradient Background for the top header
    val headerBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFDE8ED), // Top lighter pink
            Color(0xFFE2C4D3)  // Bottom deeper pastel
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // -- GORGEOUS GRADIENT HEADER --
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(headerBrush)
                ) {
                    // Top Bar inside the header
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                            }
                        },
                        actions = {
                            IconButton(onClick = { isEditing = !isEditing }) {
                                if (isEditing) {
                                    Icon(Icons.Default.Check, contentDescription = "Save Profile", tint = Color.Black)
                                } else {
                                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Profile", tint = Color.Black)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )

                    // Avatar and Name centered
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 56.dp), // below top bar
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(110.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                UserAvatar(name = user.name, size = 106)
                            }
                            if (user.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(x = (-4).dp, y = (-4).dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .padding(3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color(0xFF4CAF50))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isEditing) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.W600,
                                    color = Color.Black,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(vertical = 4.dp),
                                cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.Black)
                            )
                        } else {
                            Text(
                                text = editedName,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.W600,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "My Profile \uD83C\uDF38",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // -- CLEAN BODY CARD --
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp) // Overlap the header slightly
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color.White)
                        .padding(bottom = 50.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        // -- ACTIONS --
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ProfileActionButton(
                                icon = if (isEditing) Icons.Default.Check else Icons.Outlined.Edit, 
                                label = if (isEditing) "Save" else "Edit", 
                                onClick = { isEditing = !isEditing }
                            )
                            ProfileActionButton(icon = Icons.Outlined.Share, label = "Share", onClick = {})
                            ProfileActionButton(icon = Icons.Outlined.QrCode, label = "QR Code", onClick = {})
                            ProfileActionButton(icon = Icons.Outlined.BookmarkBorder, label = "Saved", onClick = {})
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // -- INFORMATION --
                        ProfileSectionHeader("Information")
                        if (isEditing) {
                            ProfileEditableSettingsRow(
                                icon = Icons.Outlined.Phone,
                                title = "Mobile",
                                value = editedPhone,
                                onValueChange = { editedPhone = it }
                            )
                            ProfileEditableSettingsRow(
                                icon = Icons.Outlined.AlternateEmail,
                                title = "Username",
                                value = editedUsername,
                                onValueChange = { editedUsername = it }
                            )
                            ProfileEditableSettingsRow(
                                icon = Icons.Outlined.FormatQuote,
                                title = "Bio",
                                value = editedBio,
                                onValueChange = { editedBio = it },
                                showDivider = false
                            )
                        } else {
                            ProfileSettingsRow(
                                icon = Icons.Outlined.Phone,
                                title = editedPhone,
                                subtitle = "Mobile"
                            )
                            ProfileSettingsRow(
                                icon = Icons.Outlined.AlternateEmail,
                                title = editedUsername,
                                subtitle = "Username"
                            )
                            ProfileSettingsRow(
                                icon = Icons.Outlined.FormatQuote,
                                title = editedBio,
                                subtitle = "Bio",
                                showDivider = false
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // -- ACCOUNT SETTINGS --
                        ProfileSectionHeader("Settings")
                        ProfileSettingsRow(
                            icon = Icons.Outlined.Language,
                            title = "Language",
                            subtitle = "English (US)"
                        )
                        ProfileSettingsRow(
                            icon = Icons.Outlined.Security,
                            title = "Privacy and Security"
                        )
                        ProfileSettingsRow(
                            icon = Icons.Outlined.Notifications,
                            title = "Notifications",
                            subtitle = "Enabled",
                            showToggle = true,
                            toggleState = true
                        )
                        ProfileSettingsRow(
                            icon = Icons.Outlined.Storage,
                            title = "Data and Storage",
                            showDivider = false
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))

                        // -- LOG OUT --
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            OutlinedButton(
                                onClick = { /* Handle action */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Logout, contentDescription = "Log Out", tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Log Out", color = MaterialTheme.colorScheme.error, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
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

