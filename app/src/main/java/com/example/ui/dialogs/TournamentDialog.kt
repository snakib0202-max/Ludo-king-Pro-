package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class TournamentMatch(
    val player1Name: String,
    val player1Flag: String,
    val player2Name: String,
    val player2Flag: String,
    val winnerName: String? = null,
    val isUserMatch: Boolean = false
)

@Composable
fun TournamentDialog(
    userCoins: Long,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onStartTournamentMatch: (roundName: String) -> Unit
) {
    var activeRound by remember { mutableIntStateOf(1) } // 1: Quarter, 2: Semi, 3: Final

    val quarterFinals = listOf(
        TournamentMatch("আপনি", "🇧🇩", "রাতুল ইসলাম", "🇳🇬", isUserMatch = true),
        TournamentMatch("আরিফ খান", "🇮🇳", "সাদিয়া রহমান", "🇵🇰", winnerName = "আরিফ খান"),
        TournamentMatch("সুমি আক্তার", "🇦🇱", "কবীর হোসেন", "🇲🇾", winnerName = "সুমি আক্তার"),
        TournamentMatch("ডেভিড ওয়ার্নার", "🇦🇺", "ফারহানা জাহান", "🇧🇩", winnerName = "ফারহানা জাহান")
    )

    val semiFinals = listOf(
        TournamentMatch("আপনি", "🇧🇩", "আরিফ খান", "🇮🇳", isUserMatch = true),
        TournamentMatch("সুমি আক্তার", "🇦🇱", "ফারহানা জাহান", "🇧🇩", winnerName = "ফারহানা জাহান")
    )

    val grandFinal = TournamentMatch("আপনি", "🇧🇩", "ফারহানা জাহান", "🇧🇩", isUserMatch = true)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Bar with Trophy & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFB45309)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBn) "চ্যাম্পিয়নশিপ টুর্নামেন্ট" else "Championship Tournament",
                                color = Color(0xFFFFD700),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBn) "৮-প্লেয়ার নকআউট ব্র্যাকেট" else "8-Player Knockout Bracket",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                // Prize Pool Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD700)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isBn) "মোট প্রাইজপুল" else "Total Prize",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "🏆 ৫০,০০০ কয়েন",
                                color = Color(0xFFFFD700),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .width(1.dp)
                                .background(Color(0xFF334155))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isBn) "এন্ট্রি ফি" else "Entry Fee",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "🪙 ৫,০০০ কয়েন",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Round Selection Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        1 to if (isBn) "কোয়ার্টার" else "Quarters",
                        2 to if (isBn) "সেমিফাইনাল" else "Semis",
                        3 to if (isBn) "ফাইনাল 👑" else "Finals 👑"
                    ).forEach { (round, label) ->
                        val isSelected = activeRound == round
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFFFD700) else Color(0xFF1E293B),
                            modifier = Modifier
                                .clickable { activeRound = round }
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Matches List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (activeRound) {
                        1 -> {
                            items(quarterFinals.size) { i ->
                                MatchCard(quarterFinals[i], isBn)
                            }
                        }
                        2 -> {
                            items(semiFinals.size) { i ->
                                MatchCard(semiFinals[i], isBn)
                            }
                        }
                        3 -> {
                            item {
                                MatchCard(grandFinal, isBn)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Action Button: Enter Match
                Button(
                    onClick = {
                        val rName = when (activeRound) {
                            1 -> "কোয়ার্টার ফাইনাল"
                            2 -> "সেমি ফাইনাল"
                            else -> "মেগা ফাইনাল"
                        }
                        onStartTournamentMatch(rName)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("tournament_start_match_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "ম্যাচ শুরু করুন (${when(activeRound){1->"কোয়ার্টার"; 2->"সেমি"; else->"ফাইনাল"}})" else "Start Tournament Match",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: TournamentMatch, isBn: Boolean) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (match.isUserMatch) Color(0xFF1E3A8A) else Color(0xFF1E293B)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (match.isUserMatch) 1.5.dp else 0.5.dp,
            color = if (match.isUserMatch) Color(0xFF60A5FA) else Color(0xFF334155)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player 1
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = match.player1Flag, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = match.player1Name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "VS",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            // Player 2
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = match.player2Name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = match.player2Flag, fontSize = 16.sp)
            }
        }
    }
}
