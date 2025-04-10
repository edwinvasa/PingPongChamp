package com.edwinvasa.pingpongchamp.domain.model

import java.util.UUID

data class RoundRobinMatch(
    val id: String = UUID.randomUUID().toString(),
    val player1: String,
    val player2: String,
    var result: MatchPingPongResult? = null,
    val matchHistory: List<MatchPingPongResult> = emptyList()
)
