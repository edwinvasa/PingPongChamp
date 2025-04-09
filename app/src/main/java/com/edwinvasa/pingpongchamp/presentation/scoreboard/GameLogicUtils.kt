package com.edwinvasa.pingpongchamp.presentation.scoreboard

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
    val interval = if (isSuddenDeath) 1 else serveChangeFrequency
    val serverIndex = (totalPoints / interval) % 2

    return if (initialServer == "rojo") {
        if (serverIndex == 0) "rojo" else "verde"
    } else {
        if (serverIndex == 0) "verde" else "rojo"
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
