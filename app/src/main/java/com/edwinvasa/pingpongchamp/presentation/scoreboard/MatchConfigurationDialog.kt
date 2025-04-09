package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MatchConfigurationDialog(viewModel: ScoreboardViewModel) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxDialogHeight = screenHeight * 0.8f // 80% del alto de pantalla

    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = { viewModel.showDialog.value = false }) {
                Text("Aceptar")
            }
        },
        title = { Text("Configuración del partido") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxDialogHeight)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.redName.value,
                    onValueChange = { viewModel.redName.value = it },
                    label = { Text("Nombre del jugador 1") },
                    enabled = viewModel.isCustomMatch
                )
                ColorPaletteSelector(
                    selectedColor = viewModel.redPlayerColor.value,
                    onColorSelected = { viewModel.redPlayerColor.value = it },
                    label = "Color"
                )
                OutlinedTextField(
                    value = viewModel.greenName.value,
                    onValueChange = { viewModel.greenName.value = it },
                    label = { Text("Nombre del jugador 2") },
                    enabled = viewModel.isCustomMatch
                )
                ColorPaletteSelector(
                    selectedColor = viewModel.greenPlayerColor.value,
                    onColorSelected = { viewModel.greenPlayerColor.value = it },
                    label = "Color"
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
                    Checkbox(
                        checked = viewModel.showServeIndicator.value,
                        onCheckedChange = { viewModel.showServeIndicator.value = it }
                    )
                    Text("Mostrar quién lleva el saque")
                }

                if (viewModel.showServeIndicator.value) {
                    Column {
                        Text("Empieza sacando:")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = viewModel.initialServer.value == "rojo",
                                onClick = { viewModel.initialServer.value = "rojo" }
                            )
                            Text("Jugador 1")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = viewModel.initialServer.value == "verde",
                                onClick = { viewModel.initialServer.value = "verde" }
                            )
                            Text("Jugador 2")
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = viewModel.suddenDeathEnabled.value,
                        onCheckedChange = { viewModel.suddenDeathEnabled.value = it }
                    )
                    Text("Requiere ventaja de 2 puntos al empatar")
                }
            }
        }
    )
}

@Composable
fun ColorPaletteSelector(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    label: String
) {
    val availableColors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Magenta,
        Color.Cyan,
        Color.Gray,
        Color(0xFFFFA500), // Orange
        Color(0xFF8A2BE2), // BlueViolet
        Color(0xFF00CED1)  // DarkTurquoise
    )

    Column {
        Text(label, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
            availableColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (color == selectedColor) 3.dp else 1.dp,
                            color = if (color == selectedColor) Color.Black else Color.LightGray,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}
