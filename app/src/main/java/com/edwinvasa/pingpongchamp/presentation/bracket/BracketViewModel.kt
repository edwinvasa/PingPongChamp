package com.edwinvasa.pingpongchamp.presentation.bracket

import androidx.lifecycle.ViewModel
import com.edwinvasa.pingpongchamp.domain.model.TournamentMatch
import com.edwinvasa.pingpongchamp.domain.model.MatchPingPongResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class BracketViewModel @Inject constructor() : ViewModel() {

    private val _matches = MutableStateFlow<List<TournamentMatch>>(emptyList())
    private val _champion = MutableStateFlow<String?>(null)
    val champion: StateFlow<String?> = _champion
    val matches: StateFlow<List<TournamentMatch>> = _matches

    fun setInitialMatches(matches: List<TournamentMatch>) {
        _matches.value = matches
    }

    fun setMatchWinnerAndHistoryById(matchId: String, winner: String, history: List<MatchPingPongResult>) {
        val updatedMatches = _matches.value.toMutableList()
        val matchIndex = updatedMatches.indexOfFirst { it.id == matchId }

        if (matchIndex != -1) {
            val oldMatch = updatedMatches[matchIndex]
            val updatedMatch = oldMatch.copy(
                winner = winner,
                matchHistory = history,
                isPlayed = true
            )
            updatedMatches[matchIndex] = updatedMatch
            _matches.value = updatedMatches

            checkForChampion()
            checkAndGenerateNextRoundIfNeeded(updatedMatch.round)
        }
    }

    fun setMatchWinnerById(matchId: String, winner: String) {
        val updatedMatches = _matches.value.toMutableList()
        val matchIndex = updatedMatches.indexOfFirst { it.id == matchId }

        if (matchIndex != -1) {
            val oldMatch = updatedMatches[matchIndex]
            val updatedMatch = oldMatch.copy(
                winner = winner,
                isPlayed = true
            )
            updatedMatches[matchIndex] = updatedMatch
            _matches.value = updatedMatches
            checkForChampion()

            checkAndGenerateNextRoundIfNeeded(updatedMatch.round)
        }
    }

    private fun checkAndGenerateNextRoundIfNeeded(currentRound: Int) {
        val currentRoundMatches = _matches.value.filter { it.round == currentRound }

        if (currentRoundMatches.all { it.winner != null }) {
            val nextRoundMatches = generateNextRoundMatches(_matches.value)
            if (nextRoundMatches.isNotEmpty()) {
                _matches.value = _matches.value + nextRoundMatches
            }
        }
    }

    private fun generateNextRoundMatches(
        currentMatches: List<TournamentMatch>
    ): List<TournamentMatch> {
        val lastRound = currentMatches.maxOfOrNull { it.round } ?: 1
        val currentRoundMatches = currentMatches.filter { it.round == lastRound }

        if (currentRoundMatches.any { it.winner == null }) return emptyList()

        val winners = currentRoundMatches.mapNotNull { it.winner }
        val nextRound = lastRound + 1
        val nextRoundMatches = mutableListOf<TournamentMatch>()

        var i = 0
        while (i < winners.size) {
            val player1 = winners[i]
            val player2 = if (i + 1 < winners.size) winners[i + 1] else null

            if (player2 != null) {
                nextRoundMatches.add(
                    TournamentMatch(
                        player1 = player1,
                        player2 = player2,
                        round = nextRound
                    )
                )
                i += 2
            } else {
                nextRoundMatches.add(
                    TournamentMatch(
                        player1 = player1,
                        player2 = "BYE",
                        winner = player1,
                        round = nextRound,
                        isPlayed = true
                    )
                )
                i += 1
            }
        }

        return nextRoundMatches
    }

    fun checkForChampion() {
        val lastRound = _matches.value.maxOfOrNull { it.round } ?: return
        val lastRoundMatches = _matches.value.filter { it.round == lastRound }
        if (lastRoundMatches.size == 1 && lastRoundMatches.first().winner != null) {
            _champion.value = lastRoundMatches.first().winner
        }
    }
}
