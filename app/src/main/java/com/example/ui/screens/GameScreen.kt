package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.PlayerColor
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.DiceComponent
import com.example.ui.components.LudoBoardView
import com.example.ui.components.PlayerHeaderCard
import com.example.ui.components.VoiceChatBar
import com.example.ui.dialogs.AntiCheatDialog
import com.example.ui.dialogs.GameOverDialog
import com.example.ui.dialogs.PaymentGatewayDialog
import com.example.ui.dialogs.RewardedAdDialog

@Composable
fun GameScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val isDiceRolling by viewModel.isDiceRolling.collectAsState()
    val floatingEmoji by viewModel.floatingEmoji.collectAsState()
    val activeEmojiThrow by viewModel.activeEmojiThrow.collectAsState()
    val playerChatBubbles by viewModel.playerChatBubbles.collectAsState()
    val user by viewModel.userProfile.collectAsState()
    val showAntiCheat by viewModel.showAntiCheatDialog.collectAsState()
    val showGameOver by viewModel.showGameOverDialog.collectAsState()
    val showRewardedAd by viewModel.showRewardedAdDialog.collectAsState()
    val isBankruptRecovery by viewModel.isBankruptRecoveryAd.collectAsState()
    val showPaymentDialog by viewModel.showPaymentDialog.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()

    val state = gameState ?: return
    val isBn = user?.languageBn ?: true

    val redPlayer = state.players.find { it.color == PlayerColor.RED }
    val greenPlayer = state.players.find { it.color == PlayerColor.GREEN }
    val yellowPlayer = state.players.find { it.color == PlayerColor.YELLOW }
    val bluePlayer = state.players.find { it.color == PlayerColor.BLUE }

    val redHomeTokens = state.tokens[PlayerColor.RED]?.count { it.state == com.example.game.TokenState.HOME } ?: 0
    val greenHomeTokens = state.tokens[PlayerColor.GREEN]?.count { it.state == com.example.game.TokenState.HOME } ?: 0
    val yellowHomeTokens = state.tokens[PlayerColor.YELLOW]?.count { it.state == com.example.game.TokenState.HOME } ?: 0
    val blueHomeTokens = state.tokens[PlayerColor.BLUE]?.count { it.state == com.example.game.TokenState.HOME } ?: 0

    val isMyTurn = state.currentTurnColor == PlayerColor.RED

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Arena Header Bar: Back, Pot Coins, Provably Fair Shield Badge
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateTo(ScreenDestination.HOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Total Pot Box
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏆 পট:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${state.potAmount} 🪙",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Security & Anti-Cheat badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF00E676).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFF00E676), RoundedCornerShape(12.dp))
                                .clickable { viewModel.showAntiCheatDialog.value = true }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                .testTag("anti_cheat_badge"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = "Shield", tint = Color(0xFF00E676), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${state.fairPlaySession.pingMs}ms",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Global Sound Toggle Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSoundEnabled) Color(0xFF00E676).copy(alpha = 0.18f) else Color(0xFFE53935).copy(alpha = 0.18f))
                                .border(1.dp, if (isSoundEnabled) Color(0xFF00E676) else Color(0xFFEF4444), RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleSound() }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                .testTag("game_sound_mute_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = if (isSoundEnabled) "Mute Sound" else "Unmute Sound",
                                    tint = if (isSoundEnabled) Color(0xFF00E676) else Color(0xFFEF4444),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isSoundEnabled) "ON" else "MUTE",
                                    color = if (isSoundEnabled) Color(0xFF00E676) else Color(0xFFEF4444),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // In-Game Settings Button
                        IconButton(
                            onClick = { viewModel.showSettingsDialog.value = true },
                            modifier = Modifier.size(30.dp).testTag("game_settings_button")
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 2. Voice Chat Bar & In-Game Chat Overlay
            VoiceChatBar(
                isMicOn = state.isVoiceChatActive,
                isSpeakerOn = true,
                speakingPlayerName = if (state.currentTurnColor != PlayerColor.RED && (1..3).random() == 1) "রাতুল" else null,
                players = state.players,
                messages = state.messages,
                onToggleMic = { viewModel.toggleVoiceChatMic() },
                onToggleSpeaker = { },
                onSendEmoji = { emoji -> viewModel.sendQuickEmoji(emoji) },
                onDedicateEmoji = { emoji, targetColor, targetName ->
                    viewModel.sendDedicatedEmoji(emoji, targetColor, targetName)
                },
                onSendCustomText = { text -> viewModel.sendCustomChatMessage(text) }
            )

            // 3. Top Players Row: Green (Left) & Yellow (Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (greenPlayer != null) {
                    PlayerHeaderCard(
                        player = greenPlayer,
                        isCurrentTurn = state.currentTurnColor == PlayerColor.GREEN,
                        turnSecondsLeft = state.turnTimerSeconds,
                        tokensHomeCount = greenHomeTokens,
                        activeChatBubble = playerChatBubbles[PlayerColor.GREEN]
                    )
                }
                if (yellowPlayer != null) {
                    PlayerHeaderCard(
                        player = yellowPlayer,
                        isCurrentTurn = state.currentTurnColor == PlayerColor.YELLOW,
                        turnSecondsLeft = state.turnTimerSeconds,
                        tokensHomeCount = yellowHomeTokens,
                        activeChatBubble = playerChatBubbles[PlayerColor.YELLOW]
                    )
                }
            }

            // 4. One vs One Audio Battle Equalizer HUD (Picture 4)
            if (state.mode == com.example.game.GameMode.ONE_VS_ONE_AUDIO) {
                OneVsOneAudioFaceOffBar(
                    player1 = redPlayer,
                    player2 = yellowPlayer ?: greenPlayer,
                    isVoiceActive = state.isVoiceChatActive,
                    onToggleMic = { viewModel.toggleVoiceChatMic() }
                )
            }

            // 5. Center Board Arena View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                LudoBoardView(
                    gameState = state,
                    boardSkinId = user?.boardSkinId ?: "board_classic",
                    onTokenClick = { tokenId ->
                        viewModel.executeTokenMove(tokenId)
                    }
                )

                // Floating Emoji Reaction Animation
                androidx.compose.animation.AnimatedVisibility(
                    visible = floatingEmoji != null,
                    enter = fadeIn() + scaleIn(initialScale = 0.4f),
                    exit = fadeOut() + scaleOut(targetScale = 1.4f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.75f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = floatingEmoji ?: "", fontSize = 42.sp)
                    }
                }

                // Dedicated Emoji Throw Notification (e.g. আপনি ➔ রাহাত কে 😂 উসকানো ইমোজি পাঠিয়েছেন!)
                androidx.compose.animation.AnimatedVisibility(
                    visible = activeEmojiThrow != null,
                    enter = fadeIn() + scaleIn(initialScale = 0.6f),
                    exit = fadeOut() + scaleOut(targetScale = 1.2f)
                ) {
                    val throwAction = activeEmojiThrow
                    if (throwAction != null) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF0F172A).copy(alpha = 0.95f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
                            shadowElevation = 16.dp,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = throwAction.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "🎭 ইমোজি উৎসর্গ!",
                                        color = Color(0xFFFFD700),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = throwAction.actionTextBn,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Bottom Players Row: Red (Local User - Left) & Blue (Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (redPlayer != null) {
                    PlayerHeaderCard(
                        player = redPlayer,
                        isCurrentTurn = isMyTurn,
                        turnSecondsLeft = state.turnTimerSeconds,
                        tokensHomeCount = redHomeTokens,
                        frameColorHex = 0xFFFFD700,
                        activeChatBubble = playerChatBubbles[PlayerColor.RED]
                    )
                }
                if (bluePlayer != null) {
                    PlayerHeaderCard(
                        player = bluePlayer,
                        isCurrentTurn = state.currentTurnColor == PlayerColor.BLUE,
                        turnSecondsLeft = state.turnTimerSeconds,
                        tokensHomeCount = blueHomeTokens,
                        activeChatBubble = playerChatBubbles[PlayerColor.BLUE]
                    )
                }
            }

            // 6. Bottom Controls: Turn Status & Dice Roller
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Turn Status message
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(state.currentTurnColor.baseColorHex))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isMyTurn) "👉 আপনার চাল!" else "${state.currentTurnColor.titleBn} দলের চাল...",
                                color = if (isMyTurn) Color(0xFFFFD700) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBn) state.lastMoveDescriptionBn else state.lastMoveDescriptionEn,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            maxLines = 2
                        )
                    }

                    // 3D Animated Dice Component
                    DiceComponent(
                        diceValue = state.currentDiceValue,
                        isRolling = isDiceRolling,
                        isMyTurn = isMyTurn,
                        playerColor = state.currentTurnColor,
                        fairSeedHash = state.diceRollHash,
                        onRollClick = { viewModel.rollDice() }
                    )
                }
            }
        }

        // Dialogs
        if (showAntiCheat) {
            AntiCheatDialog(
                session = state.fairPlaySession,
                lastDiceHash = state.diceRollHash,
                onDismiss = { viewModel.showAntiCheatDialog.value = false }
            )
        }

        if (showGameOver && state.winner != null) {
            GameOverDialog(
                winnerColor = state.winner!!,
                isUserWinner = state.winner == PlayerColor.RED,
                potCoins = state.potAmount,
                onPlayAgain = {
                    viewModel.startNewGame(state.mode, viewModel.selectedBetOption.value)
                },
                onBackToLobby = {
                    viewModel.navigateTo(ScreenDestination.HOME)
                }
            )
        }

        if (showRewardedAd) {
            RewardedAdDialog(
                isBankruptRecovery = isBankruptRecovery,
                onDismiss = { viewModel.showRewardedAdDialog.value = false },
                onRewardEarned = { coins, gems ->
                    viewModel.handleAdRewardEarned(coins, gems)
                }
            )
        }

        if (showPaymentDialog) {
            PaymentGatewayDialog(
                onDismiss = { viewModel.showPaymentDialog.value = false },
                onPaymentSuccess = { coinsEarned ->
                    viewModel.handlePaymentSuccess(coinsEarned)
                }
            )
        }
    }
}

