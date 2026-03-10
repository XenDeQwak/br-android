package com.xen.blobgame.ui.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xen.blobgame.data.remote.Status
import com.xen.blobgame.data.remote.serializer.AttackMessage
import com.xen.blobgame.data.remote.serializer.MoveMessage
import com.xen.blobgame.data.remote.serializer.PlayerModel
import com.xen.blobgame.ui.viewmodel.GameStateViewModel
import com.xen.blobgame.ui.viewmodel.PlayerViewModel
import com.xen.blobgame.ui.viewmodel.GameRoomViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.*

private val PLAYER_COLORS = listOf(
    Color(0xFFFF6B6B), // coral-red
    Color(0xFF4ECDC4), // teal
    Color(0xFFFFE66D), // yellow
    Color(0xFF6BCB77), // green
    Color(0xFFA78BFA), // violet
    Color(0xFFFFA552), // orange
    Color(0xFF60A5FA), // blue
    Color(0xFFF472B6), // pink
)

private data class PlayerView(
    val model: PlayerModel,
    val color: Color,
)

@Composable
fun GameScreen(
    gameStateViewModel: GameStateViewModel,
    playerViewModel: PlayerViewModel,
    gameRoomViewModel: GameRoomViewModel,
    onGameDone: () -> Unit,
) {
    val player by playerViewModel.currentPlayer.collectAsState()
    val room   by gameRoomViewModel.currentRoom.collectAsState()
    val gameState by gameStateViewModel.gameState.collectAsState(initial = null)

    val currentPlayerId = player?.id
    val currentRoomId   = room?.id

    if (currentPlayerId == null || currentRoomId == null) {
        Box(
            Modifier.fillMaxSize().background(Color(0xFF0D1117)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    LaunchedEffect(currentRoomId) {
        gameStateViewModel.connect(currentRoomId)
    }
    DisposableEffect(Unit) {
        onDispose { gameStateViewModel.disconnect() }
    }

    val colorMap = remember { mutableMapOf<String, Color>() }

    val players: List<PlayerView> = remember(gameState) {
        println("🔵 GameScreen - gameState: $gameState")
        println("🔵 GameScreen - gameState?.players size: ${gameState?.players?.size}")
        gameState?.players?.map { playerModel ->
            println("📍 Mapping player: ${playerModel.name}")
            val color = colorMap.getOrPut(playerModel.id.toString()) {
                PLAYER_COLORS[colorMap.size % PLAYER_COLORS.size]
            }
            PlayerView(model = playerModel, color = color)
        } ?: emptyList<PlayerView>().also { println("⚠️ Players list is empty or gameState is null") }
    }

    val currentPlayer = players.find { it.model.id == currentPlayerId }
    println("🎮 GameScreen - currentPlayer found: ${currentPlayer?.model?.name}, total players: ${players.size}")

    var joystickDelta by remember { mutableStateOf(Offset.Zero) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(joystickDelta) {
        while (joystickDelta != Offset.Zero) {
            val speed = 2f
            val newX = (currentPlayer?.model?.playerGameState?.posX ?: 0f) + joystickDelta.x * speed
            val newY = (currentPlayer?.model?.playerGameState?.posY ?: 0f) + joystickDelta.y * speed
            gameStateViewModel.move(
                MoveMessage(
                    playerId = currentPlayerId,
                    roomId   = UUID.fromString(currentRoomId),
                    posX     = newX,
                    posY     = newY,
                )
            )
            delay(50L)
        }
    }

    var attackFlash by remember { mutableStateOf(false) }

    // ✅ NEW: Use gameState for real-time updates
    val isHost = player?.id.toString() == gameState?.hostId.toString()
    val gameStarted = gameState?.status == Status.ONGOING
    val isWaiting = gameState?.status == Status.WAITING
    val playersNeeded = (room?.maxPlayers ?: 0) - (gameState?.players?.size ?: 0)

    // ✅ NEW: Show waiting screen
    if (isWaiting) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1117)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Waiting for players...",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Players: ${gameState?.players?.size}/${room?.maxPlayers}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )

                if (isHost && playersNeeded > 0) {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { gameStateViewModel.requestStartGame(UUID.fromString(currentRoomId), currentPlayerId) }
                    ) {
                        Text("Start Game Now")
                    }
                }
            }
        }
        return  // ✅ Early return so game screen doesn't render
    }

    if (gameStarted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1117)),
        ) {
            GameArena(
                players = players,
                modifier = Modifier.fillMaxSize(),
            )

            currentPlayer?.let { me ->
                PlayerHUD(
                    player = me,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                )
            }

            if (currentPlayer?.model?.playerGameState?.isDead == true) {
                DeadOverlay(onGameDone = onGameDone)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Joystick(
                    onDelta = { joystickDelta = it },
                    onRelease = { joystickDelta = Offset.Zero },
                )

                AttackButton(
                    flash = attackFlash,
                    onClick = {
                        val target = players
                            .filter { it.model.id != currentPlayerId && !it.model.playerGameState.isDead }
                            .minByOrNull { p ->
                                val me = currentPlayer?.model?.playerGameState
                                if (me == null) Float.MAX_VALUE
                                else hypot(
                                    (p.model.playerGameState.posX - me.posX).toDouble(),
                                    (p.model.playerGameState.posY - me.posY).toDouble(),
                                ).toFloat()
                            }
                        if (target != null) {
                            coroutineScope.launch {
                                attackFlash = true
                                delay(200)
                                attackFlash = false
                            }
                            gameStateViewModel.attack(
                                AttackMessage(
                                    roomId     = UUID.fromString(currentRoomId),
                                    attackerId = currentPlayerId,
                                    targetId   = target.model.id,
                                )
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun GameArena(
    players: List<PlayerView>,
    modifier: Modifier = Modifier,
) {
    println("🎯 GameArena - rendering ${players.size} players")
    Canvas(modifier = modifier) {
        drawGrid(this)

        players.forEach { pv ->
            println("🎨 Drawing player: ${pv.model.name}")
            val model = pv.model.playerGameState
            val cx = model.posX * (size.width / 100f)
            val cy = model.posY * (size.height / 100f)
            val radius = 30.dp.toPx()

            if (!model.isDead) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(pv.color.copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(cx, cy),
                        radius = radius * 2f,
                    ),
                    radius = radius * 2f,
                    center = Offset(cx, cy),
                )
                drawCircle(color = pv.color, radius = radius, center = Offset(cx, cy))
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3f),
                )
                drawHealthBarAbove(
                    center = Offset(cx, cy),
                    radius = radius,
                    health = model.health,
                    color  = pv.color,
                )
            } else {
                drawCircle(
                    color  = Color.Gray.copy(alpha = 0.3f),
                    radius = radius,
                    center = Offset(cx, cy),
                )
                drawLine(
                    color = Color.Red.copy(alpha = 0.6f),
                    start = Offset(cx - radius * 0.6f, cy - radius * 0.6f),
                    end   = Offset(cx + radius * 0.6f, cy + radius * 0.6f),
                    strokeWidth = 5f,
                    cap   = StrokeCap.Round,
                )
                drawLine(
                    color = Color.Red.copy(alpha = 0.6f),
                    start = Offset(cx + radius * 0.6f, cy - radius * 0.6f),
                    end   = Offset(cx - radius * 0.6f, cy + radius * 0.6f),
                    strokeWidth = 5f,
                    cap   = StrokeCap.Round,
                )
            }
        }
    }
}

private fun drawGrid(scope: DrawScope) {
    val cols = 10
    val rows = 10
    val lineColor = Color.White.copy(alpha = 0.04f)
    for (i in 0..cols) {
        val x = i * scope.size.width / cols
        scope.drawLine(lineColor, Offset(x, 0f), Offset(x, scope.size.height), 1f)
    }
    for (j in 0..rows) {
        val y = j * scope.size.height / rows
        scope.drawLine(lineColor, Offset(0f, y), Offset(scope.size.width, y), 1f)
    }
}

private fun DrawScope.drawHealthBarAbove(
    center: Offset,
    radius: Float,
    health: Int,
    color: Color,
) {
    val barW  = radius * 2.2f
    val barH  = 8f
    val left  = center.x - barW / 2f
    val top   = center.y - radius - 18f
    val fill  = (health.coerceIn(0, 100) / 100f) * barW

    drawRoundRect(
        color        = Color.Black.copy(alpha = 0.5f),
        topLeft      = Offset(left, top),
        size         = Size(barW, barH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
    )
    if (fill > 0f) {
        drawRoundRect(
            color        = color,
            topLeft      = Offset(left, top),
            size         = Size(fill, barH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
        )
    }
}

@Composable
private fun PlayerHUD(player: PlayerView, modifier: Modifier = Modifier) {
    val animatedHealth by animateFloatAsState(
        targetValue    = player.model.playerGameState.health / 100f,
        animationSpec  = tween(durationMillis = 300),
        label          = "healthAnim",
    )

    Column(modifier = modifier) {
        Text(
            text       = "HP",
            color      = Color.White.copy(alpha = 0.7f),
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color.White.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedHealth)
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(player.color, player.color.copy(alpha = 0.7f))
                        )
                    )
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text     = "${player.model.playerGameState.health} / 100",
            color    = Color.White.copy(alpha = 0.55f),
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun Joystick(
    baseSize: Dp    = 120.dp,
    thumbSize: Dp   = 48.dp,
    onDelta: (Offset) -> Unit,
    onRelease: () -> Unit,
) {
    val maxRadius = (baseSize - thumbSize).value / 2f
    var thumbPos by remember { mutableStateOf(Offset.Zero) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(baseSize)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd    = { thumbPos = Offset.Zero; onRelease() },
                    onDragCancel = { thumbPos = Offset.Zero; onRelease() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val raw  = thumbPos + dragAmount
                        val dist = raw.getDistance()
                        thumbPos = if (dist <= maxRadius) raw
                        else raw / dist * maxRadius
                        onDelta(thumbPos / maxRadius)
                    },
                )
            },
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbPos.x.roundToInt(), thumbPos.y.roundToInt()) }
                .size(thumbSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.5f))
                    )
                )
                .shadow(8.dp, CircleShape),
        )
    }
}

@Composable
private fun AttackButton(flash: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue   = if (flash) 0.88f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label         = "attackScale",
    )
    val glowAlpha by animateFloatAsState(
        targetValue   = if (flash) 0.7f else 0.25f,
        animationSpec = tween(120),
        label         = "attackGlow",
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size((90 * scale + 20).dp)
                .clip(CircleShape)
                .background(Color(0xFFFF4444).copy(alpha = glowAlpha)),
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size((90 * scale).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFFF6060), Color(0xFFCC1111))
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onClick() })
                },
        ) {
            Text(
                text       = "ATTACK",
                color      = Color.White,
                fontSize   = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp,
            )
        }
    }
}

@Composable
private fun DeadOverlay(onGameDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500)
        onGameDone()
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("YOU DIED", color = Color(0xFFFF4444), fontSize = 40.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Text("Returning to lobby…", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    }
}