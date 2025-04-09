package com.edwinvasa.pingpongchamp.presentation.scoreboard

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.edwinvasa.pingpongchamp.R
import com.edwinvasa.pingpongchamp.presentation.bracket.BracketViewModel

class ScoreboardViewModel(
    context: Context,
    val isCustomMatch: Boolean,
    initialRedName: String = "Jugador 1",
    initialGreenName: String = "Jugador 2"
) : ViewModel() {
    var suddenDeathEnabled = mutableStateOf(true)
    var showSuddenDeathAnimation = mutableStateOf(false)
    val showConfettiAnimation = mutableStateOf(false)
    val redPlayerColor = mutableStateOf(Color(0xFFD32F2F))
    val greenPlayerColor = mutableStateOf(Color(0xFF388E3C))

    val currentSet = mutableStateOf(1)
    val showNewSetMessage = mutableStateOf(false)
    val lastSetWinner = mutableStateOf<String?>(null)

    var redName = mutableStateOf(initialRedName)
    var greenName = mutableStateOf(initialGreenName)

    var redPoints = mutableStateOf(0)
    var greenPoints = mutableStateOf(0)

    var redWins = mutableStateOf(0)
    var greenWins = mutableStateOf(0)

    var winningPoints = mutableStateOf(5)
    var totalGames = mutableStateOf(2)

    var winner = mutableStateOf<String?>(null)

    var matchHistory = mutableStateListOf<MatchPingPongResult>()
    var showHistory = mutableStateOf(true)

    var showServeIndicator = mutableStateOf(false)
    var initialServer = mutableStateOf("rojo")
    var serveChangeFrequency = mutableStateOf(2)

    var showDialog = mutableStateOf(isCustomMatch)
    var suddenDeathAnnounced = mutableStateOf(false)

    val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.victory_sound)

    val isSuddenDeathActive: Boolean
        get() {
            if (!suddenDeathEnabled.value) return false
            val red = redPoints.value
            val green = greenPoints.value
            val maxScore = maxOf(red, green)
            val minScore = minOf(red, green)
            val threshold = winningPoints.value - 1

            return red >= threshold && green >= threshold && (maxScore - minScore) < 2
        }

    val isGameOver: Boolean
        get() = isGameOver(
            redPoints.value,
            greenPoints.value,
            winningPoints.value,
            suddenDeathEnabled.value
        )

    val currentServer: String?
        get() {
            val totalPoints = redPoints.value + greenPoints.value
            if (!showServeIndicator.value) return null

            return getCurrentServer(
                totalPoints = totalPoints,
                initialServer = initialServer.value,
                serveChangeFrequency = serveChangeFrequency.value,
                isSuddenDeath = isSuddenDeathActive
            )
        }

    fun resetMatch() {
        redPoints.value = 0
        greenPoints.value = 0
        redWins.value = 0
        greenWins.value = 0
        winner.value = null
        currentSet.value = 0
        showNewSetMessage.value = false
        lastSetWinner.value = null
    }

    fun clearHistory() {
        matchHistory.clear()
    }

    fun shouldTriggerSuddenDeathEvent(): Boolean {
        val isNowInSuddenDeath = isSuddenDeathActive
        val shouldAnnounce = isNowInSuddenDeath && redPoints.value == greenPoints.value && !suddenDeathAnnounced.value

        if (shouldAnnounce) {
            suddenDeathAnnounced.value = true
            return true
        }

        if (!isNowInSuddenDeath || redPoints.value != greenPoints.value) {
            suddenDeathAnnounced.value = false
        }

        return false
    }

    fun tryEndGameAutomatically(matchId: String?, bracketViewModel: BracketViewModel?) {
        if (!isGameOver) {
            return
        }

        if (!canEndGame(
                redPoints.value,
                greenPoints.value,
                winningPoints.value,
                suddenDeathEnabled.value
            )
        ) {
            return
        }

        val redWinsGame = redPoints.value > greenPoints.value
        val greenWinsGame = greenPoints.value > redPoints.value

        when {
            redWinsGame -> {
                redWins.value++
                matchHistory.add(MatchPingPongResult(redPoints.value, greenPoints.value, redName.value))
                redPoints.value = 0
                greenPoints.value = 0
                if (redWins.value == totalGames.value) {
                    winner.value = redName.value
                }else {
                    currentSet.value += 1
                    showNewSetMessage.value = true
                    lastSetWinner.value = redName.value
                }
                matchId?.let {
                    bracketViewModel?.setMatchWinnerAndHistoryById(it, redName.value, matchHistory.toList())
                }
            }

            greenWinsGame -> {
                greenWins.value++
                matchHistory.add(MatchPingPongResult(redPoints.value, greenPoints.value, greenName.value))
                redPoints.value = 0
                greenPoints.value = 0
                if (greenWins.value == totalGames.value) {
                    winner.value = greenName.value
                } else {
                    currentSet.value += 1
                    showNewSetMessage.value = true
                    lastSetWinner.value = greenName.value
                }
                matchId?.let {
                    bracketViewModel?.setMatchWinnerAndHistoryById(it, greenName.value, matchHistory.toList())
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}
