package com.edwinvasa.pingpongchamp.presentation.scoreboard

import android.util.Log

fun isGameOver(
    redPoints: Int,
    greenPoints: Int,
    winningPoints: Int,
    suddenDeathEnabled: Boolean
): Boolean {
    return if (suddenDeathEnabled) {
        val hasRequiredPoints = redPoints >= winningPoints || greenPoints >= winningPoints
        val hasTwoPointLead = kotlin.math.abs(redPoints - greenPoints) >= 2
        hasRequiredPoints && hasTwoPointLead
    } else {
        redPoints >= winningPoints || greenPoints >= winningPoints
    }
}

fun getCurrentServer(
    totalPoints: Int,
    initialServer: String,
    serveChangeFrequency: Int,
    isSuddenDeath: Boolean
): String {
    Log.d("SaqueDebug", "== getCurrentServer Start==")
    Log.d("SaqueDebug", "Puntos totales: $totalPoints")
    Log.d("SaqueDebug", "Servidor inicial: $initialServer")
    Log.d("SaqueDebug", "Frecuencia de saque: $serveChangeFrequency")
    Log.d("SaqueDebug", "¿Muerte súbita?: $isSuddenDeath")

    val interval = if (isSuddenDeath) 1 else serveChangeFrequency
    val serverIndex = (totalPoints / interval) % 2

    val result = if (initialServer == "rojo") {
        if (serverIndex == 0) "rojo" else "verde"
    } else {
        if (serverIndex == 0) "verde" else "rojo"
    }

    Log.d("SaqueDebug", "Índice de servidor: $serverIndex")
    Log.d("SaqueDebug", "Servidor actual: $result")
    Log.d("SaqueDebug", "== getCurrentServer Finished==")
    return result
}
