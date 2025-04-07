package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreboardScreen() {
    var playerRedName by remember { mutableStateOf(TextFieldValue("Jugador Rojo")) }
    var playerGreenName by remember { mutableStateOf(TextFieldValue("Jugador Verde")) }

    var redPoints by remember { mutableStateOf(0) }
    var greenPoints by remember { mutableStateOf(0) }

    var redWins by remember { mutableStateOf(0) }
    var greenWins by remember { mutableStateOf(0) }

    var winningPoints by remember { mutableStateOf(5) }
    var totalGames by remember { mutableStateOf(5) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Marcador", fontSize = 32.sp)

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = playerRedName,
                onValueChange = { playerRedName = it },
                label = { Text("Jugador Rojo") }
            )
            OutlinedTextField(
                value = playerGreenName,
                onValueChange = { playerGreenName = it },
                label = { Text("Jugador Verde") }
            )
        }

        ScoreRow(playerRedName.text, redPoints, { redPoints++ }, { if (redPoints > 0) redPoints-- })
        ScoreRow(playerGreenName.text, greenPoints, { greenPoints++ }, { if (greenPoints > 0) greenPoints-- })

        Text("${playerRedName.text} ha ganado $redWins partidos")
        Text("${playerGreenName.text} ha ganado $greenWins partidos")

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = winningPoints.toString(),
                onValueChange = { winningPoints = it.toIntOrNull() ?: 5 },
                label = { Text("Puntos para ganar") }
            )
            OutlinedTextField(
                value = totalGames.toString(),
                onValueChange = { totalGames = it.toIntOrNull() ?: 5 },
                label = { Text("Total de Partidos") }
            )
        }

        Button(onClick = {
            if (redPoints >= winningPoints) {
                redWins++
                redPoints = 0
                greenPoints = 0
            } else if (greenPoints >= winningPoints) {
                greenWins++
                redPoints = 0
                greenPoints = 0
            }
        }) {
            Text("Finalizar Partido")
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
