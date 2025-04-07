package com.edwinvasa.pingpongchamp.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edwinvasa.pingpongchamp.presentation.bracket.BracketScreen
import com.edwinvasa.pingpongchamp.presentation.championship.ChampionshipScreen
import com.edwinvasa.pingpongchamp.presentation.scoreboard.ScoreboardScreen
import com.edwinvasa.pingpongchamp.presentation.championship.PlayerRouletteScreen
import java.net.URLDecoder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost(navController: NavHostController) {
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
            arguments = listOf(navArgument("playersJson") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("playersJson") ?: "[]"
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
            val type = object : TypeToken<List<String>>() {}.type
            val players: List<String> = Gson().fromJson(decodedJson, type)

            PlayerRouletteScreen(
                navController = navController,
                players = players
            ) { p1, p2 ->
            }
        }
        composable(
            route = Routes.Bracket.route,
            arguments = listOf(navArgument("playersJson") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("playersJson") ?: "[]"
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
            val type = object : TypeToken<List<String>>() {}.type
            val players: List<String> = Gson().fromJson(decodedJson, type)

            BracketScreen(players = players)
        }

    }
}
