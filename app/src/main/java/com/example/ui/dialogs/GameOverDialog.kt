package com.example.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.game.PlayerColor

@Composable
fun GameOverDialog(
    winnerColor: PlayerColor,
    isUserWinner: Boolean,
    potCoins: Long,
    onPlayAgain: () -> Unit,
    onBackToLobby: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy Image Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ludo_banner),
                        contentDescription = "Victory Trophy Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF0F172A).copy(alpha = 0.8f))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isUserWinner) "🏆 আপনি জয়ী হয়েছেন! 🏆" else "খেলা সমাপ্ত!",
                    color = Color(0xFFFFD700),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${winnerColor.titleBn} দল চ্যাম্পিয়ন হয়েছে",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Prize Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "পুরস্কার কয়েন", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(
                                text = if (isUserWinner) "+$potCoins" else "০",
                                color = if (isUserWinner) Color(0xFFFFD700) else Color(0xFFE2E8F0),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFF334155)))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "অর্জিত এক্সপি", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(
                                text = if (isUserWinner) "+120 XP" else "+40 XP",
                                color = Color(0xFF4ADE80),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("play_again_button")
                ) {
                    Text(
                        text = "পুনরায় খেলুন (Rematch)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onBackToLobby,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("back_to_lobby_button")
                ) {
                    Text(
                        text = "লবিতে ফিরে যান",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
