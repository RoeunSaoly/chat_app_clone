package com.example.chat_app_clone.ui.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_app_clone.viewmodel.SettingViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.ui.components.MessengerBottomNavBar
import com.example.chat_app_clone.ui.components.UserAvatar
import com.example.chat_app_clone.ui.theme.MessengerBlue

// ---------------------------------------------------------------------------
// Data model for a settings row
// ---------------------------------------------------------------------------

private data class SettingItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val showChevron: Boolean = true,
    val badgeCount: Int = 0,
    val isToggle: Boolean = false,
    val toggleDefault: Boolean = false,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit = {}
)

// ---------------------------------------------------------------------------
// SettingsScreen
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBack: () -> Unit = {},
    onPeopleTabClick: () -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    // Account
    onPersonalInfoClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {},
    // Preferences
    onNotificationsClick: () -> Unit = {},
    onAppearanceClick: () -> Unit = {},
    onMediaStorageClick: () -> Unit = {},
    // Support
    onHelpCenterClick: () -> Unit = {},
    onTermsClick: () -> Unit = {}
) {
    val viewModel: SettingViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    val isLightTheme = MaterialTheme.colorScheme.surface == Color.White
    val chipBg = if (isLightTheme) Color(0xFFF0F2F5) else Color(0xFF3E4042)

    var showLogoutDialog by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(true) }
    var messagePreviewEnabled by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(2) } // Settings tab active

    // ---------------------------------------------------------------------------
    // Logout confirmation dialog
    // ---------------------------------------------------------------------------
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text("Log out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---------------------------------------------------------------------------
    // Setting row definitions
    // ---------------------------------------------------------------------------
    val accountItems = listOf(
        SettingItem(
            title = "Personal information",
            subtitle = user?.displayName ?: "Name, username, phone",
            icon = Icons.Default.Person,
            iconBg = Color(0xFFE6F1FB),
            iconTint = MessengerBlue,
            onClick = onPersonalInfoClick
        ),
        SettingItem(
            title = "Privacy",
            subtitle = "Active status, blocked people",
            icon = Icons.Default.Lock,
            iconBg = Color(0xFFEAF3DE),
            iconTint = Color(0xFF3B6D11),
            onClick = onPrivacyClick
        ),
        SettingItem(
            title = "Security",
            subtitle = "Two-factor authentication",
            icon = Icons.Default.Shield,
            iconBg = Color(0xFFFAEEDA),
            iconTint = Color(0xFF854F0B),
            onClick = onSecurityClick
        )
    )

    val preferenceItems = listOf(
        SettingItem(
            title = "Notifications",
            subtitle = "Sounds, badges, alerts",
            icon = Icons.Default.Notifications,
            iconBg = Color(0xFFEEEDFE),
            iconTint = Color(0xFF534AB7),
            badgeCount = 3,
            onClick = onNotificationsClick
        ),
        SettingItem(
            title = "Dark mode",
            subtitle = "Follow system theme",
            icon = Icons.Default.DarkMode,
            iconBg = Color(0xFFE1F5EE),
            iconTint = Color(0xFF0F6E56),
            showChevron = false,
            isToggle = true,
            toggleDefault = true
        ),
        SettingItem(
            title = "Message previews",
            subtitle = "Show in notifications",
            icon = Icons.Default.Message,
            iconBg = Color(0xFFE1F5EE),
            iconTint = Color(0xFF0F6E56),
            showChevron = false,
            isToggle = true,
            toggleDefault = true
        ),
        SettingItem(
            title = "Appearance",
            subtitle = "Chat themes, bubble colors",
            icon = Icons.Default.Palette,
            iconBg = Color(0xFFFAECE7),
            iconTint = Color(0xFF993C1D),
            onClick = onAppearanceClick
        ),
        SettingItem(
            title = "Media & storage",
            subtitle = "Auto-download, storage used",
            icon = Icons.Default.Storage,
            iconBg = chipBg,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = onMediaStorageClick
        )
    )

    val supportItems = listOf(
        SettingItem(
            title = "Help center",
            icon = Icons.Default.Help,
            iconBg = Color(0xFFE6F1FB),
            iconTint = MessengerBlue,
            onClick = onHelpCenterClick
        ),
        SettingItem(
            title = "Terms & privacy policy",
            icon = Icons.Default.Description,
            iconBg = chipBg,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = onTermsClick
        ),
        SettingItem(
            title = "Log out",
            icon = Icons.AutoMirrored.Filled.Logout,
            iconBg = Color(0xFFFCEBEB),
            iconTint = MaterialTheme.colorScheme.error,
            showChevron = false,
            isDestructive = true,
            onClick = { showLogoutDialog = true }
        )
    )

    // ---------------------------------------------------------------------------
    // Scaffold
    // ---------------------------------------------------------------------------
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
                onItemSelected = { index ->
                    selectedTab = index
                    when (index) {
                        0 -> onHomeTabClick()
                        1 -> onPeopleTabClick()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // ------------------------------------------------------------------
            // Profile section
            // ------------------------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with online dot
                    Box(modifier = Modifier.size(80.dp)) {
                        UserAvatar(
                            name = user?.displayName ?: "User",
                            size = 80,
                            isOnline = false
                        )
                        // Online dot
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF44C553))
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = user?.displayName ?: "Loading...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick actions row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        ProfileActionButton(
                            icon = Icons.Default.Edit,
                            label = "Edit profile",
                            chipBg = chipBg,
                            onClick = onEditProfileClick
                        )
                        ProfileActionButton(
                            icon = Icons.Default.Link,
                            label = "Share link",
                            chipBg = chipBg,
                            onClick = {}
                        )
                        ProfileActionButton(
                            icon = Icons.Default.QrCode,
                            label = "QR code",
                            chipBg = chipBg,
                            onClick = {}
                        )
                    }
                }
            }

            // ------------------------------------------------------------------
            // Account section
            // ------------------------------------------------------------------
            item { SectionDivider() }
            item { SectionLabel(title = "Account") }
            item {
                SettingsGroup(
                    items = accountItems,
                    darkModeEnabled = darkModeEnabled,
                    messagePreviewEnabled = messagePreviewEnabled,
                    onDarkModeToggle = { darkModeEnabled = it },
                    onMessagePreviewToggle = { messagePreviewEnabled = it }
                )
            }

            // ------------------------------------------------------------------
            // Preferences section
            // ------------------------------------------------------------------
            item { SectionDivider() }
            item { SectionLabel(title = "Preferences") }
            item {
                SettingsGroup(
                    items = preferenceItems,
                    darkModeEnabled = darkModeEnabled,
                    messagePreviewEnabled = messagePreviewEnabled,
                    onDarkModeToggle = { darkModeEnabled = it },
                    onMessagePreviewToggle = { messagePreviewEnabled = it }
                )
            }

            // ------------------------------------------------------------------
            // Support section
            // ------------------------------------------------------------------
            item { SectionDivider() }
            item { SectionLabel(title = "Support") }
            item {
                SettingsGroup(
                    items = supportItems,
                    darkModeEnabled = darkModeEnabled,
                    messagePreviewEnabled = messagePreviewEnabled,
                    onDarkModeToggle = { darkModeEnabled = it },
                    onMessagePreviewToggle = { messagePreviewEnabled = it }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-composables
// ---------------------------------------------------------------------------

@Composable
private fun ProfileActionButton(
    icon: ImageVector,
    label: String,
    chipBg: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(chipBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = MessengerBlue
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun SettingsGroup(
    items: List<SettingItem>,
    darkModeEnabled: Boolean,
    messagePreviewEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onMessagePreviewToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items.forEachIndexed { index, item ->
            SettingRow(
                item = item,
                darkModeEnabled = darkModeEnabled,
                messagePreviewEnabled = messagePreviewEnabled,
                onDarkModeToggle = onDarkModeToggle,
                onMessagePreviewToggle = onMessagePreviewToggle
            )
            if (index < items.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 70.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    item: SettingItem,
    darkModeEnabled: Boolean,
    messagePreviewEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onMessagePreviewToggle: (Boolean) -> Unit
) {
    // Determine the toggle state for items that are toggles
    val toggleState = when (item.title) {
        "Dark mode" -> darkModeEnabled
        "Message previews" -> messagePreviewEnabled
        else -> item.toggleDefault
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !item.isToggle) { item.onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(item.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (item.isDestructive) MaterialTheme.colorScheme.error else item.iconTint
            )
        }

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = if (item.isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
            )
            if (item.subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Trailing: badge + chevron, or toggle
        when {
            item.isToggle -> {
                Switch(
                    checked = toggleState,
                    onCheckedChange = {
                        when (item.title) {
                            "Dark mode" -> onDarkModeToggle(it)
                            "Message previews" -> onMessagePreviewToggle(it)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MessengerBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.4f)
                    )
                )
            }
            else -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (item.badgeCount > 0) {
                        Badge(
                            containerColor = MessengerBlue
                        ) {
                            Text(
                                text = item.badgeCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                    if (item.showChevron) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Gray,
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingScreen()
}