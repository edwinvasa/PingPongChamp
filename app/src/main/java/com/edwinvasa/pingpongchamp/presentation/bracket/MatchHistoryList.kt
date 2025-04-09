package com.edwinvasa.pingpongchamp.presentation.bracket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.edwinvasa.pingpongchamp.presentation.scoreboard.MatchPingPongResult

@Composable
fun MatchHistoryList(player1: String, player2: String, matchHistory: List<MatchPingPongResult>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Text("Historial", style = MaterialTheme.typography.labelMedium)

        matchHistory.forEachIndexed { index, result ->
            val displayText = if (result.winner == player1) {
                "\uD83C\uDFC6 $player1 ${result.redPoints} - ${result.greenPoints} $player2"
            } else {
                "$player1 ${result.redPoints} - ${result.greenPoints} $player2 \uD83C\uDFC6"
            }

            Text(text = "${index + 1}. \n" +
                    " $displayText", style = MaterialTheme.typography.bodySmall)
        }
    }
}
