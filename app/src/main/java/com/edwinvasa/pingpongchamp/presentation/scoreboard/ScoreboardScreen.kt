package com.edwinvasa.pingpongchamp.presentation.scoreboard

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.edwinvasa.pingpongchamp.presentation.bracket.BracketViewModel
import kotlinx.coroutines.delay
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
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
            initialRedName = player1 ?: "Jugador 1",
            initialGreenName = player2 ?: "Jugador 2"
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

    val showSetTransition = remember { mutableStateOf(false) }
    val showSetWinnerText = remember { mutableStateOf(false) }

    val scaleAnim by animateFloatAsState(
        targetValue = if (showSetWinnerText.value) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "ScaleAnim"
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (showSetWinnerText.value) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alphaAnim"
    )

    LaunchedEffect(viewModel.showNewSetMessage.value) {
        if (viewModel.showNewSetMessage.value && viewModel.lastSetWinner.value != null) {
            playSetWonSound(context)

            showSetTransition.value = true
            showSetWinnerText.value = true

            delay(3500) // Duración del mensaje

            showSetTransition.value = false
            showSetWinnerText.value = false
            viewModel.showNewSetMessage.value = false
        }
    }

    LaunchedEffect(viewModel.winner.value) {
        viewModel.winner.value?.let { winner ->
            playMatchWonSound(context)

            viewModel.showConfettiAnimation.value = true
            showBigWinnerText.value = true

            delay(3500)

            viewModel.showConfettiAnimation.value = false
            showBigWinnerText.value = false

            snackbarHostState.showSnackbar("🏆 Ganador: $winner")
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
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val isPortrait = maxHeight > maxWidth

                if (isPortrait) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScoreboardContent(
                            viewModel = viewModel,
                            isCustomMatch = isCustomMatch,
                            matchId = matchId,
                            bracketViewModel = bracketViewModel
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScoreboardContent(
                            viewModel = viewModel,
                            isCustomMatch = isCustomMatch,
                            matchId = matchId,
                            bracketViewModel = bracketViewModel,
                            isHorizontal = true
                        )
                    }
                }
            }

            if (showSetTransition.value && viewModel.lastSetWinner.value != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000))
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedVisibility(visible = showSetWinnerText.value) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .scale(scaleAnim)
                                    .alpha(alphaAnim)
                            ) {
                                Text(
                                    text = "🏁 ¡Set finalizado!",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Ganador: ${viewModel.lastSetWinner.value}",
                                    fontSize = 28.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "🏓 Comienza el Set #${viewModel.currentSet.value}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Animaciones y efectos
            if (viewModel.showSuddenDeathAnimation.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimationView(animationFile = "animations/sudden_death.json")
                }
            }

            if (viewModel.showConfettiAnimation.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimationView(animationFile = "animations/confetti.json", size = 500.dp)
                }
            }

            if (showBigWinnerText.value && viewModel.winner.value != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000))
                        .zIndex(2f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (viewModel.showConfettiAnimation.value) {
                            LottieAnimationView(animationFile = "animations/confetti.json", size = 500.dp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "🏆 ¡${viewModel.winner.value} gana el partido! 🏆",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
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

fun playSetWonSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.tada_set)
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
    mediaPlayer.start()
}

fun playMatchWonSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.victory_sound)
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

