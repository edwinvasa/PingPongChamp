package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@Composable
fun MatchHistoryFloatingPanel(
    matchHistory: List<MatchPingPongResult>,
    redName: String,
    greenName: String,
    showHistory: Boolean,
    onToggleHistory: () -> Unit,
    onClearHistory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            // Botón para mostrar/ocultar
            FloatingActionButton(
                onClick = onToggleHistory,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (showHistory) Icons.Default.VisibilityOff else Icons.Default.History,
                    contentDescription = "Mostrar/Ocultar historial"
                )
            }

            // Panel flotante con historial
            AnimatedVisibility(
                visible = showHistory,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                Surface(
                    tonalElevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .widthIn(min = 200.dp)
                        .width(260.dp)
                        .heightIn(max = 300.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Historial", style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = onClearHistory) {
                                Icon(Icons.Default.Delete, contentDescription = "Limpiar")
                            }
                        }

                        if (matchHistory.isEmpty()) {
                            Text("Sin partidas registradas.", fontSize = 14.sp)
                        } else {
                            LazyColumn {
                                itemsIndexed(matchHistory) { index, match ->
                                    Text(
                                        buildString {
                                            append("${index + 1}. ")
                                            if (match.redPoints > match.greenPoints) {
                                                append("🏆$redName ${match.redPoints} - ${match.greenPoints} $greenName")
                                            } else {
                                                append("$redName ${match.redPoints} - ${match.greenPoints} $greenName🏆")
                                            }
                                        },
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
