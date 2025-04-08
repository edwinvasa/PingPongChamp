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
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.edwinvasa.pingpongchamp.R
import com.edwinvasa.pingpongchamp.presentation.main.Routes
import kotlinx.coroutines.launch

@Composable
fun ScoreboardScreen(
    navController: NavController? = null,
    matchId: String? = null,
    player1: String? = null,
    player2: String? = null,
    shouldReturnAfterMatch: Boolean = false
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val showBigWinnerText = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isCustomMatch = matchId == null && player1 == null && player2 == null

    val viewModel = remember {
        ScoreboardViewModel(
            context = context,
            isCustomMatch = isCustomMatch,
            initialRedName = player1 ?: "Jugador Rojo",
            initialGreenName = player2 ?: "Jugador Verde"
        )
    }

    val parentEntry = remember(navController?.currentBackStackEntry) {
        navController?.getBackStackEntry(Routes.Bracket.route)
    }
    val bracketViewModel: BracketViewModel? = parentEntry?.let { hiltViewModel(it) }

    LaunchedEffect(viewModel.redPoints.value, viewModel.greenPoints.value) {
        if (viewModel.shouldTriggerSuddenDeathEvent()) {
            coroutineScope.launch {
                viewModel.showSuddenDeathAnimation.value = true
                snackbarHostState.showSnackbar("¡Muerte súbita! Se necesita ventaja de 2 puntos.")
                delay(800) // dura la animación
                viewModel.showSuddenDeathAnimation.value = false
            }
            playSuddenDeathSound(context)
        }
    }

    LaunchedEffect(viewModel.winner.value) {
        viewModel.winner.value?.let {
            viewModel.mediaPlayer.start()
            viewModel.showConfettiAnimation.value = true
            showBigWinnerText.value = true

            delay(3500) // tiempo para mostrar confetti + texto grande

            viewModel.showConfettiAnimation.value = false
            showBigWinnerText.value = false

            snackbarHostState.showSnackbar("Ganador: $it")
            delay(2000)

            if (shouldReturnAfterMatch) {
                navController?.popBackStack()
            }
        }
    }

    if (viewModel.showDialog.value) {
        MatchConfigurationDialog(viewModel)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                    TextButton(onClick = { viewModel.showDialog.value = true }) {
                        Text("Editar configuración")
                    }
                }

                PlayerScoreSection(
                    name = viewModel.redName.value,
                    score = viewModel.redPoints.value,
                    wins = viewModel.redWins.value,
                    onPlus = { if (!viewModel.isGameOver) viewModel.redPoints.value++ },
                    onMinus = { if (!viewModel.isGameOver && viewModel.redPoints.value > 0) viewModel.redPoints.value-- },
                    isServing = viewModel.currentServer == "rojo",
                    enabled = viewModel.winner.value == null
                )

                PlayerScoreSection(
                    name = viewModel.greenName.value,
                    score = viewModel.greenPoints.value,
                    wins = viewModel.greenWins.value,
                    onPlus = { if (!viewModel.isGameOver) viewModel.greenPoints.value++ },
                    onMinus = { if (!viewModel.isGameOver && viewModel.greenPoints.value > 0) viewModel.greenPoints.value-- },
                    isServing = viewModel.currentServer == "verde",
                    enabled = viewModel.winner.value == null
                )

                EndMatchButton(
                    viewModel = viewModel,
                    isGameOver = viewModel.isGameOver,
                    matchId = matchId,
                    bracketViewModel = bracketViewModel,
                    snackbarHostState = snackbarHostState,
                    scope = coroutineScope
                )

                viewModel.winner.value?.let {
                    Text("¡Ganador: $it!", color = MaterialTheme.colorScheme.primary)

                    Button(onClick = { viewModel.resetMatch() }) {
                        Text("Reiniciar Marcador")
                    }
                }

                MatchHistorySection(
                    matchHistory = viewModel.matchHistory,
                    redName = viewModel.redName.value,
                    greenName = viewModel.greenName.value,
                    showHistory = viewModel.showHistory.value,
                    onToggleHistory = {
                        viewModel.showHistory.value = !viewModel.showHistory.value
                    },
                    onClearHistory = { viewModel.clearHistory() }
                )
            }

            if (viewModel.showSuddenDeathAnimation.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimationView(animationFile = "animations/sudden_death.json")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.showConfettiAnimation.value) {
                    LottieAnimationView(animationFile = "animations/confetti.json", size = 500.dp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (showBigWinnerText.value && viewModel.winner.value != null) {
                    Text(
                        text = "🏆 ¡${viewModel.winner.value} gana! 🏆",
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}

fun playSuddenDeathSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.sudden_death)
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
    mediaPlayer.start()
}

@Composable
fun LottieAnimationView(animationFile: String, size: Dp = 250.dp) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(animationFile))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.5f
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier.size(size)
    )
}
