package com.edwinvasa.pingpongchamp.domain.model

data class TournamentMatch(
    val player1: String,
    val player2: String,
    var winner: String? = null
)
