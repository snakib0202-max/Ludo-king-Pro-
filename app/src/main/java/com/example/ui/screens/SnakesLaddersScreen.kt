package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.PlayerColor
import com.example.game.SnakesLaddersEngine
import com.example.game.SnakesLaddersState
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnakesLaddersScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var gameState by remember { mutableStateOf(SnakesLaddersState()) }
    var isRollingAnim by remember { mutableStateOf(false) }
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val diceRotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val executeRoll: () -> Unit = {
        scope.launch {
            isRollingAnim = true
            viewModel.soundManager.playDiceRoll()
            diceRotation.animateTo(
                targetValue = diceRotation.value + 360f,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            )
            val roll = SnakesLaddersEngine.rollDice()
            val nextState = SnakesLaddersEngine.handleRoll(gameState, roll)
            gameState = nextState
            isRollingAnim = false

            // Sound feedback for move and win/loss
            if (nextState.winner != null) {
                if (nextState.winner == PlayerColor.RED) {
                    viewModel.soundManager.playVictory()
                } else {
                    viewModel.soundManager.playLoss()
                }
            } else if (nextState.isSnakeBite) {
                viewModel.soundManager.playSnakeBite()
            } else if (nextState.isLadderClimb) {
                viewModel.soundManager.playLadderClimb()
            } else {
                viewModel.soundManager.playTokenStep()
            }
        }
    }

    // Auto-roll for bot players
    LaunchedEffect(gameState.currentTurnIndex, gameState.winner) {
        if (gameState.winner == null && gameState.currentPlayer.isBot) {
            delay(1000)
            isRollingAnim = true
            viewModel.soundManager.playDiceRoll()
            diceRotation.animateTo(
                targetValue = diceRotation.value + 360f,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            )
            val roll = SnakesLaddersEngine.rollDice()
            val nextState = SnakesLaddersEngine.handleRoll(gameState, roll)
            gameState = nextState
            isRollingAnim = false

            if (nextState.winner != null) {
                if (nextState.winner == PlayerColor.RED) {
                    viewModel.soundManager.playVictory()
                } else {
                    viewModel.soundManager.playLoss()
                }
            } else if (nextState.isSnakeBite) {
                viewModel.soundManager.playSnakeBite()
            } else if (nextState.isLadderClimb) {
                viewModel.soundManager.playLadderClimb()
            } else {
                viewModel.soundManager.playTokenStep()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🐍 সাপ-লুডু (Snakes & Ladders)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
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
                        modifier = Modifier.testTag("snakes_sound_toggle")
                    ) {
                        Icon(
                            imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (isSoundEnabled) "Mute Sound" else "Unmute Sound",
                            tint = if (isSoundEnabled) Color(0xFF00E676) else Color(0xFFEF4444)
                        )
                    }
                    IconButton(onClick = { gameState = SnakesLaddersState() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Restart",
                            tint = Color.White
                        )
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
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Event Notification Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when {
                    gameState.isSnakeBite -> Color(0xFF7F1D1D)
                    gameState.isLadderClimb -> Color(0xFF14532D)
                    gameState.winner != null -> Color(0xFF78350F)
                    else -> Color(0xFF1E293B)
                },
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = gameState.eventMessageBn,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                )
            }

            // 10x10 Snakes & Ladders Board with Canvas overlay for snakes & ladders
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A2E))
                    .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
            ) {
                val boardSizePx = constraints.maxWidth.toFloat()
                val cellSize = boardSizePx / 10f

                // Draw background grid tiles
                Canvas(modifier = Modifier.fillMaxSize()) {
                    for (row in 0..9) {
                        for (col in 0..9) {
                            val isEven = (row + col) % 2 == 0
                            val tileColor = if (isEven) Color(0xFFFDE68A) else Color(0xFFFBCFE8)
                            drawRect(
                                color = tileColor,
                                topLeft = Offset(col * cellSize, row * cellSize),
                                size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                            )
                        }
                    }

                    // Draw Ladders (Gold/Wood styled lines with rungs)
                    SnakesLaddersEngine.LADDERS.forEach { (bottom, top) ->
                        val (bRow, bCol) = SnakesLaddersEngine.cellToRowCol(bottom)
                        val (tRow, tCol) = SnakesLaddersEngine.cellToRowCol(top)
                        val start = Offset((bCol + 0.5f) * cellSize, (bRow + 0.5f) * cellSize)
                        val end = Offset((tCol + 0.5f) * cellSize, (tRow + 0.5f) * cellSize)

                        // Main ladder sides
                        drawLine(
                            color = Color(0xFF16A34A),
                            start = start,
                            end = end,
                            strokeWidth = 6f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw Snakes (Red/Orange curved wavy lines)
                    SnakesLaddersEngine.SNAKES.forEach { (head, tail) ->
                        val (hRow, hCol) = SnakesLaddersEngine.cellToRowCol(head)
                        val (tRow, tCol) = SnakesLaddersEngine.cellToRowCol(tail)
                        val start = Offset((hCol + 0.5f) * cellSize, (hRow + 0.5f) * cellSize)
                        val end = Offset((tCol + 0.5f) * cellSize, (tRow + 0.5f) * cellSize)

                        val path = Path().apply {
                            moveTo(start.x, start.y)
                            val midX = (start.x + end.x) / 2 + 20f
                            val midY = (start.y + end.y) / 2 - 20f
                            quadraticTo(midX, midY, end.x, end.y)
                        }

                        drawPath(
                            path = path,
                            color = Color(0xFFDC2626),
                            style = Stroke(width = 8f, cap = StrokeCap.Round)
                        )

                        // Snake Head circle
                        drawCircle(
                            color = Color(0xFF991B1B),
                            radius = 9f,
                            center = start
                        )
                    }
                }

                // Draw numbers 1 to 100 overlay
                for (cellNum in 1..100) {
                    val (row, col) = SnakesLaddersEngine.cellToRowCol(cellNum)
                    val xOffset = (col * (maxWidth.value / 10f)).dp
                    val yOffset = (row * (maxHeight.value / 10f)).dp

                    Box(
                        modifier = Modifier
                            .offset(x = xOffset, y = yOffset)
                            .size((maxWidth.value / 10f).dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            text = "$cellNum",
                            color = Color(0xFF475569),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(1.dp)
                        )
                    }
                }

                // Render Player Tokens on the board
                gameState.players.forEach { p ->
                    val pos = gameState.positions[p.color] ?: 1
                    val (row, col) = SnakesLaddersEngine.cellToRowCol(pos)
                    val xOffset = (col * (maxWidth.value / 10f) + (p.color.ordinal % 2) * 8).dp
                    val yOffset = (row * (maxHeight.value / 10f) + (p.color.ordinal / 2) * 8).dp

                    Box(
                        modifier = Modifier
                            .offset(x = xOffset, y = yOffset)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(p.color.baseColorHex))
                            .border(1.5.dp, Color.White, CircleShape)
                            .shadow(4.dp, CircleShape)
                    )
                }
            }

            // Bottom Players and Roll Dice Panel
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Players Status row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        gameState.players.forEachIndexed { index, p ->
                            val isTurn = index == gameState.currentTurnIndex % gameState.players.size
                            val pos = gameState.positions[p.color] ?: 1
                            val pColor = Color(p.color.baseColorHex)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isTurn) Color(0xFF334155) else Color.Transparent)
                                    .border(
                                        width = if (isTurn) 1.5.dp else 0.dp,
                                        color = if (isTurn) pColor else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = p.countryFlag, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(pColor)
                                    )
                                }
                                Text(
                                    text = p.name.take(6),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = if (isTurn) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "ঘর: $pos",
                                    color = Color(0xFFFFD700),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dice Roll Button & Current Value
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentP = gameState.currentPlayer
                        val isHumanTurn = !currentP.isBot && gameState.winner == null

                        // Animated Dice Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .size(54.dp)
                                .rotate(diceRotation.value)
                                .clickable(enabled = isHumanTurn && !isRollingAnim) {
                                    executeRoll()
                                }
                                .testTag("snakes_ladders_dice_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isRollingAnim) "🎲" else (gameState.currentDiceValue?.toString() ?: "🎲"),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { executeRoll() },
                            enabled = isHumanTurn && !isRollingAnim,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("snakes_roll_action_btn")
                        ) {
                            Text(
                                text = if (isHumanTurn) "ডাইস রোল করুন" else "${currentP.name} চালছে...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Winner Celebration Modal
            if (gameState.winner != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "👑 বিজয়ী চ্যাম্পিয়ন! 👑", color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${gameState.players.find { it.color == gameState.winner }?.name} ১০০ ঘরে পৌঁছে খেলা জিতেছেন!",
                            color = Color.White,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { gameState = SnakesLaddersState() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Text("আবার খেলুন")
                            }
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                            ) {
                                Text("লবিতে ফিরুন")
                            }
                        }
                    }
                }
            }
        }
    }
}
