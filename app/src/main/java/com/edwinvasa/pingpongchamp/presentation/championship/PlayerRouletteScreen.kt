package com.edwinvasa.pingpongchamp.presentation.championship

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import com.edwinvasa.pingpongchamp.R
import com.edwinvasa.pingpongchamp.domain.model.TournamentMatch
import com.edwinvasa.pingpongchamp.presentation.main.Routes
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@Composable
fun PlayerRouletteScreen(
    navController: NavController,
    players: List<String>,
    onAllMatchesGenerated: (List<TournamentMatch>) -> Unit
) {
    var remainingPlayers by remember { mutableStateOf(players.shuffled().toMutableList()) }
    var selectedPlayers by remember { mutableStateOf(listOf<String>()) }
    var highlightedPlayer by remember { mutableStateOf<String?>(null) }
    var isAnimating by remember { mutableStateOf(false) }
    var matches by remember { mutableStateOf(listOf<TournamentMatch>()) }
    var showMatchBanner by remember { mutableStateOf(false) }
    var autoMode by remember { mutableStateOf(false) }
    var byeMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Sonidos
    val drumRollPlayer = remember { MediaPlayer.create(context, R.raw.drumroll) }
    val selectPlayer = remember { MediaPlayer.create(context, R.raw.select) }
    val matchReadyPlayer = remember { MediaPlayer.create(context, R.raw.match_ready) }

    val scrollState = rememberScrollState()

    fun playSelectionAnimation(onFinish: () -> Unit) {
        coroutineScope.launch {
            isAnimating = true
            byeMessage = null
            drumRollPlayer.start()

            val totalDuration = 3000L
            val stepDelay = 100L
            val iterations = (totalDuration / stepDelay).toInt()

            repeat(iterations) {
                highlightedPlayer = remainingPlayers.random()
                delay(stepDelay)
            }

            drumRollPlayer.pause()
            drumRollPlayer.seekTo(0)
            selectPlayer.start()

            val selected = highlightedPlayer
            if (selected != null) {
                delay(500L)
                remainingPlayers.remove(selected)
                selectedPlayers = selectedPlayers + selected
            }

            if (selectedPlayers.size == 2) {
                matchReadyPlayer.start()
                showMatchBanner = true
                delay(2000L)

                val match = TournamentMatch(
                    id = UUID.randomUUID().toString(),
                    player1 = selectedPlayers[0],
                    player2 = selectedPlayers[1]
                )
                matches = matches + match
                selectedPlayers = emptyList()
                showMatchBanner = false
            }

            highlightedPlayer = null
            isAnimating = false

            if (remainingPlayers.size == 1 && selectedPlayers.isEmpty()) {
                val byePlayer = remainingPlayers.removeAt(0)
                val match = TournamentMatch(
                    id = UUID.randomUUID().toString(),
                    player1 = byePlayer,
                    player2 = "BYE"
                )
                matches = matches + match
                byeMessage = "$byePlayer pasa directamente"
                showMatchBanner = true
                delay(2500L)
                showMatchBanner = false
            }

            if (remainingPlayers.size < 2 && selectedPlayers.isEmpty()) {
                onAllMatchesGenerated(matches)
                val matchesJson = URLEncoder.encode(Gson().toJson(matches), StandardCharsets.UTF_8.toString())
                navController.navigate(Routes.Bracket.createRoute(matchesJson))
            } else if (autoMode) {
                delay(1000)
                playSelectionAnimation(onFinish)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sorteo de Jugadores", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(players) { player ->
                    val wasSelected = matches.any { it.player1 == player || it.player2 == player }
                    PlayerCard(
                        player = player,
                        isHighlighted = player == highlightedPlayer,
                        isDisabled = wasSelected
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Seleccionados: ${selectedPlayers.joinToString(" vs ")}")

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { if (!isAnimating) playSelectionAnimation {} },
                    enabled = !isAnimating && remainingPlayers.size >= 1
                ) {
                    Text("Siguiente Sorteo")
                }

                Button(
                    onClick = {
                        if (!isAnimating && remainingPlayers.size >= 1) {
                            autoMode = true
                            playSelectionAnimation {}
                        }
                    },
                    enabled = !isAnimating && remainingPlayers.size >= 1
                ) {
                    Text("Auto Sortear")
                }
            }
        }

        AnimatedVisibility(visible = showMatchBanner, modifier = Modifier.fillMaxSize()) {
            val transition = rememberInfiniteTransition(label = "MatchBannerTransition")

            val glowColor by transition.animateColor(
                initialValue = Color(0xFFFF4081), // Rosa neón
                targetValue = Color(0xFF448AFF), // Azul neón
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "GlowColor"
            )

            val scalePulse by transition.animateFloat(
                initialValue = 1f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "ScalePulse"
            )

            // Fondo oscuro semitransparente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000)) // negro con opacidad
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scalePulse
                            scaleY = scalePulse
                        }
                ) {
                    if (byeMessage != null) {
                        Text(
                            text = byeMessage ?: "",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4CAF50),
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.White,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 12f
                                )
                            )
                        )
                    } else {
                        Text(
                            text = selectedPlayers.getOrNull(0) ?: "",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = glowColor,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = glowColor,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 18f
                                )
                            )
                        )
                        Text(
                            text = "VS",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = selectedPlayers.getOrNull(1) ?: "",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = glowColor,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = glowColor,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 18f
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCard(player: String, isHighlighted: Boolean, isDisabled: Boolean) {
    val backgroundColor by animateColorAsState(
        when {
            isHighlighted -> Color(0xFFFFD54F)
            isDisabled -> Color.LightGray
            else -> MaterialTheme.colorScheme.surface
        },
        label = "CardBackgroundColor"
    )

    val scale by animateFloatAsState(
        if (isHighlighted) 1.05f else 1f,
        label = "CardScale"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .padding(horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = when {
                    isDisabled -> Color.Gray
                    isHighlighted -> Color.Red
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = player,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDisabled) Color.DarkGray else Color.Unspecified
                )
            )
        }
    }
}