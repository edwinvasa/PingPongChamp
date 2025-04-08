package com.edwinvasa.pingpongchamp.presentation.scoreboard

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.edwinvasa.pingpongchamp.R

data class MatchResult(
    val redPoints: Int,
    val greenPoints: Int,
    val winner: String
)

class ScoreboardViewModel(
    context: Context,
    val isCustomMatch: Boolean,
    initialRedName: String = "Jugador Rojo",
    initialGreenName: String = "Jugador Verde"
) : ViewModel() {
    var suddenDeathEnabled = mutableStateOf(true)
    var showSuddenDeathAnimation = mutableStateOf(false)
    val showConfettiAnimation = mutableStateOf(false)

    var redName = mutableStateOf(initialRedName)
    var greenName = mutableStateOf(initialGreenName)

    var redPoints = mutableStateOf(0)
    var greenPoints = mutableStateOf(0)

    var redWins = mutableStateOf(0)
    var greenWins = mutableStateOf(0)

    var winningPoints = mutableStateOf(5)
    var totalGames = mutableStateOf(2)

    var winner = mutableStateOf<String?>(null)

    var matchHistory = mutableStateListOf<MatchResult>()
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
            Log.d("SaqueDebug", "Red: ${redPoints.value}, Green: ${greenPoints.value}, Winning: ${winningPoints.value}")
            Log.d("SaqueDebug", "SuddenDeathEnabled: ${suddenDeathEnabled.value}, SuddenDeathActive: $isSuddenDeathActive")

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


    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}
