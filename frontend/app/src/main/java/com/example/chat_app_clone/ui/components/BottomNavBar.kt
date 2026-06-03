package com.example.chat_app_clone.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_app_clone.ui.theme.MessengerBlue

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun MessengerBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Chats", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, badgeCount = 0),
        BottomNavItem("People", Icons.Filled.People, Icons.Outlined.PeopleOutline, badgeCount = 0),
        BottomNavItem("Notifications", Icons.Filled.Notifications, Icons.Outlined.Notifications, badgeCount = 0),
        BottomNavItem("Setting", Icons.Filled.Settings, Icons.Outlined.Settings, badgeCount = 0),

        )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.height(64.dp)
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            val tint by animateColorAsState(
                targetValue = if (isSelected) MessengerBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "navTint"
            )
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge { Text(item.badgeCount.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                label = null,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}
