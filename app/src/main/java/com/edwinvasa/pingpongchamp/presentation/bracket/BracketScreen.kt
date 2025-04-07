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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.edwinvasa.pingpongchamp.domain.model.TournamentMatch
import com.edwinvasa.pingpongchamp.presentation.main.Routes

@Composable
fun BracketScreen(
    navController: NavController,
    initialMatches: List<TournamentMatch>
) {
    val viewModel: BracketViewModel = hiltViewModel()
    val matches by viewModel.matches.collectAsState()
    val champion by viewModel.champion.collectAsState()

    LaunchedEffect(initialMatches) {
        if (matches.isEmpty()) {
            viewModel.setInitialMatches(initialMatches)
        }
    }

    LaunchedEffect(champion) {
        champion?.let { winner ->
            navController.navigate(Routes.Champion.createRoute(winner))
        }
    }

    val matchesByRound = matches.groupBy { it.round }.toSortedMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Campeonato", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            matchesByRound.forEach { (roundNumber, roundMatches) ->
                item {
                    Text(
                        text = "Ronda $roundNumber",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(roundMatches.size) { index ->
                    val match = roundMatches[index]
                    MatchCard(
                        match = match,
                        onWinnerSelected = { winner ->
                            viewModel.setMatchWinnerById(match.id, winner)
                        },
                        onStartMatch = {
                            val route = Routes.ScoreboardWithPlayers.createRoute(
                                match.player1,
                                match.player2,
                                match.id
                            )
                            navController.navigate(route)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MatchCard(
    match: TournamentMatch,
    onWinnerSelected: (String) -> Unit,
    onStartMatch: () -> Unit
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

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onStartMatch,
                enabled = match.winner == null
            ) {
                Text("Iniciar partido")
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
