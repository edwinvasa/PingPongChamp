package com.edwinvasa.pingpongchamp.domain.model

import java.util.UUID

data class TournamentMatch(
    val id: String = UUID.randomUUID().toString(),
    val player1: String,
    val player2: String,
    var winner: String? = null,
    val round: Int = 1,
    var isPlayed: Boolean = false,
    var matchHistory: List<MatchPingPongResult> = emptyList()
)
