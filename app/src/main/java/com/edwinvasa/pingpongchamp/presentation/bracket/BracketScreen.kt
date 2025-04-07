package com.edwinvasa.pingpongchamp.presentation.bracket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edwinvasa.pingpongchamp.domain.model.TournamentMatch

@Composable
fun BracketScreen(players: List<String>) {
    val viewModel: BracketViewModel = viewModel()
    val matches by viewModel.matches.collectAsState()

    LaunchedEffect(players) {
        if (matches.isEmpty()) {
            viewModel.generateInitialMatches(players)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bracket del Campeonato", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(matches.size) { index ->
                val match = matches[index]
                MatchCard(match = match, onWinnerSelected = { winner ->
                    viewModel.setMatchWinner(match, winner)
                })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MatchCard(
    match: TournamentMatch,
    onWinnerSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Partido:")
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                PlayerSelectable(name = match.player1, isWinner = match.winner == match.player1) {
                    onWinnerSelected(match.player1)
                }
                Text("vs")
                PlayerSelectable(name = match.player2, isWinner = match.winner == match.player2) {
                    onWinnerSelected(match.player2)
                }
            }
        }
    }
}

@Composable
fun PlayerSelectable(name: String, isWinner: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isWinner) Color.Green else Color.LightGray

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(name)
    }
}
