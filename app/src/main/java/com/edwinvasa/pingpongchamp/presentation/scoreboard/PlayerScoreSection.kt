package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerScoreSection(
    name: String,
    score: Int,
    wins: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    isServing: Boolean = false,
    enabled: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        ScoreRow(
            name = name,
            score = score,
            onPlus = onPlus,
            onMinus = onMinus,
            isServing = isServing,
            enabled = enabled
        )
        Text("ganados: $wins")
    }
}

@Composable
fun ScoreRow(
    name: String,
    score: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    isServing: Boolean = false,
    enabled: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (isServing) 1.2f else 1f,
        label = "ServeIndicatorScale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = if (isServing) "🏓 $name" else name,
            fontSize = 20.sp,
            color = LocalContentColor.current,
            modifier = Modifier.scale(scale)
        )
        IconButton(onClick = onMinus, enabled = enabled) { Text("-") }
        Text("$score", fontSize = 24.sp)
        IconButton(onClick = onPlus, enabled = enabled) { Text("+") }
    }
}