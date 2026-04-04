package com.example.chat_app_clone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
    )
}

@Composable
fun ProfileActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFFDE8ED).copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProfileSettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    showDivider: Boolean = true,
    showToggle: Boolean = false,
    toggleState: Boolean = false,
    iconTint: Color = Color.Black
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF9F9F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
            
            if (showToggle) {
                Switch(
                    checked = toggleState,
                    onCheckedChange = null,
                    modifier = Modifier.padding(start = 8.dp),
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE2C4D3), checkedTrackColor = Color(0xFFFDE8ED))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Navigate",
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                color = Color.LightGray.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun ProfileEditableSettingsRow(
    icon: ImageVector,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF9F9F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
        }
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                color = Color.LightGray.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )
        }
    }
}
