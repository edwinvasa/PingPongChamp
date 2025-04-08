package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun MatchConfigurationDialog(viewModel: ScoreboardViewModel) {
    Dialog(onDismissRequest = { viewModel.showDialog.value = false }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Configuración del partido",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = viewModel.redName.value,
                    onValueChange = { viewModel.redName.value = it },
                    label = { Text("Nombre jugador rojo") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = viewModel.greenName.value,
                    onValueChange = { viewModel.greenName.value = it },
                    label = { Text("Nombre jugador verde") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = viewModel.winningPoints.value.toString(),
                    onValueChange = {
                        viewModel.winningPoints.value = it.toIntOrNull() ?: 5
                    },
                    label = { Text("Puntos para ganar") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = viewModel.totalGames.value.toString(),
                    onValueChange = {
                        viewModel.totalGames.value = it.toIntOrNull() ?: 5
                    },
                    label = { Text("Total de partidos que se deben ganar") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Checkbox(
                        checked = viewModel.showServeIndicator.value,
                        onCheckedChange = { viewModel.showServeIndicator.value = it }
                    )
                    Text("Mostrar quién lleva el saque", style = MaterialTheme.typography.bodyMedium)
                }

                if (viewModel.showServeIndicator.value) {
                    OutlinedTextField(
                        value = viewModel.serveChangeFrequency.value.toString(),
                        onValueChange = {
                            viewModel.serveChangeFrequency.value = it.toIntOrNull() ?: 2
                        },
                        label = { Text("Cambiar saque cada X puntos") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    Column {
                        Text("Empieza sacando:", style = MaterialTheme.typography.bodyMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = viewModel.initialServer.value == "rojo",
                                onClick = { viewModel.initialServer.value = "rojo" }
                            )
                            Text(viewModel.redName.value)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = viewModel.initialServer.value == "verde",
                                onClick = { viewModel.initialServer.value = "verde" }
                            )
                            Text(viewModel.greenName.value)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = viewModel.suddenDeathEnabled.value,
                        onCheckedChange = { viewModel.suddenDeathEnabled.value = it }
                    )
                    Text("Requiere ventaja de 2 puntos al empatar", style = MaterialTheme.typography.bodyMedium)
                }

                Button(
                    onClick = { viewModel.showDialog.value = false },
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.SportsTennis, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Aceptar", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
