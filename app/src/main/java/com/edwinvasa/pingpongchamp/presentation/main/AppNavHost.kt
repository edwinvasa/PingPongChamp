package com.edwinvasa.pingpongchamp.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edwinvasa.pingpongchamp.domain.model.TournamentMatch
import com.edwinvasa.pingpongchamp.presentation.bracket.BracketScreen
import com.edwinvasa.pingpongchamp.presentation.championship.ChampionScreen
import com.edwinvasa.pingpongchamp.presentation.championship.ChampionshipScreen
import com.edwinvasa.pingpongchamp.presentation.scoreboard.ScoreboardScreen
import com.edwinvasa.pingpongchamp.presentation.championship.PlayerRouletteScreen
import com.edwinvasa.pingpongchamp.presentation.roundrobin.RoundRobinScreen
import java.net.URLDecoder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost(navController: NavHostController) {

    fun decodePlayersJson(json: String): List<String> {
        val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(decodedJson, type)
    }

    fun decodeMatchesJson(json: String): List<TournamentMatch> {
        val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
        val type = object : TypeToken<List<TournamentMatch>>() {}.type
        return Gson().fromJson(decodedJson, type)
    }

    NavHost(navController = navController, startDestination = Routes.Main.route) {
        composable(Routes.Main.route) {
            MainScreen(
                onScoreboardClick = {
                    navController.navigate(Routes.Scoreboard.route)
                },
                onChampionshipClick = {
                    navController.navigate(Routes.Championship.route)
                }
            )
        }
        composable(Routes.Scoreboard.route) {
            ScoreboardScreen()
        }
        composable(Routes.Championship.route) {
            ChampionshipScreen(navController = navController)
        }
        composable(
            route = Routes.PlayerRoulette.route,
            arguments = listOf(navArgument("playersJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("playersJson") ?: "[]"
            val players = decodePlayersJson(json)

            PlayerRouletteScreen(
                navController = navController,
                players = players,
                onAllMatchesGenerated = {}
            )
        }
        composable(
            route = Routes.Bracket.route,
            arguments = listOf(navArgument("matchesJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("matchesJson") ?: "[]"
            val matches = decodeMatchesJson(json)

            BracketScreen(
                navController = navController,
                initialMatches = matches
            )
        }
        composable(
            route = Routes.ScoreboardWithPlayers.route,
            arguments = listOf(
                navArgument("player1") { type = NavType.StringType },
                navArgument("player2") { type = NavType.StringType },
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val player1 = backStackEntry.arguments?.getString("player1") ?: ""
            val player2 = backStackEntry.arguments?.getString("player2") ?: ""
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""

            ScoreboardScreen(
                navController = navController,
                player1 = player1,
                player2 = player2,
                matchId = matchId,
                shouldReturnAfterMatch = true,
                callerRoute = Routes.Bracket.route
            )
        }
        composable(
            route = Routes.Champion.route,
            arguments = listOf(navArgument("championName") { type = NavType.StringType })
        ) { backStackEntry ->
            val championName = backStackEntry.arguments?.getString("championName") ?: "Desconocido"
            ChampionScreen(championName) {
                navController.popBackStack(Routes.Main.route, inclusive = false)
            }
        }
        composable(
            route = Routes.RoundRobin.route,
            arguments = listOf(navArgument("playersJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("playersJson") ?: "[]"
            val players = decodePlayersJson(json)

            RoundRobinScreen(
                navController = navController,
                players = players
            )
        }
        composable(
            route = Routes.RoundRobinScoreboard.route,
            arguments = listOf(
                navArgument("player1") { type = NavType.StringType },
                navArgument("player2") { type = NavType.StringType },
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val player1 = backStackEntry.arguments?.getString("player1") ?: ""
            val player2 = backStackEntry.arguments?.getString("player2") ?: ""
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""

            ScoreboardScreen(
                navController = navController,
                player1 = player1,
                player2 = player2,
                matchId = matchId,
                shouldReturnAfterMatch = true,
                callerRoute = Routes.RoundRobin.route
            )
        }
    }
}