@Composable
fun ScoreboardContent(
    viewModel: ScoreboardViewModel,
    isCustomMatch: Boolean,
    matchId: String?,
    bracketViewModel: BracketViewModel?,
    isHorizontal: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Marcador", fontSize = 32.sp)

        if (isCustomMatch) {
            TextButton(onClick = { viewModel.showDialog.value = true }) {
                Text("Editar configuración")
            }
        }

        val redColor = viewModel.redPlayerColor.value
        val redContentColor = if (redColor.isDark()) Color.White else Color.Black

        val greenColor = viewModel.greenPlayerColor.value
        val greenContentColor = if (greenColor.isDark()) Color.White else Color.Black

        if (isHorizontal) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(redColor)
                        .padding(16.dp)
                ) {
                    CompositionLocalProvider(LocalContentColor provides redContentColor) {
                        PlayerScoreSection(
                            name = viewModel.redName.value,
                            score = viewModel.redPoints.value,
                            wins = viewModel.redWins.value,
                            onPlus = {
                                Log.d("Scoreboard", "onPlus presionado para rojo")
                                if (!viewModel.isGameOver) viewModel.redPoints.value++
                                viewModel.tryEndGameAutomatically(matchId, bracketViewModel)
                            },
                            onMinus = {
                                Log.d("Scoreboard", "onMinus presionado para rojo")
                                if (!viewModel.isGameOver && viewModel.redPoints.value > 0) viewModel.redPoints.value--
                                viewModel.tryEndGameAutomatically(matchId, bracketViewModel)
                            },
                            isServing = viewModel.currentServer == "rojo",
                            enabled = viewModel.winner.value == null
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(greenColor)
                        .padding(16.dp)
                ) {
                    CompositionLocalProvider(LocalContentColor provides greenContentColor) {
                        PlayerScoreSection(
                            name = viewModel.greenName.value,
                            score = viewModel.greenPoints.value,
                            wins = viewModel.greenWins.value,
                            onPlus = {
                                Log.d("Scoreboard", "onPlus presionado para verde")
                                if (!viewModel.isGameOver) viewModel.greenPoints.value++
                                viewModel.tryEndGameAutomatically(matchId, bracketViewModel)
                            },
                            onMinus = {
                                Log.d("Scoreboard", "onMinus presionado para verde")
                                if (!viewModel.isGameOver && viewModel.greenPoints.value > 0) viewModel.greenPoints.value--
                                viewModel.tryEndGameAutomatically(matchId, bracketViewModel)
                            },
                            isServing = viewModel.currentServer == "verde",
                            enabled = viewModel.winner.value == null
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(redColor)
                    .padding(16.dp)
            ) {
                CompositionLocalProvider(LocalContentColor provides redContentColor) {
                    PlayerScoreSection(
                        name = viewModel.redName.value,
                        score = viewModel.redPoints.value,
                        wins = viewModel.redWins.value,
                        onPlus = {
                            Log.d("Scoreboard", "onPlus presionado para rojo")
                            if (!viewModel.isGameOver) viewModel.redPoints.value++
                            viewModel.tryEndGameAutomatically(matchId, bracketViewModel)
                        },
                        onMinus = {
                            Log.d("Scoreboard", "onMinus presionado para rojo")
                            if (!viewModel.isGameOver && viewModel.redPoints.value > 0) viewModel.redPoints.value--
                            viewModel.tryEndGameAutomatically(matchId, bracketViewModel)
                        },
                        isServing = viewModel.currentServer == "rojo",
                        enabled = viewModel.winner.value == null
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(greenColor)
                    .padding(16.dp)
            ) {
                CompositionLocalProvider(LocalContentColor provides greenContentColor) {
                    PlayerScoreSection(
                        name = viewModel.greenName.value,
                        score = viewModel.greenPoints.value,
                        wins = viewModel.greenWins.value,
                        onPlus = {
                            Log.d("Scoreboard", "onPlus presionado para verde")
                            if (!viewModel.isGameOver) viewModel.greenPoints.value++
                            viewModel.tryEndGameAutomatically(matchId, bracketViewModel)
                        },
                        onMinus = {
                            Log.d("Scoreboard", "onMinus presionado para verde")
                            if (!viewModel.isGameOver && viewModel.greenPoints.value > 0) viewModel.greenPoints.value--
                            viewModel.tryEndGameAutomatically(matchId, bracketViewModel)
                        },
                        isServing = viewModel.currentServer == "verde",
                        enabled = viewModel.winner.value == null
                    )
                }
            }
        }
        /*
        EndMatchButton(
            viewModel = viewModel,
            isGameOver = viewModel.isGameOver,
            matchId = matchId,
            bracketViewModel = bracketViewModel,
            snackbarHostState = snackbarHostState,
            scope = coroutineScope
        )*/

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
            onToggleHistory = { viewModel.showHistory.value = !viewModel.showHistory.value },
            onClearHistory = { viewModel.clearHistory() }
        )
    }
}

fun Color.isDark(): Boolean {
    return this.luminance() < 0.5
}
