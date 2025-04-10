package com.edwinvasa.pingpongchamp.presentation.bracket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edwinvasa.pingpongchamp.domain.model.TournamentMatch
import com.edwinvasa.pingpongchamp.presentation.championship.PlayerSlot

@Composable
fun MatchBox(
    match: TournamentMatch,
    onWinnerSelected: (String) -> Unit,
    onStartMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHistory by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .width(180.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Text("Ronda ${match.round}", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(4.dp))

        PlayerSlot(match.player1, isWinner = match.winner == match.player1) {
            onWinnerSelected(match.player1)
        }

        Spacer(modifier = Modifier.height(4.dp))

        PlayerSlot(match.player2, isWinner = match.winner == match.player2) {
            onWinnerSelected(match.player2)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (match.winner != null) {
                IconButton(onClick = { showHistory = !showHistory }) {
                    Icon(Icons.Default.History, contentDescription = "Ver historial")
                }
            }

            Button(
                onClick = onStartMatch,
                enabled = match.winner == null,
                modifier = Modifier.height(32.dp)
            ) {
                Text("Jugar", style = MaterialTheme.typography.labelSmall)
            }
        }

        if (match.winner != null && showHistory) {
            Spacer(modifier = Modifier.height(8.dp))
            MatchHistoryList(
                player1 = match.player1,
                player2 = match.player2,
                matchHistory = match.matchHistory
            )
        }
    }
}
