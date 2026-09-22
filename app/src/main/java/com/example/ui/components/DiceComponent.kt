package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.PlayerColor
import kotlinx.coroutines.delay

@Composable
fun DiceComponent(
    diceValue: Int?,
    isRolling: Boolean,
    isMyTurn: Boolean,
    playerColor: PlayerColor,
    fairSeedHash: String,
    onRollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayValue by remember { mutableStateOf(diceValue ?: 6) }
    val rotationAngle = remember { Animatable(0f) }
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(diceValue) {
        if (diceValue != null) {
            displayValue = diceValue
        }
    }

    // Rolling animation
    LaunchedEffect(isRolling) {
        if (isRolling) {
            // Rapid rotation and face cycling
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 600) {
                displayValue = (1..6).random()
                rotationAngle.snapTo((rotationAngle.value + 45f) % 360f)
                delay(60)
            }
            rotationAngle.animateTo(
                targetValue = 0f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
        }
    }

    // Pulsing indicator when waiting for player to roll
    val infiniteTransition = rememberInfiniteTransition(label = "turn_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isMyTurn && !isRolling && diceValue == null) 1.12f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .scale(if (isMyTurn && diceValue == null) pulseGlow else 1f)
                .rotate(rotationAngle.value)
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFF0F4F8),
                            Color(0xFFD9E2EC)
                        )
                    )
                )
                .border(
                    width = if (isMyTurn) 3.dp else 1.5.dp,
                    color = if (isMyTurn) Color(playerColor.baseColorHex) else Color(0xFFB0BEC5),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(enabled = isMyTurn && !isRolling && diceValue == null) {
                    onRollClick()
                }
                .testTag("ludo_dice_button"),
            contentAlignment = Alignment.Center
        ) {
            DiceFaceDots(
                value = displayValue,
                dotColor = Color(playerColor.darkColorHex)
            )
        }

        if (isMyTurn && diceValue == null) {
            Text(
                text = "ছক্কা মারুন!",
                color = Color(0xFFFFD700),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun DiceFaceDots(value: Int, dotColor: Color) {
    val dotSize = 10.dp
    val spacing = 6.dp

    when (value) {
        1 -> {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
        2 -> {
            Row(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.Top))
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.Bottom))
            }
        }
        3 -> {
            Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.TopStart))
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.Center))
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.BottomEnd))
            }
        }
        4 -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(11.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxSize().weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                }
                Row(modifier = Modifier.fillMaxSize().weight(1f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                }
            }
        }
        5 -> {
            Box(modifier = Modifier.fillMaxSize().padding(11.dp)) {
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.TopStart))
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.TopEnd))
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.Center))
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.BottomStart))
                Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor).align(Alignment.BottomEnd))
            }
        }
        6 -> {
            Row(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize().weight(1f)) {
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                }
                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize().weight(1f), horizontalAlignment = Alignment.End) {
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                    Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor))
                }
            }
        }
    }
}
