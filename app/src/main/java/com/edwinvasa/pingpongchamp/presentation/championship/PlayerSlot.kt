package com.edwinvasa.pingpongchamp.presentation.championship

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayerSlot(name: String, isWinner: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isWinner) Color(0xFFB8E986) else Color(0xFFE0E0E0))
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
