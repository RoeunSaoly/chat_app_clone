package com.example.chat_app_clone.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

data class BottomMenuItem(
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomMenu(selectedIndex: Int, onItemSelected: (Int) -> Unit) {

    val items = listOf(
        BottomMenuItem("Chats", Icons.Default.Chat),
        BottomMenuItem("Contacts", Icons.Default.Person),
        BottomMenuItem("Settings", Icons.Default.Settings)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                },
                label = {
                    Text(item.label)
                }
            )
        }
    }
}