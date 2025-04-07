package com.edwinvasa.pingpongchamp.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    onScoreboardClick: () -> Unit,
    onChampionshipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ping Pong Champ 🏓",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Button(
            onClick = onScoreboardClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(text = "Ir al Marcador")
        }

        Button(
            onClick = onChampionshipClick,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Ir a Campeonato")
        }
    }
}
