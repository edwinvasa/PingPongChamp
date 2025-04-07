package com.edwinvasa.pingpongchamp.presentation.main

sealed class Routes(val route: String) {
    object Main : Routes("main")
    object Scoreboard : Routes("scoreboard")
    object Championship : Routes("championship")
    object PlayerRoulette : Routes("player_roulette/{playersJson}") {
        fun createRoute(playersJson: String) = "player_roulette/$playersJson"
    }
    object Bracket : Routes("bracket_screen/{matchesJson}") {
        fun createRoute(matchesJson: String) = "bracket_screen/$matchesJson"
    }
    object ScoreboardWithPlayers : Routes("scoreboard/{player1}/{player2}/{matchId}") {
        fun createRoute(player1: String, player2: String, matchId: String): String {
            return "scoreboard/$player1/$player2/$matchId"
        }
    }
    object Champion : Routes("champion/{championName}") {
        fun createRoute(championName: String) = "champion/$championName"
    }
}
