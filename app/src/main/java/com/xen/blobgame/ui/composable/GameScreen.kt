package com.xen.blobgame.ui.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.xen.blobgame.ui.viewmodel.GameSessionViewModel

@Composable
fun GameScreen(
    viewModel: GameSessionViewModel
) {
    val players by viewModel.players.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            players
                .filter { it.isAlive }
                .forEach { player ->
                    drawCircle(
                        color = player.color,
                        radius = 40f,
                        center = Offset(player.x, player.y)
                    )
                }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Joystick(
                onMove = { dx, dy ->
                    viewModel.move(dx, dy)
                }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.attack() }
            ) {
                Text("Attack")
            }
        }
    }
}