@Composable
fun OneVsOneAudioFaceOffBar(
    player1: com.example.game.PlayerInfo?,
    player2: com.example.game.PlayerInfo?,
    isVoiceActive: Boolean,
    onToggleMic: () -> Unit
) {
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "wave")
    val wave1 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "w1"
    )
    val wave2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(400, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "w2"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF06B6D4)),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("audio_face_off_bar")
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player 1
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = player1?.countryFlag ?: "🇧🇩", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = player1?.name ?: "আপনি",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "লাইভ অডিও", color = Color(0xFF00E676), fontSize = 9.sp)
                }
            }

            // Center Equalizer Waveforms and Glowing Mic
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.clickable(onClick = onToggleMic)
            ) {
                Box(
                    modifier = Modifier
                        .height((16 * wave1).dp)
                        .width(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF00E676))
                )
                Box(
                    modifier = Modifier
                        .height((22 * wave2).dp)
                        .width(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF06B6D4))
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isVoiceActive) Color(0xFF00E676) else Color(0xFFE53935)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isVoiceActive) "🎙️" else "🔇", fontSize = 16.sp)
                }

                Box(
                    modifier = Modifier
                        .height((22 * wave2).dp)
                        .width(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF06B6D4))
                )
                Box(
                    modifier = Modifier
                        .height((16 * wave1).dp)
                        .width(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF00E676))
                )
            }

            // Player 2
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = player2?.name ?: "প্রতিদ্বন্দ্বী",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "সংযুক্ত", color = Color(0xFF38BDF8), fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = player2?.countryFlag ?: "🇮🇳", fontSize = 16.sp)
            }
        }
    }
}
