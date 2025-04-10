package com.edwinvasa.pingpongchamp.domain.model

data class RoundRobinTournament(
    val players: List<String>,
    val matches: List<RoundRobinMatch>
)
