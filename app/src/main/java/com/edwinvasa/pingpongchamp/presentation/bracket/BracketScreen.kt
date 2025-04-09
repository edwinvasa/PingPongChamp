package com.edwinvasa.pingpongchamp.presentation.bracket

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.edwinvasa.pingpongchamp.domain.model.TournamentMatch
import com.edwinvasa.pingpongchamp.presentation.main.Routes
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlin.math.ceil
import kotlin.math.log2

@Composable
fun BracketScreen(
    navController: NavController,
    initialMatches: List<TournamentMatch>
) {
    val viewModel: BracketViewModel = hiltViewModel()
    val matches by viewModel.matches.collectAsState()
    val champion by viewModel.champion.collectAsState()

    val scrollState = rememberScrollState()

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

    // Calcular el número total de rondas basado en los matches iniciales
    val initialPlayerCount = initialMatches.size * 2
    val expectedTotalRounds = ceil(log2(initialPlayerCount.toDouble())).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "TORNEO DE PING PONG",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            matchesByRound.forEach { (roundNumber, roundMatches) ->
                val roundLabel = when (roundNumber) {
                    expectedTotalRounds -> "Final"
                    expectedTotalRounds - 1 -> "Semifinal"
                    else -> "Ronda $roundNumber"
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.width(180.dp)
                    ) {
                        Text(
                            text = roundLabel,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        roundMatches.forEach { match ->
                            MatchBox(
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
                        }
                    }
                }
            }
        }
    }
}
