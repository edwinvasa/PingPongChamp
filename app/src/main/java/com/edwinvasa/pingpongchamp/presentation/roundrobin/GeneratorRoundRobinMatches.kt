package com.edwinvasa.pingpongchamp.presentation.roundrobin

import com.edwinvasa.pingpongchamp.domain.model.RoundRobinMatch

fun generateRoundRobinMatches(players: List<String>): List<RoundRobinMatch> {
    val matches = mutableListOf<RoundRobinMatch>()
    for (i in players.indices) {
        for (j in i + 1 until players.size) {
            matches.add(
                RoundRobinMatch(
                    player1 = players[i],
                    player2 = players[j]
                )
            )
        }
    }
    return matches
}
