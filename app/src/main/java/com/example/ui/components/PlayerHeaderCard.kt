package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.PlayerColor
import com.example.game.PlayerInfo

@Composable
fun PlayerHeaderCard(
    player: PlayerInfo,
    isCurrentTurn: Boolean,
    turnSecondsLeft: Int,
    tokensHomeCount: Int,
    frameColorHex: Long = 0xFFFFD700,
    activeChatBubble: String? = null,
    modifier: Modifier = Modifier
) {
    val playerColor = Color(player.color.baseColorHex)
    val playerDark = Color(player.color.darkColorHex)

    val transition = rememberInfiniteTransition(label = "turn_glow")
    val turnGlowScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrentTurn) 1.05f else 1f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse),
        label = "turn_glow_scale"
    )

    Box(contentAlignment = Alignment.TopCenter) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E293B),
            border = androidx.compose.foundation.BorderStroke(
                width = if (isCurrentTurn) 2.5.dp else 1.dp,
                color = if (isCurrentTurn) playerColor else Color(0xFF334155)
            ),
            shadowElevation = if (isCurrentTurn) 8.dp else 2.dp,
            modifier = modifier.scale(if (isCurrentTurn) turnGlowScale else 1f)
        ) {
            Column(modifier = Modifier.padding(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Avatar with Frame & Player Color indicator
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(player.color.lightColorHex), playerColor)
                                    )
                                )
                                .border(2.dp, Color(frameColorHex), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player.name.firstOrNull()?.toString() ?: "P",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Speaking dot badge
                        if (player.isSpeaking) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    // Name, Country Flag & Home Tokens count
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = player.countryFlag,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = player.name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(playerColor)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "ঘরে: $tokensHomeCount/4",
                                color = Color(0xFFCBD5E1),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Turn Countdown Progress Bar
                if (isCurrentTurn) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (turnSecondsLeft / 15f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = playerColor,
                        trackColor = Color(0xFF334155),
                    )
                }
            }
        }

        // Floating Chat / Dedicated Emoji bubble over the card
        androidx.compose.animation.AnimatedVisibility(
            visible = activeChatBubble != null,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.5.dp, Color(0xFFFFD700), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = activeChatBubble ?: "",
                    color = Color.White,
                    fontSize = if ((activeChatBubble ?: "").length <= 2) 20.sp else 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
