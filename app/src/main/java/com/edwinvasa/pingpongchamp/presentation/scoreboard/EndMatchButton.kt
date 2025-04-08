package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.edwinvasa.pingpongchamp.presentation.bracket.BracketViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.Icon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EndMatchButton(
    viewModel: ScoreboardViewModel,
    isGameOver: Boolean,
    matchId: String?,
    bracketViewModel: BracketViewModel?,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    Button(
        onClick = {
            scope.launch {
                if (!isGameOver) {
                    snackbarHostState.showSnackbar("No puedes finalizar el juego aún. En muerte súbita, se necesita ventaja de 2 puntos.")
                    return@launch
                }

                val redWinsGame = viewModel.redPoints.value > viewModel.greenPoints.value
                val greenWinsGame = viewModel.greenPoints.value > viewModel.redPoints.value

                when {
                    redWinsGame -> {
                        viewModel.redWins.value++
                        viewModel.matchHistory.add(MatchResult(viewModel.redPoints.value, viewModel.greenPoints.value, viewModel.redName.value))
                        viewModel.redPoints.value = 0
                        viewModel.greenPoints.value = 0
                        if (viewModel.redWins.value == viewModel.totalGames.value) {
                            viewModel.winner.value = viewModel.redName.value
                        }
                        if (matchId != null && bracketViewModel != null) {
                            bracketViewModel.setMatchWinnerById(matchId, viewModel.redName.value)
                        }
                    }

                    greenWinsGame -> {
                        viewModel.greenWins.value++
                        viewModel.matchHistory.add(MatchResult(viewModel.redPoints.value, viewModel.greenPoints.value, viewModel.greenName.value))
                        viewModel.redPoints.value = 0
                        viewModel.greenPoints.value = 0
                        if (viewModel.greenWins.value == viewModel.totalGames.value) {
                            viewModel.winner.value = viewModel.greenName.value
                        }
                        if (matchId != null && bracketViewModel != null) {
                            bracketViewModel.setMatchWinnerById(matchId, viewModel.greenName.value)
                        }
                    }
                }
            }
        },
        enabled = viewModel.winner.value == null &&
                canEndGame(
                    viewModel.redPoints.value,
                    viewModel.greenPoints.value,
                    viewModel.winningPoints.value,
                    viewModel.suddenDeathEnabled.value
                ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(Icons.Default.SportsTennis, contentDescription = "Tennis", modifier = Modifier.padding(end = 8.dp))
        Text("Finalizar Partido", style = MaterialTheme.typography.labelLarge)
    }
}

fun canEndGame(
    redPoints: Int,
    greenPoints: Int,
    pointsToWin: Int,
    suddenDeathEnabled: Boolean
): Boolean {
    val hasWinner = (redPoints >= pointsToWin || greenPoints >= pointsToWin)
    val diff = kotlin.math.abs(redPoints - greenPoints)

    return if (suddenDeathEnabled && redPoints >= pointsToWin && greenPoints >= pointsToWin) {
        diff >= 2
    } else {
        hasWinner
    }
}