package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.PlayerColor
import com.example.game.SixPlayerLudoEngine
import com.example.game.SixPlayerState
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SixPlayerLudoScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var gameState by remember { mutableStateOf(SixPlayerState()) }
    var isRollingAnim by remember { mutableStateOf(false) }
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val diceRotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val performMove: (Int) -> Unit = { tokenId ->
        val nextState = SixPlayerLudoEngine.moveToken(gameState, tokenId)
        gameState = nextState
        if (nextState.winner != null) {
            if (nextState.winner == PlayerColor.RED) {
                viewModel.soundManager.playVictory()
            } else {
                viewModel.soundManager.playLoss()
            }
        } else {
            viewModel.soundManager.playTokenStep()
        }
    }

    // Bot AI loop
    LaunchedEffect(gameState.currentTurnIndex, gameState.hasRolled, gameState.winner) {
        val currentP = gameState.currentPlayer
        if (gameState.winner == null && currentP.isBot) {
            if (!gameState.hasRolled) {
                delay(800)
                isRollingAnim = true
                viewModel.soundManager.playDiceRoll()
                diceRotation.animateTo(
                    targetValue = diceRotation.value + 360f,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                )
                val rolled = SixPlayerLudoEngine.rollDice()
                val selectable = SixPlayerLudoEngine.calculateSelectableTokens(
                    gameState.tokens[currentP.color] ?: emptyList(),
                    rolled
                )
                isRollingAnim = false

                if (selectable.isEmpty()) {
                    // No moves available, pass turn
                    delay(500)
                    val nextIndex = (gameState.currentTurnIndex + 1) % gameState.players.size
                    gameState = gameState.copy(
                        currentTurnIndex = nextIndex,
                        currentDiceValue = rolled,
                        eventMessage = "${currentP.name} $rolled ফেলেছে কিন্তু চালার মতো গুটি নেই!"
                    )
                } else {
                    // Pick the best token to move
                    gameState = gameState.copy(
                        currentDiceValue = rolled,
                        hasRolled = true,
                        selectableTokenIds = selectable,
                        eventMessage = "${currentP.name} $rolled ফেলেছে।"
                    )
                    delay(600)
                    val chosenTokenId = selectable.first()
                    performMove(chosenTokenId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⭐ ৬-প্লেয়ার লুডু (6-Player Mode)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleSound() },
                        modifier = Modifier.testTag("six_player_sound_toggle")
                    ) {
                        Icon(
                            imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (isSoundEnabled) "Mute Sound" else "Unmute Sound",
                            tint = if (isSoundEnabled) Color(0xFF00E676) else Color(0xFFEF4444)
                        )
                    }
                    IconButton(onClick = { gameState = SixPlayerState() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF090D16)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Event Notification Banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = gameState.eventMessage,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp)
                )
            }

            // Hexagonal 6-Player Graphical Board View
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A))
                    .border(2.5.dp, Color(0xFFFFD700), RoundedCornerShape(20.dp))
            ) {
                val sizePx = constraints.maxWidth.toFloat()
                val center = Offset(sizePx / 2f, sizePx / 2f)
                val radius = sizePx * 0.44f

                // Draw 6-sided multi-track hexagonal board
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val colors = listOf(
                        Color(0xFFE53935), // Red
                        Color(0xFF8E24AA), // Purple
                        Color(0xFF1E88E5), // Blue
                        Color(0xFFFDD835), // Yellow
                        Color(0xFFFB8C00), // Orange
                        Color(0xFF43A047)  // Green
                    )

                    // Draw 6 triangular radial home sectors
                    for (i in 0 until 6) {
                        val angle1 = Math.toRadians((i * 60.0) - 30.0)
                        val angle2 = Math.toRadians(((i + 1) * 60.0) - 30.0)
                        val p1 = Offset((center.x + radius * cos(angle1)).toFloat(), (center.y + radius * sin(angle1)).toFloat())
                        val p2 = Offset((center.x + radius * cos(angle2)).toFloat(), (center.y + radius * sin(angle2)).toFloat())

                        val path = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            close()
                        }
                        drawPath(path, color = colors[i].copy(alpha = 0.25f))
                    }

                    // Center Finish Hub Hexagon
                    drawCircle(
                        color = Color(0xFF090D16),
                        radius = sizePx * 0.14f,
                        center = center
                    )
                    drawCircle(
                        brush = Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFD97706))),
                        radius = sizePx * 0.12f,
                        center = center
                    )
                }

                // 6 Yard Bases with interactive Tokens
                val playerOrder = listOf(
                    PlayerColor.RED, PlayerColor.PURPLE, PlayerColor.BLUE,
                    PlayerColor.YELLOW, PlayerColor.ORANGE, PlayerColor.GREEN
                )

                playerOrder.forEachIndexed { index, color ->
                    val angle = Math.toRadians((index * 60.0) - 30.0 + 30.0)
                    val yardDist = radius * 0.72f
                    val yardX = (center.x + yardDist * cos(angle)).toFloat()
                    val yardY = (center.y + yardDist * sin(angle)).toFloat()

                    val pTokens = gameState.tokens[color] ?: emptyList()
                    val isCurrentTurn = gameState.currentPlayer.color == color

                    // Yard Box
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (yardX / (sizePx / maxWidth.value) - 26).dp,
                                y = (yardY / (sizePx / maxHeight.value) - 26).dp
                            )
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(color.darkColorHex))
                            .border(
                                width = if (isCurrentTurn) 2.dp else 1.dp,
                                color = if (isCurrentTurn) Color.White else Color(color.lightColorHex),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // 4 Tokens in the Yard
                        Column(
                            verticalArrangement = Arrangement.SpaceAround,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(horizontalArrangement = Arrangement.SpaceAround) {
                                TokenDot(
                                    token = pTokens.getOrNull(0),
                                    isSelectable = gameState.selectableTokenIds.contains(0) && isCurrentTurn,
                                    onClick = {
                                        if (gameState.currentPlayer.isLocalHuman && gameState.hasRolled && gameState.selectableTokenIds.contains(0)) {
                                            performMove(0)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                TokenDot(
                                    token = pTokens.getOrNull(1),
                                    isSelectable = gameState.selectableTokenIds.contains(1) && isCurrentTurn,
                                    onClick = {
                                        if (gameState.currentPlayer.isLocalHuman && gameState.hasRolled && gameState.selectableTokenIds.contains(1)) {
                                            performMove(1)
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(horizontalArrangement = Arrangement.SpaceAround) {
                                TokenDot(
                                    token = pTokens.getOrNull(2),
                                    isSelectable = gameState.selectableTokenIds.contains(2) && isCurrentTurn,
                                    onClick = {
                                        if (gameState.currentPlayer.isLocalHuman && gameState.hasRolled && gameState.selectableTokenIds.contains(2)) {
                                            performMove(2)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                TokenDot(
                                    token = pTokens.getOrNull(3),
                                    isSelectable = gameState.selectableTokenIds.contains(3) && isCurrentTurn,
                                    onClick = {
                                        if (gameState.currentPlayer.isLocalHuman && gameState.hasRolled && gameState.selectableTokenIds.contains(3)) {
                                            performMove(3)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Center Trophy / Dice in center hub
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "👑", fontSize = 24.sp)
                        Text(
                            text = if (gameState.currentDiceValue != null) "${gameState.currentDiceValue}" else "৬-লুডু",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 6 Players Mini-Cards Grid (2 rows of 3)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                itemsIndexed(gameState.players) { index, player ->
                    val isTurn = index == gameState.currentTurnIndex % gameState.players.size
                    val pColor = Color(player.color.baseColorHex)
                    val tokensAtHome = gameState.tokens[player.color]?.count { it.stepCount >= SixPlayerLudoEngine.MAX_STEPS } ?: 0

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isTurn) 2.dp else 0.5.dp,
                            color = if (isTurn) pColor else Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = player.countryFlag, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Column {
                                Text(
                                    text = player.name.take(6),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = if (isTurn) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                                Text(
                                    text = "ঘরে: $tokensAtHome/4",
                                    color = Color(0xFFFFD700),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Human Roll Controller
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentP = gameState.currentPlayer
                    val isHumanTurn = currentP.isLocalHuman && gameState.winner == null

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentP.countryFlag, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = currentP.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isHumanTurn) "আপনার চাল! ডাইস রোল করুন" else "${currentP.name} খেলছেন...",
                                color = Color(0xFFFFD700),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // 3D Animated Roll Dice
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(50.dp)
                            .rotate(diceRotation.value)
                            .clickable(enabled = isHumanTurn && !gameState.hasRolled && !isRollingAnim) {
                                scope.launch {
                                    isRollingAnim = true
                                    viewModel.soundManager.playDiceRoll()
                                    diceRotation.animateTo(
                                        targetValue = diceRotation.value + 360f,
                                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                                    )
                                    val roll = SixPlayerLudoEngine.rollDice()
                                    val selectable = SixPlayerLudoEngine.calculateSelectableTokens(
                                        gameState.tokens[currentP.color] ?: emptyList(),
                                        roll
                                    )
                                    isRollingAnim = false

                                    if (selectable.isEmpty()) {
                                        val nextIndex = (gameState.currentTurnIndex + 1) % gameState.players.size
                                        gameState = gameState.copy(
                                            currentTurnIndex = nextIndex,
                                            currentDiceValue = roll,
                                            eventMessage = "আপনি $roll ফেলেছেন কিন্তু চালার মতো গুটি নেই!"
                                        )
                                    } else {
                                        gameState = gameState.copy(
                                            currentDiceValue = roll,
                                            hasRolled = true,
                                            selectableTokenIds = selectable,
                                            eventMessage = "আপনি $roll ফেলেছেন! গুটি স্পর্শ করে চালুন।"
                                        )
                                    }
                                }
                            }
                            .testTag("six_player_dice_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isRollingAnim) "🎲" else (gameState.currentDiceValue?.toString() ?: "🎲"),
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TokenDot(
    token: com.example.game.SixPlayerToken?,
    isSelectable: Boolean,
    onClick: () -> Unit
) {
    if (token == null) return
    val baseColor = Color(token.color.baseColorHex)

    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelectable) 1.25f else 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(16.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(baseColor)
            .border(
                width = if (isSelectable) 2.dp else 1.dp,
                color = if (isSelectable) Color(0xFFFFD700) else Color.White,
                shape = CircleShape
            )
            .clickable(enabled = isSelectable, onClick = onClick)
    )
}
