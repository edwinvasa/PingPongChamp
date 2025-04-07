package com.edwinvasa.pingpongchamp.presentation.bracket

import androidx.lifecycle.ViewModel
import com.edwinvasa.pingpongchamp.domain.model.TournamentMatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BracketViewModel : ViewModel() {

    private val _matches = MutableStateFlow<List<TournamentMatch>>(emptyList())
    val matches: StateFlow<List<TournamentMatch>> = _matches

    fun generateInitialMatches(players: List<String>) {
        val shuffled = players.shuffled()
        val pairs = shuffled.chunked(2)
        val initialMatches = pairs.map { pair ->
            TournamentMatch(
                player1 = pair.getOrNull(0) ?: "",
                player2 = pair.getOrNull(1) ?: ""
            )
        }
        _matches.value = initialMatches
    }

    fun setMatchWinner(match: TournamentMatch, winner: String) {
        val updated = _matches.value.map {
            if (it == match) it.copy(winner = winner) else it
        }
        _matches.value = updated
    }
}
