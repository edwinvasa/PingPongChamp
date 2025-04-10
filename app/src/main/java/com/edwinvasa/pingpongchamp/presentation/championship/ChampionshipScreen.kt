package com.edwinvasa.pingpongchamp.presentation.championship

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edwinvasa.pingpongchamp.R
import com.edwinvasa.pingpongchamp.presentation.main.Routes
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ChampionshipScreen(navController: NavController) {
    var playerName by remember { mutableStateOf("") }
    var players by remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var showBracketInfo by remember { mutableStateOf(false) }
    var showRoundRobinInfo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "🎯 Crear Campeonato",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Nombre del jugador") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                if (playerName.isNotBlank()) {
                    IconButton(onClick = { playerName = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Borrar")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (playerName.isNotBlank()) {
                    players = players + playerName.trim()
                    playerName = ""
                }
            },
            modifier = Modifier
                .align(Alignment.End)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Agregar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar jugador")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (players.isNotEmpty()) {
            Text("🎮 Jugadores añadidos:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                itemsIndexed(players, key = { _, name -> name }) { index, player ->
                    var visible by remember(player) { mutableStateOf(true) }

                    AnimatedVisibility(visible = visible) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(player, style = MaterialTheme.typography.bodyLarge)
                                }
                                IconButton(
                                    onClick = {
                                        visible = false
                                        coroutineScope.launch {
                                            delay(250)
                                            players = players.toMutableList().also { it.removeAt(index) }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar jugador")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            val playersJson = URLEncoder.encode(
                                Gson().toJson(players),
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate(Routes.PlayerRoulette.createRoute(playersJson))
                        },
                        enabled = players.size >= 2,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Bracket")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Torneo Eliminatorio")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showBracketInfo = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Info Bracket")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            val playersJson = URLEncoder.encode(
                                Gson().toJson(players),
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate(Routes.RoundRobin.createRoute(playersJson))
                        },
                        enabled = players.size >= 2,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Round Robin")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Torneo Todos contra todos")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showRoundRobinInfo = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Info Round Robin")
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showBracketInfo,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = { showBracketInfo = false },
            confirmButton = {
                TextButton(onClick = { showBracketInfo = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("¿Qué es un torneo eliminatorio?") },
            text = {
                Column {
                    Text("En este formato, los jugadores se enfrentan en rondas y el perdedor queda eliminado. ¡Solo uno puede ser campeón!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(painter = painterResource(id = R.drawable.bracket_example), contentDescription = null)
                }
            }
        )
    }

    AnimatedVisibility(
        visible = showRoundRobinInfo,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = { showRoundRobinInfo = false },
            confirmButton = {
                TextButton(onClick = { showRoundRobinInfo = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("¿Qué es un torneo todos contra todos?") },
            text = {
                Column {
                    Text("Cada jugador se enfrenta contra todos los demás. El que acumule más puntos será el campeón.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(painter = painterResource(id = R.drawable.roundrobin_example), contentDescription = null)
                }
            }
        )
    }
}