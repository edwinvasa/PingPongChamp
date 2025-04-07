package com.edwinvasa.pingpongchamp.presentation.main

sealed class Routes(val route: String) {
    object Main : Routes("main")
    object Scoreboard : Routes("scoreboard")
    object Championship : Routes("championship")
    object PlayerRoulette : Routes("player_roulette/{playersJson}") {
        fun createRoute(playersJson: String) = "player_roulette/$playersJson"
    }
    object Bracket : Routes("bracket_screen/{playersJson}") {
        fun createRoute(playersJson: String) = "bracket_screen/$playersJson"
    }
}
