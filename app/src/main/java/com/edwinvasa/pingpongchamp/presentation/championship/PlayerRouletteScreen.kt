package com.edwinvasa.pingpongchamp.presentation.championship

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edwinvasa.pingpongchamp.presentation.main.Routes
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PlayerRouletteScreen(
    navController: NavController,
    players: List<String>,
    onMatchSelected: (player1: String, player2: String) -> Unit
) {
    var remainingPlayers by remember { mutableStateOf(players.toMutableList()) }
    var selectedPlayers by remember { mutableStateOf(listOf<String>()) }
    var highlightedPlayer by remember { mutableStateOf<String?>(null) }
    var isAnimating by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sorteo de Jugadores", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(remainingPlayers.toList()) { player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (player == highlightedPlayer) Color.Yellow else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = player,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (remainingPlayers.isNotEmpty() && !isAnimating) {
                    isAnimating = true
                    coroutineScope.launch {
                        val totalDuration = 3000L
                        val stepDelay = 100L
                        val iterations = (totalDuration / stepDelay).toInt()

                        repeat(iterations) {
                            highlightedPlayer = remainingPlayers.random()
                            delay(stepDelay)
                        }

                        val selected = highlightedPlayer ?: return@launch
                        delay(100L)

                        remainingPlayers = remainingPlayers.toMutableList().apply { remove(selected) }
                        selectedPlayers = selectedPlayers + selected
                        isAnimating = false

                        if (selectedPlayers.size == 2) {
                            delay(1000L)
                            onMatchSelected(selectedPlayers[0], selectedPlayers[1])
                            selectedPlayers = emptyList()
                        }
                    }
                }
            },
            enabled = remainingPlayers.isNotEmpty() && !isAnimating
        ) {
            Text("Seleccionar jugador")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Seleccionados: ${selectedPlayers.joinToString(" vs ")}")

        if (remainingPlayers.isEmpty() && selectedPlayers.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val allPlayers = players
                    val playersJson = URLEncoder.encode(Gson().toJson(allPlayers), StandardCharsets.UTF_8.toString())
                    navController.navigate(Routes.Bracket.createRoute(playersJson))
                }
            ) {
                Text("Ver Bracket")
            }
        }


    }
}
