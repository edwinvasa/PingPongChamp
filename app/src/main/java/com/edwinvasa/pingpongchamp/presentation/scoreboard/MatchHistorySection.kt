package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MatchHistorySection(
    matchHistory: List<MatchResult>,
    redName: String,
    greenName: String,
    showHistory: Boolean,
    onToggleHistory: () -> Unit,
    onClearHistory: () -> Unit
) {
    if (matchHistory.isNotEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Historial de partidos:", fontSize = 18.sp, modifier = Modifier.weight(1f))
            IconButton(onClick = onToggleHistory) {
                Text(if (showHistory) "👁️" else "🚫")
            }
            IconButton(onClick = onClearHistory) {
                Text("🗑️")
            }
        }
    }

    if (showHistory && matchHistory.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        matchHistory.forEachIndexed { index, match ->
            Text(
                text = buildString {
                    append("${index + 1}. ")
                    if (match.redPoints > match.greenPoints) {
                        append("🏆$redName ${match.redPoints} - ${match.greenPoints} $greenName")
                    } else {
                        append("$redName ${match.redPoints} - ${match.greenPoints} $greenName🏆")
                    }
                },
                fontSize = 14.sp
            )
        }
    }
}