package com.edwinvasa.pingpongchamp.presentation.championship

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.airbnb.lottie.compose.*
import com.edwinvasa.pingpongchamp.R
import androidx.compose.ui.platform.LocalContext

@Composable
fun ChampionScreen(
    championName: String,
    onBackToHome: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("animations/confetti.json"))
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.victory_sound) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1.0f
    )
    mediaPlayer.start()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🎉 ¡Tenemos un campeón! 🎉", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Text("🏆 $championName 🏆", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF388E3C))
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onBackToHome) {
                Text("Volver al inicio")
            }
        }
    }
}

