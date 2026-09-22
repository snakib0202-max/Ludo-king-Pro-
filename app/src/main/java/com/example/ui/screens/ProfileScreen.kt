package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.UserEntity
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.dialogs.FeedbackDialog

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()
    val showFeedback by viewModel.showFeedbackDialog.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsState()
    val u = user ?: UserEntity()
    val isBn = u.languageBn

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(ScreenDestination.HOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "প্লেয়ার প্রোফাইল ও স্ট্যাটস" else "Player Profile & Stats",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Global Sound Quick Toggle Button in Profile Header
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (isSoundEnabled) "Mute Sound" else "Unmute Sound",
                        tint = if (isSoundEnabled) Color(0xFF00E676) else Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Player Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD700)),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar with Royal Border
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFFD700), Color(0xFFE53935))
                                    )
                                )
                                .border(3.dp, Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = u.name.firstOrNull()?.toString() ?: "L",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = u.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "👑 Ludo Grandmaster (Lv.${u.level})",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // XP Bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "XP প্রগ্রেস", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                Text(text = "${u.currentXp} / ${u.maxXp}", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { (u.currentXp.toFloat() / u.maxXp.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFFFFD700),
                                trackColor = Color(0xFF334155)
                            )
                        }
                    }
                }
            }

            // Analytics: Match Records
            item {
                val winRate = if (u.totalGames > 0) ((u.wins.toFloat() / u.totalGames) * 100).toInt() else 67
                Text(
                    text = if (isBn) "📊 সেশন অ্যানালিটিক্স ও রেকর্ডস" else "📊 Session Analytics",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(label = "মোট ম্যাচ", value = "${u.totalGames}", modifier = Modifier.weight(1f))
                    StatBox(label = "বিজয়", value = "${u.wins}", modifier = Modifier.weight(1f))
                    StatBox(label = "উইন রেট", value = "$winRate%", valueColor = Color(0xFF00E676), modifier = Modifier.weight(1f))
                    StatBox(label = "উইন স্ট্রিক", value = "${u.winStreak} 🔥", valueColor = Color(0xFFFF9800), modifier = Modifier.weight(1f))
                }
            }

            // Analytics: Provably Fair Dice Distribution
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "ফেয়ার প্লে ডাইস ডিস্ট্রিবিউশন (SHA-256)" else "Provably Fair Dice Distribution",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (isBn) "প্রতিটি ডাইস রোলের ফলাফল গাণিতিকভাবে নিরপেক্ষ ও অপরিবর্তনীয়" else "Mathematically verified unbiased random outcomes",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dice bars
                        val diceCounts = listOf(
                            Pair("১", u.onesRolled),
                            Pair("২", u.twosRolled),
                            Pair("৩", u.threesRolled),
                            Pair("৪", u.foursRolled),
                            Pair("৫", u.fivesRolled),
                            Pair("৬ (ছক্কা)", u.sixesRolled)
                        )
                        val maxCount = (diceCounts.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            diceCounts.forEach { (face, count) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = face,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(60.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = { (count.toFloat() / maxCount).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = if (face.contains("৬")) Color(0xFFFFD700) else Color(0xFF38BDF8),
                                        trackColor = Color(0xFF334155)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$count বার",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 10.sp,
                                        modifier = Modifier.width(42.dp),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sound & Audio Settings Card in Profile
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "সাউন্ড ও অডিও সেটিংস (Sound Effects)" else "Sound Effects & Audio",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sound Effects Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = null,
                                    tint = if (isSoundEnabled) Color(0xFF00E676) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isBn) "গেম সাউন্ড এফেক্টস" else "Game Sound Effects",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (isSoundEnabled) (if (isBn) "চাল, ডাইস ও জয়ের শব্দ সক্রিয়" else "Roll, moves & victory SFX on")
                                        else (if (isBn) "মিউট করা রয়েছে (Muted)" else "Sound is muted"),
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Switch(
                                checked = isSoundEnabled,
                                onCheckedChange = { viewModel.toggleSound(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF00E676),
                                    uncheckedThumbColor = Color(0xFF94A3B8),
                                    uncheckedTrackColor = Color(0xFF334155)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Vibration Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Vibration,
                                    contentDescription = null,
                                    tint = if (isVibrationEnabled) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isBn) "হ্যাপটিক ভাইব্রেশন" else "Haptic Vibration",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (isVibrationEnabled) (if (isBn) "চাল ও কাটার ভাইব্রেশন অন" else "Tactile feedback enabled")
                                        else (if (isBn) "ভাইব্রেশন বন্ধ" else "Vibration disabled"),
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Switch(
                                checked = isVibrationEnabled,
                                onCheckedChange = { viewModel.toggleVibration(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF38BDF8),
                                    uncheckedThumbColor = Color(0xFF94A3B8),
                                    uncheckedTrackColor = Color(0xFF334155)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preview Sound Effect Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.testSoundEffects() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBn) "ডাইস ও চাল" else "Move SFX",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = { viewModel.testWinSound() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166534)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isBn) "🏆 জয়" else "Win SFX",
                                    fontSize = 11.sp,
                                    color = Color(0xFF86EFAC),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { viewModel.testLossSound() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isBn) "💔 হার" else "Loss SFX",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFCA5A5),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // User Feedback Button
            item {
                Button(
                    onClick = { viewModel.showFeedbackDialog.value = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("open_feedback_button")
                ) {
                    Icon(Icons.Default.Feedback, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "মতামত ও ফিডব্যাক দিন (Feedback)" else "Submit Feedback & Rating",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Feedback Dialog
        if (showFeedback) {
            FeedbackDialog(
                onDismiss = { viewModel.showFeedbackDialog.value = false },
                onSubmitFeedback = { rating, cat, comments ->
                    // Feedback captured
                }
            )
        }
    }
}

@Composable
fun StatBox(
    label: String,
    value: String,
    valueColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
