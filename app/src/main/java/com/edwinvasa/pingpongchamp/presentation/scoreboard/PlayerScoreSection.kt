package com.edwinvasa.pingpongchamp.presentation.scoreboard

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    isLeftPlayer: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.Unspecified,
    showWinsTopRight: Boolean = false,
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColor
    ) {
        Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = if (isServing) "🏓 $name" else name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(46.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = onMinus, enabled = enabled) { Text("-") }
                    Text(
                        "$score",
                        fontSize = 105.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(onClick = onPlus, enabled = enabled) { Text("+") }
                }
            }

            if (showWinsTopRight) {
                Text(
                    text = "$wins",
                    fontSize = 75.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "$wins",
                    fontSize = 75.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(if (isLeftPlayer) Alignment.BottomEnd else Alignment.BottomStart)
                        .padding(8.dp)
                )
            }
        }
    }
}