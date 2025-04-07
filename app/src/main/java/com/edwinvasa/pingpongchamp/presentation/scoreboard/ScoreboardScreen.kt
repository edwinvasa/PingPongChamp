package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.edwinvasa.pingpongchamp.presentation.bracket.BracketViewModel
import kotlinx.coroutines.delay
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.edwinvasa.pingpongchamp.R
import com.edwinvasa.pingpongchamp.presentation.main.Routes

@Composable
fun ScoreboardScreen(
    navController: NavController? = null,
    matchId: String? = null,
    player1: String? = null,
    player2: String? = null,
    shouldReturnAfterMatch: Boolean = false
) {
    var redName by remember { mutableStateOf(player1 ?: "Jugador Rojo") }
    var greenName by remember { mutableStateOf(player2 ?: "Jugador Verde") }

    val parentEntry = remember(navController?.currentBackStackEntry) {
        navController?.getBackStackEntry(Routes.Bracket.route)
    }
    val viewModel: BracketViewModel? = parentEntry?.let { hiltViewModel(it) }

    var redPoints by remember { mutableStateOf(0) }
    var greenPoints by remember { mutableStateOf(0) }

    var redWins by remember { mutableStateOf(0) }
    var greenWins by remember { mutableStateOf(0) }

    var winningPoints by remember { mutableStateOf(5) }
    var totalGames by remember { mutableStateOf(5) }

    var winner by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.victory_sound) }

    LaunchedEffect(winner) {
        if (winner != null) {
            mediaPlayer.start()
            snackbarHostState.showSnackbar("Ganador: $winner")
            delay(2000)
            navController?.popBackStack()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Marcador", fontSize = 32.sp)

            // Agrupamos los campos arriba
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = redName,
                    onValueChange = { redName = it },
                    label = { Text("Nombre jugador rojo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = greenName,
                    onValueChange = { greenName = it },
                    label = { Text("Nombre jugador verde") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = winningPoints.toString(),
                        onValueChange = { winningPoints = it.toIntOrNull() ?: 5 },
                        label = { Text("Puntos para ganar") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = totalGames.toString(),
                        onValueChange = { totalGames = it.toIntOrNull() ?: 5 },
                        label = { Text("Total de Partidos") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ScoreRow(redName, redPoints, { redPoints++ }, { if (redPoints > 0) redPoints-- })
            ScoreRow(greenName, greenPoints, { greenPoints++ }, { if (greenPoints > 0) greenPoints-- })

            Text("$redName ha ganado $redWins partidos")
            Text("$greenName ha ganado $greenWins partidos")

            Button(
                onClick = {
                    when {
                        redPoints >= winningPoints -> {
                            redWins++
                            redPoints = 0
                            greenPoints = 0
                            if (redWins == totalGames) {
                                winner = redName
                            }
                            if (matchId != null && viewModel != null) {
                                viewModel.setMatchWinnerById(matchId, redName)
                            }
                        }

                        greenPoints >= winningPoints -> {
                            greenWins++
                            redPoints = 0
                            greenPoints = 0
                            if (greenWins == totalGames) {
                                winner = greenName
                            }
                            if (matchId != null && viewModel != null) {
                                viewModel.setMatchWinnerById(matchId, greenName)
                            }
                        }
                    }
                },
                enabled = winner == null
            ) {
                Text("Finalizar Partido")
            }

            // Botón de reinicio cuando ya hay ganador
            if (winner != null) {
                Text("¡Ganador: $winner!", color = MaterialTheme.colorScheme.primary)

                Button(
                    onClick = {
                        redPoints = 0
                        greenPoints = 0
                        redWins = 0
                        greenWins = 0
                        winner = null
                    }
                ) {
                    Text("Reiniciar Marcador")
                }
            }
        }
    }
}

@Composable
fun ScoreRow(
    name: String,
    score: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(name, fontSize = 20.sp)
        IconButton(onClick = onMinus) { Text("-") }
        Text("$score", fontSize = 24.sp)
        IconButton(onClick = onPlus) { Text("+") }
    }
}
