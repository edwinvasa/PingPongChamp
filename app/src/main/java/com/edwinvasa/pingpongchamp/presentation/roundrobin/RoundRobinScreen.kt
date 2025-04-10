package com.edwinvasa.pingpongchamp.presentation.roundrobin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.edwinvasa.pingpongchamp.domain.model.MatchPingPongResult
import com.edwinvasa.pingpongchamp.domain.model.RoundRobinMatch
import com.edwinvasa.pingpongchamp.presentation.bracket.MatchHistoryList
import com.edwinvasa.pingpongchamp.presentation.championship.PlayerSlot
import com.edwinvasa.pingpongchamp.presentation.main.Routes
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun RoundRobinScreen(
    navController: NavController,
    players: List<String>
) {
    val viewModel: RoundRobinViewModel = hiltViewModel()

    LaunchedEffect(players) {
        if (viewModel.tournament.value == null) {
            viewModel.startTournament(players)
        }
    }

    val tournament by viewModel.tournament.collectAsState()
    val standings = viewModel.getDetailedStandings()
    val hasMatchesPlayed = tournament?.matches?.any { it.result != null } == true
    val pendingMatches = tournament?.matches?.filter { it.result == null } ?: emptyList()
    val playedMatches = tournament?.matches?.filter { it.result != null } ?: emptyList()

    val allMatchesPlayed = pendingMatches.isEmpty() && playedMatches.isNotEmpty()
    var showFinalMessage by remember { mutableStateOf(false) }

    LaunchedEffect(allMatchesPlayed) {
        if (allMatchesPlayed) {
            showFinalMessage = true
            delay(4000)
            showFinalMessage = false
        }
    }

    val lastMatchStates = remember { mutableStateMapOf<String, MatchPingPongResult?>() }

    LaunchedEffect(tournament) {
        tournament?.matches?.forEach { match ->
            val previousResult = lastMatchStates[match.id]
            val newResult = match.result

            if (newResult != null && previousResult != newResult) {
                lastMatchStates[match.id] = newResult
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text("Torneo Round Robin", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            if (hasMatchesPlayed && standings.isNotEmpty()) {
                Text("Tabla de Posiciones", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f)
                ) {
                    RankingStandingsDetailed(standings)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (pendingMatches.isNotEmpty()) {
                    item {
                        Text("Partidos Pendientes", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(pendingMatches) { match ->
                        RoundRobinMatchBox(
                            match = match,
                            onStartMatch = {
                                val route = Routes.RoundRobinScoreboard.createRoute(
                                    match.player1, match.player2, match.id
                                )
                                navController.navigate(route)
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                if (playedMatches.isNotEmpty()) {
                    item {
                        Text("Partidos Jugados", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(playedMatches) { match ->
                        RoundRobinMatchBox(
                            match = match,
                            onStartMatch = {
                                val route = Routes.RoundRobinScoreboard.createRoute(
                                    match.player1, match.player2, match.id
                                )
                                navController.navigate(route)
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showFinalMessage,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏁 ¡Torneo Finalizado!", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    standings.take(3).forEachIndexed { index, playerStats ->
                        val icon = when (index) {
                            0 -> "🏆"
                            1 -> "🥈"
                            2 -> "🥉"
                            else -> ""
                        }
                        Text("${index + 1}. $icon ${playerStats.player}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RankingStandingsDetailed(standings: List<RoundRobinViewModel.PlayerStats>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("#", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Jugador", fontWeight = FontWeight.Bold, modifier = Modifier.weight(3f))
            Text("V/P", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
            Text("Sets", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
            Text("✅", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("🚫", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Pts", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        val scrollState = rememberScrollState()
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            standings.forEachIndexed { index, stats ->
                val (fontSize, icon, weight) = when (index) {
                    0 -> Triple(18.sp, "🏆", FontWeight.Bold)
                    1 -> Triple(17.sp, "🥈", FontWeight.SemiBold)
                    2 -> Triple(16.sp, "🥉", FontWeight.Medium)
                    else -> Triple(14.sp, "", FontWeight.Normal)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${index + 1}.", fontSize = fontSize, fontWeight = weight, modifier = Modifier.weight(1f))
                    Text("$icon ${stats.player}", fontSize = fontSize, fontWeight = weight, modifier = Modifier.weight(3f))
                    Text("${stats.wins}/${stats.losses}", fontSize = fontSize, modifier = Modifier.weight(2f))
                    Text("${stats.setsPlayed}", fontSize = fontSize, modifier = Modifier.weight(2f))
                    Text("${stats.pointsFor}", fontSize = fontSize, modifier = Modifier.weight(1f))
                    Text("${stats.pointsAgainst}", fontSize = fontSize, modifier = Modifier.weight(1f))
                    Text("${stats.wins}", fontSize = fontSize, modifier = Modifier.weight(1f))
                }
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
            }
        }
    }
}


@Composable
fun RoundRobinMatchBox(
    match: RoundRobinMatch,
    onStartMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHistory by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        PlayerSlot(
            name = match.player1,
            isWinner = match.result?.winner == match.player1,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(4.dp))

        PlayerSlot(
            name = match.player2,
            isWinner = match.result?.winner == match.player2,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (match.result != null) {
                IconButton(onClick = { showHistory = !showHistory }) {
                    Icon(Icons.Default.History, contentDescription = "Ver historial")
                }
            }

            Button(
                onClick = onStartMatch,
                enabled = match.result == null,
                modifier = Modifier.height(32.dp)
            ) {
                Text("Jugar", style = MaterialTheme.typography.labelSmall)
            }
        }

        if (match.result != null && showHistory) {
            Spacer(modifier = Modifier.height(8.dp))
            MatchHistoryList(
                player1 = match.player1,
                player2 = match.player2,
                matchHistory = match.matchHistory
            )
        }
    }
}