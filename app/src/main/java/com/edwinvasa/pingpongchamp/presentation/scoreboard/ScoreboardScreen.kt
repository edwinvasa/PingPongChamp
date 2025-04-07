package com.edwinvasa.pingpongchamp.presentation.scoreboard

import android.content.Context
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.launch

@Composable
fun ScoreboardScreen(
    navController: NavController? = null,
    matchId: String? = null,
    player1: String? = null,
    player2: String? = null,
    shouldReturnAfterMatch: Boolean = false
) {

    data class MatchResult(
        val redPoints: Int,
        val greenPoints: Int,
        val winner: String
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val isCustomMatch = matchId == null && player1 == null && player2 == null

    val parentEntry = remember(navController?.currentBackStackEntry) {
        navController?.getBackStackEntry(Routes.Bracket.route)
    }
    val viewModel: BracketViewModel? = parentEntry?.let { hiltViewModel(it) }

    var suddenDeathEnabled by remember { mutableStateOf(true) }

    var redName by remember { mutableStateOf(player1 ?: "Jugador Rojo") }
    var greenName by remember { mutableStateOf(player2 ?: "Jugador Verde") }

    var redPoints by remember { mutableStateOf(0) }
    var greenPoints by remember { mutableStateOf(0) }

    var redWins by remember { mutableStateOf(0) }
    var greenWins by remember { mutableStateOf(0) }

    var winningPoints by remember { mutableStateOf(5) }
    var totalGames by remember { mutableStateOf(5) }

    var winner by remember { mutableStateOf<String?>(null) }

    var matchHistory = remember { mutableStateListOf<MatchResult>() }
    var showHistory by remember { mutableStateOf(true) }

    var showServeIndicator by remember { mutableStateOf(false) }
    var initialServer by remember { mutableStateOf("rojo") }
    var serveChangeFrequency by remember { mutableStateOf(2) }

    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.victory_sound)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    var showDialog by remember { mutableStateOf(isCustomMatch) }
    var suddenDeathAnnounced by remember { mutableStateOf(false) }

    // Calcular el jugador que saca según los puntos jugados
    val totalPointsPlayed = redPoints + greenPoints

    val isSuddenDeathActive = suddenDeathEnabled &&
            redPoints == greenPoints &&
            redPoints >= winningPoints - 1

    val isGameOver = if (suddenDeathEnabled) {
        val hasRequiredPoints = redPoints >= winningPoints || greenPoints >= winningPoints
        val hasTwoPointLead = kotlin.math.abs(redPoints - greenPoints) >= 2
        hasRequiredPoints && hasTwoPointLead
    } else {
        redPoints >= winningPoints || greenPoints >= winningPoints
    }

    if (isSuddenDeathActive && !suddenDeathAnnounced) {
        LaunchedEffect("sudden_death") {
            suddenDeathAnnounced = true
            coroutineScope.launch {
                snackbarHostState.showSnackbar("¡Muerte súbita! Se necesita ventaja de 2 puntos.")
            }
            playSuddenDeathSound(context)
        }
    }

    if (!isSuddenDeathActive && suddenDeathAnnounced) {
        suddenDeathAnnounced = false // Reiniciar para futuros sets
    }

    val currentServer = remember(totalPointsPlayed, showServeIndicator, initialServer, serveChangeFrequency) {
        if (!showServeIndicator) null
        else getCurrentServer(totalPointsPlayed, initialServer, serveChangeFrequency, isSuddenDeathActive)
    }

    LaunchedEffect(winner) {
        if (winner != null) {
            mediaPlayer.start()
            snackbarHostState.showSnackbar("Ganador: $winner")
            delay(2000)
            if (shouldReturnAfterMatch) {
                navController?.popBackStack()
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Configuración del partido") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    OutlinedTextField(
                        value = winningPoints.toString(),
                        onValueChange = { winningPoints = it.toIntOrNull() ?: 5 },
                        label = { Text("Puntos para ganar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = totalGames.toString(),
                        onValueChange = { totalGames = it.toIntOrNull() ?: 5 },
                        label = { Text("Total de partidos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = serveChangeFrequency.toString(),
                        onValueChange = { serveChangeFrequency = it.toIntOrNull() ?: 2 },
                        label = { Text("Cambiar saque cada X puntos") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = showServeIndicator
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = showServeIndicator, onCheckedChange = { showServeIndicator = it })
                        Text("Mostrar quién lleva el saque")
                    }
                    if (showServeIndicator) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Empieza sacando:")
                            Spacer(Modifier.width(8.dp))
                            RadioButton(
                                selected = initialServer == "rojo",
                                onClick = { initialServer = "rojo" }
                            )
                            Text("Jugador Rojo")
                            Spacer(Modifier.width(8.dp))
                            RadioButton(
                                selected = initialServer == "verde",
                                onClick = { initialServer = "verde" }
                            )
                            Text("Jugador Verde")
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = suddenDeathEnabled, onCheckedChange = { suddenDeathEnabled = it })
                        Text("Requiere ventaja de 2 puntos al empatar")
                    }
                }
            }
        )
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

            if (isCustomMatch) {
                TextButton(onClick = { showDialog = true }) {
                    Text("Editar configuración")
                }
            }

            ScoreRow(
                name = redName,
                score = redPoints,
                onPlus = { if (!isGameOver) redPoints++ },
                onMinus = { if (!isGameOver && redPoints > 0) redPoints-- },
                isServing = currentServer == "rojo",
                enabled = winner == null
            )

            ScoreRow(
                name = greenName,
                score = greenPoints,
                onPlus = { if (!isGameOver) greenPoints++ },
                onMinus = { if (!isGameOver && greenPoints > 0) greenPoints-- },
                isServing = currentServer == "verde",
                enabled = winner == null
            )

            Text("$redName ha ganado $redWins partidos")
            Text("$greenName ha ganado $greenWins partidos")

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (!isGameOver) {
                            snackbarHostState.showSnackbar("No puedes finalizar el juego aún. En muerte súbita, se necesita ventaja de 2 puntos.")
                            return@launch
                        }

                        val redWinsGame = redPoints > greenPoints
                        val greenWinsGame = greenPoints > redPoints

                        when {
                            redWinsGame -> {
                                redWins++
                                matchHistory.add(MatchResult(redPoints, greenPoints, redName))
                                redPoints = 0
                                greenPoints = 0
                                if (redWins == totalGames) {
                                    winner = redName
                                }
                                if (matchId != null && viewModel != null) {
                                    viewModel.setMatchWinnerById(matchId, redName)
                                }
                            }

                            greenWinsGame -> {
                                greenWins++
                                matchHistory.add(MatchResult(redPoints, greenPoints, greenName))
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
                    }
                },
                enabled = winner == null && canEndGame(redPoints, greenPoints, winningPoints, suddenDeathEnabled)
            ) {
                Text("Finalizar Partido")
            }

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

            if (matchHistory.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Historial de partidos:", fontSize = 18.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = { showHistory = !showHistory }) {
                        Text(if (showHistory) "👁️" else "🚫")
                    }
                    IconButton(onClick = {
                        matchHistory.clear()
                    }) {
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
    }
}

