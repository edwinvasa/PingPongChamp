package com.edwinvasa.pingpongchamp.presentation.championship

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edwinvasa.pingpongchamp.presentation.main.Routes
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ChampionshipScreen(navController: NavController) {
    var playerName by remember { mutableStateOf("") }
    var players by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Crear Campeonato", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Nombre del jugador") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (playerName.isNotBlank()) {
                    players = players + playerName.trim()
                    playerName = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Agregar jugador")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Jugadores añadidos:")
        LazyColumn {
            items(players.size) { index ->
                Text("• ${players[index]}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val playersJson = URLEncoder.encode(Gson().toJson(players), StandardCharsets.UTF_8.toString())
                navController.navigate(Routes.PlayerRoulette.createRoute(playersJson))
            },
            enabled = players.size >= 2,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Sortear partidos")
        }
    }
}
