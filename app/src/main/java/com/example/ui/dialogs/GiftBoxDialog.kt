package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.GiftBoxItem
import com.example.game.GiftBoxManager
import com.example.game.GiftBoxReward
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GiftBoxDialog(
    box: GiftBoxItem,
    isOfflineMode: Boolean,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onRewardClaimed: (coins: Long, gems: Int, offlinePoints: Long) -> Unit,
    onWatchAdForBonusBox: () -> Unit
) {
    var isOpened by remember { mutableStateOf(false) }
    var reward by remember { mutableStateOf<GiftBoxReward?>(null) }
    val scope = rememberCoroutineScope()
    val bounceAnim = remember { Animatable(1f) }

    LaunchedEffect(isOpened) {
        if (!isOpened) {
            bounceAnim.animateTo(
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            bounceAnim.snapTo(1f)
        }
    }

    Dialog(onDismissRequest = { if (isOpened) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(box.rarity.colorHex)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color(box.rarity.colorHex)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOfflineMode) "অফলাইন গিফট বক্স 🎁" else "মিস্ট্রি গিফট বক্স 🎁",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isOpened) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isOpened) {
                    // Closed Chest
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(bounceAnim.value)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(box.rarity.colorHex).copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .clickable {
                                isOpened = true
                                reward = GiftBoxManager.generateReward(box)
                            }
                            .testTag("open_gift_box_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎁", fontSize = 64.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isBn) box.nameBn else box.nameEn,
                        color = Color(box.rarity.colorHex),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isBn) box.descriptionBn else "Tap the box to reveal mystery rewards!",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isOpened = true
                            reward = GiftBoxManager.generateReward(box)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(box.rarity.colorHex)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("tap_to_open_button")
                    ) {
                        Text(
                            text = if (isBn) "বক্স খুলুন (Tap to Open)" else "Open Chest",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    val r = reward ?: return@Column
                    // Opened Chest Reveal
                    Text(text = "🎉", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isBn) "অভিনন্দন! পুরস্কার উন্মোচিত" else "Congratulations! Rewards Unlocked",
                        color = Color(0xFF4ADE80),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rewards List Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(box.rarity.colorHex).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RewardRow(emoji = "🪙", label = "কয়েন প্রাপ্তি:", value = "+${r.coinsWon} Coins", color = Color(0xFFFFD700))
                        if (r.gemsWon > 0) {
                            RewardRow(emoji = "💎", label = "জেম প্রাপ্তি:", value = "+${r.gemsWon} Gems", color = Color(0xFF38BDF8))
                        }
                        RewardRow(emoji = "⚡", label = "অফলাইন পয়েন্ট:", value = "+${r.offlinePointsWon} Pts", color = Color(0xFF00E676))
                        if (r.specialBonus != null) {
                            RewardRow(emoji = "✨", label = "বোনাস:", value = r.specialBonus, color = Color(0xFFFF80AB))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onRewardClaimed(r.coinsWon, r.gemsWon, r.offlinePointsWon)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("claim_box_reward_button")
                    ) {
                        Text(
                            text = if (isBn) "পুরস্কার সংগ্রহ করুন" else "Collect Rewards",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Online bonus box option via ad
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF3B82F6).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(10.dp))
                            .clickable {
                                onRewardClaimed(r.coinsWon, r.gemsWon, r.offlinePointsWon)
                                onDismiss()
                                onWatchAdForBonusBox()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBn) "বিজ্ঞাপন দেখে আরেকটি মিস্ট্রি বক্স খুলুন 🎁" else "Watch Ad to Open Another Box",
                                color = Color(0xFF60A5FA),
                                fontSize = 11.sp,
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
fun RewardRow(emoji: String, label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, color = Color(0xFFCBD5E1), fontSize = 12.sp)
        }
        Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