fun getCurrentServer(
    totalPoints: Int,
    initialServer: String,
    serveChangeFrequency: Int,
    isSuddenDeath: Boolean
): String {
    val interval = if (isSuddenDeath) 1 else serveChangeFrequency
    val serverIndex = (totalPoints / interval) % 2
    return if (initialServer == "rojo") {
        if (serverIndex == 0) "rojo" else "verde"
    } else {
        if (serverIndex == 0) "verde" else "rojo"
    }
}

fun playSuddenDeathSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.sudden_death)
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
    mediaPlayer.start()
}

fun canEndGame(
    redPoints: Int,
    greenPoints: Int,
    pointsToWin: Int,
    suddenDeathEnabled: Boolean
): Boolean {
    val hasWinner = (redPoints >= pointsToWin || greenPoints >= pointsToWin)
    val diff = kotlin.math.abs(redPoints - greenPoints)

    return if (suddenDeathEnabled && redPoints >= pointsToWin && greenPoints >= pointsToWin) {
        diff >= 2
    } else {
        hasWinner
    }
}

@Composable
fun ScoreRow(
    name: String,
    score: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    isServing: Boolean = false,
    enabled: Boolean = true
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isServing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        label = "ServeIndicatorColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isServing) 1.2f else 1f,
        label = "ServeIndicatorScale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = if (isServing) "🏓 $name" else name,
            fontSize = 20.sp,
            color = animatedColor,
            modifier = Modifier.scale(scale)
        )
        IconButton(onClick = onMinus, enabled = enabled) { Text("-") }
        Text("$score", fontSize = 24.sp)
        IconButton(onClick = onPlus, enabled = enabled) { Text("+") }
    }
}
