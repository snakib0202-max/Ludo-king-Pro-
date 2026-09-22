package com.example.ui.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class SevenBetType(val labelBn: String, val multiplier: Int) {
    DOWN("২ থেকে ৬ (Down)", 2),
    SEVEN("লাকি ৭ (Lucky 7)", 3),
    UP("৮ থেকে ১২ (Up)", 2)
}

@Composable
fun SevenUpDownDialog(
    userCoins: Long,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onWinCoins: (Long) -> Unit
) {
    var selectedBet by remember { mutableStateOf(SevenBetType.SEVEN) }
    var betAmount by remember { mutableLongStateOf(500L) }
    var dice1 by remember { mutableIntStateOf(3) }
    var dice2 by remember { mutableIntStateOf(4) }
    var isRolling by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("আপনার পছন্দের অপশন বেছে ডাইস রোল করুন!") }
    val diceRotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isRolling) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎲", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isBn) "৭ আপ ৭ ডাউন (7 Up Down)" else "7 Up 7 Down",
                                color = Color(0xFFFFD700),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ব্যালেন্স: $userCoins কয়েন",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, enabled = !isRolling) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2 Animated Dices Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dice 1
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE53935),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(56.dp)
                            .rotate(diceRotation.value)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "$dice1", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(text = "+", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    // Dice 2
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E88E5),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(56.dp)
                            .rotate(-diceRotation.value)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "$dice2", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(text = "=", color = Color(0xFFFFD700), fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    // Sum Total
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "${dice1 + dice2}", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Result Banner
                Text(
                    text = resultMessage,
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bet Type Selection (2-6, 7, 8-12)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SevenBetType.entries.forEach { type ->
                        val isSelected = selectedBet == type
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFFFD700) else Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color.White else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isRolling) { selectedBet = type }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = type.labelBn,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "${type.multiplier}X লাভ",
                                    color = if (isSelected) Color(0xFF78350F) else Color(0xFFFFD700),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bet Coins Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf(500L, 1000L, 2500L, 5000L).forEach { coins ->
                        val isCurrent = betAmount == coins
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) Color(0xFF2563EB) else Color(0xFF1E293B),
                            modifier = Modifier.clickable(enabled = !isRolling) { betAmount = coins }
                        ) {
                            Text(
                                text = "$coins",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Roll Button
                Button(
                    onClick = {
                        scope.launch {
                            isRolling = true
                            resultMessage = "ডাইস ঘুরছে..."
                            diceRotation.animateTo(
                                targetValue = diceRotation.value + 720f,
                                animationSpec = tween(600, easing = FastOutSlowInEasing)
                            )
                            val d1 = Random.nextInt(1, 7)
                            val d2 = Random.nextInt(1, 7)
                            dice1 = d1
                            dice2 = d2
                            val sum = d1 + d2

                            val isWin = when (selectedBet) {
                                SevenBetType.DOWN -> sum in 2..6
                                SevenBetType.SEVEN -> sum == 7
                                SevenBetType.UP -> sum in 8..12
                            }

                            if (isWin) {
                                val winCoins = betAmount * selectedBet.multiplier
                                resultMessage = "🎉 দারুন! মোট যোগফল $sum! আপনি জিতেছেন $winCoins কয়েন!"
                                onWinCoins(winCoins)
                            } else {
                                resultMessage = "💔 ওহ না! মোট যোগফল $sum! আপনার বাজি সফল হয়নি।"
                            }
                            isRolling = false
                        }
                    },
                    enabled = !isRolling && userCoins >= betAmount,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("seven_up_down_roll_btn")
                ) {
                    Text(
                        text = if (isRolling) "ঘুরছে..." else "ডাইস রোল করুন (বাজি: $betAmount)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
