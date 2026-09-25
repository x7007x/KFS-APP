package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ServiceIcon(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val icon = iconMap[name] ?: Icons.Default.Circle
    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = modifier)
}

private val iconMap: Map<String, ImageVector> = mapOf(
    "clipboard_results" to Icons.AutoMirrored.Filled.Assignment,
    "edit_note" to Icons.Default.EditNote,
    "globe" to Icons.Default.Language,
    "medical_services" to Icons.Default.LocalHospital,
    "wallet" to Icons.Default.AccountBalanceWallet,
    "envelope" to Icons.Default.Email,
    "description" to Icons.Default.Description,
    "graduation_cap" to Icons.Default.School,
    "military" to Icons.Default.Shield,
    "book" to Icons.Default.Book,
    "home" to Icons.Default.Home,
    "group" to Icons.Default.Groups,
    "search" to Icons.Default.Search,
    "calendar_today" to Icons.Default.CalendarMonth,
    "badge" to Icons.Default.Badge,
    "building" to Icons.Default.Business,
    "diamond" to Icons.Default.Diamond,
    "eye" to Icons.Default.Visibility,
    "newspaper" to Icons.AutoMirrored.Filled.List,
    "teacher" to Icons.Default.Person,
    "more" to Icons.Default.Apps,
    "colleges" to Icons.Default.AccountBox,
)
