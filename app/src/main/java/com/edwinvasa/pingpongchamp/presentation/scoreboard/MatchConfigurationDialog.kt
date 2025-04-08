package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MatchConfigurationDialog(viewModel: ScoreboardViewModel) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = { viewModel.showDialog.value = false }) {
                Text("Aceptar")
            }
        },
        title = { Text("Configuración del partido") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.redName.value,
                    onValueChange = { viewModel.redName.value = it },
                    label = { Text("Nombre jugador rojo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.greenName.value,
                    onValueChange = { viewModel.greenName.value = it },
                    label = { Text("Nombre jugador verde") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.winningPoints.value.toString(),
                    onValueChange = { viewModel.winningPoints.value = it.toIntOrNull() ?: 5 },
                    label = { Text("Puntos para ganar") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.totalGames.value.toString(),
                    onValueChange = { viewModel.totalGames.value = it.toIntOrNull() ?: 5 },
                    label = { Text("Total de partidos que se deben ganar") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.serveChangeFrequency.value.toString(),
                    onValueChange = { viewModel.serveChangeFrequency.value = it.toIntOrNull() ?: 2 },
                    label = { Text("Cambiar saque cada X puntos") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.showServeIndicator.value
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = viewModel.showServeIndicator.value, onCheckedChange = { viewModel.showServeIndicator.value = it })
                    Text("Mostrar quién lleva el saque")
                }
                if (viewModel.showServeIndicator.value) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Empieza sacando:")
                        Spacer(Modifier.width(8.dp))
                        RadioButton(
                            selected = viewModel.initialServer.value == "rojo",
                            onClick = { viewModel.initialServer.value = "rojo" }
                        )
                        Text("Jugador Rojo")
                        Spacer(Modifier.width(8.dp))
                        RadioButton(
                            selected = viewModel.initialServer.value == "verde",
                            onClick = { viewModel.initialServer.value = "verde" }
                        )
                        Text("Jugador Verde")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = viewModel.suddenDeathEnabled.value, onCheckedChange = { viewModel.suddenDeathEnabled.value = it })
                    Text("Requiere ventaja de 2 puntos al empatar")
                }
            }
        }
    )
}