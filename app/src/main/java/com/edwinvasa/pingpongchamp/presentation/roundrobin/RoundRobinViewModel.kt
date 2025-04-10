package com.edwinvasa.pingpongchamp.presentation.roundrobin

import androidx.lifecycle.ViewModel
import com.edwinvasa.pingpongchamp.domain.model.MatchPingPongResult
import com.edwinvasa.pingpongchamp.domain.model.RoundRobinTournament
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RoundRobinViewModel @Inject constructor() : ViewModel() {

    private val _tournament = MutableStateFlow<RoundRobinTournament?>(null)
    val tournament: StateFlow<RoundRobinTournament?> = _tournament

    private val _champion = MutableStateFlow<String?>(null)
    val champion: StateFlow<String?> = _champion

    fun startTournament(players: List<String>) {
        val matches = generateRoundRobinMatches(players)
        _tournament.value = RoundRobinTournament(players, matches)
    }

    fun setMatchWinnerAndHistoryById(matchId: String, winner: String, history: List<MatchPingPongResult>) {

        _tournament.value = _tournament.value?.let { current ->
            val updatedMatches = current.matches.map {
                if (it.id == matchId) {
                    it.copy(
                        result = history.lastOrNull(),
                        matchHistory = history
                    )
                } else it
            }
            current.copy(matches = updatedMatches)
        }
    }

    data class PlayerStats(
        val player: String,
        val wins: Int,
        val losses: Int,
        val pointsFor: Int,
        val pointsAgainst: Int,
        val setsWon: Int,
        val setsPlayed: Int
    )

    fun getDetailedStandings(): List<PlayerStats> {
        val currentTournament = tournament.value ?: return emptyList()
        val statsMap = mutableMapOf<String, PlayerStats>()

        currentTournament.players.forEach { player ->
            statsMap[player] = PlayerStats(player, 0, 0, 0, 0, 0, 0)
        }

        currentTournament.matches.forEach { match ->
            if (match.matchHistory.isNotEmpty()) {
                val player1 = match.player1
                val player2 = match.player2
                val player1Stats = statsMap[player1]!!
                val player2Stats = statsMap[player2]!!

                var player1Points = 0
                var player2Points = 0
                var player1SetsWon = 0
                var player2SetsWon = 0
                var setsPlayed = 0

                match.matchHistory.forEach { set ->
                    player1Points += set.redPoints
                    player2Points += set.greenPoints
                    setsPlayed++
                    if (set.winner == player1) player1SetsWon++
                    if (set.winner == player2) player2SetsWon++
                }

                val winner = match.result?.winner
                statsMap[player1] = player1Stats.copy(
                    wins = player1Stats.wins + if (winner == player1) 1 else 0,
                    losses = player1Stats.losses + if (winner == player2) 1 else 0,
                    pointsFor = player1Stats.pointsFor + player1Points,
                    pointsAgainst = player1Stats.pointsAgainst + player2Points,
                    setsWon = player1Stats.setsWon + player1SetsWon,
                    setsPlayed = player1Stats.setsPlayed + setsPlayed
                )

                statsMap[player2] = player2Stats.copy(
                    wins = player2Stats.wins + if (winner == player2) 1 else 0,
                    losses = player2Stats.losses + if (winner == player1) 1 else 0,
                    pointsFor = player2Stats.pointsFor + player2Points,
                    pointsAgainst = player2Stats.pointsAgainst + player1Points,
                    setsWon = player2Stats.setsWon + player2SetsWon,
                    setsPlayed = player2Stats.setsPlayed + setsPlayed
                )
            }
        }

        return statsMap.values
            .sortedWith(compareByDescending<PlayerStats> { it.wins }
                .thenByDescending { it.setsWon }
                .thenByDescending { it.pointsFor - it.pointsAgainst })
    }

    private fun checkChampion() {
        val standings = getDetailedStandings()
        val first = standings.firstOrNull()
        if (first != null && allMatchesPlayed()) {
            _champion.value = first.player
        }
    }

    private fun allMatchesPlayed(): Boolean {
        return tournament.value?.matches?.all { it.result != null } == true
    }
}