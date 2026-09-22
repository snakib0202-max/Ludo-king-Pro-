package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val trophies: Int,
    val winRate: String,
    val avatarBg: Color,
    val isCurrentUser: Boolean = false
)

val GLOBAL_LEADERBOARD = listOf(
    LeaderboardUser(1, "সুলতান খান 👑", 4850, "78%", Color(0xFFE53935)),
    LeaderboardUser(2, "রাহাত চৌধুরী 🥈", 4320, "72%", Color(0xFF1E88E5)),
    LeaderboardUser(3, "সাকিব আল হাসান 🥉", 3950, "69%", Color(0xFF43A047)),
    LeaderboardUser(4, "তানভীর আহমেদ", 3410, "65%", Color(0xFFFDD835)),
    LeaderboardUser(5, "আপনি (Ludo Champion)", 2450, "67%", Color(0xFFFF9800), isCurrentUser = true),
    LeaderboardUser(6, "ফারহান কবির", 2180, "58%", Color(0xFF8E24AA)),
    LeaderboardUser(7, "মেহরাব হোসেন", 1920, "54%", Color(0xFF00ACC1)),
    LeaderboardUser(8, "আরিফুর রহমান", 1680, "51%", Color(0xFF3949AB)),
    LeaderboardUser(9, "নাঈম ইসলাম", 1450, "49%", Color(0xFF5E35B1)),
    LeaderboardUser(10, "ইমরান শেখ", 1210, "45%", Color(0xFFD81B60))
)

@Composable
fun LeaderboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()
    val isBn = user?.languageBn ?: true
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = if (isBn) listOf("গ্লোবাল র‍্যাঙ্ক", "সাপ্তাহিক লিগ", "বন্ধুরা") else listOf("Global", "Weekly League", "Friends")

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(ScreenDestination.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBn) "গ্লোবাল লিডারবোর্ড" else "Global Leaderboard",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0F172A),
            contentColor = Color(0xFFFFD700),
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .background(Color(0xFFFFD700), RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top 3 Podium Cards
            item {
                PodiumSection(top3 = GLOBAL_LEADERBOARD.take(3))
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBn) "শীর্ষ খেলোয়াড়বৃন্দ" else "Top Competitors",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(GLOBAL_LEADERBOARD) { item ->
                LeaderboardRowItem(item = item)
            }
        }
    }
}

@Composable
fun PodiumSection(top3: List<LeaderboardUser>) {
    val rank1 = top3.getOrNull(0) ?: return
    val rank2 = top3.getOrNull(1) ?: return
    val rank3 = top3.getOrNull(2) ?: return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // Rank 2 (Silver)
        PodiumPillar(user = rank2, height = 120.dp, pillarColor = Color(0xFFB0BEC5), crownText = "🥈 2nd")

        // Rank 1 (Gold)
        PodiumPillar(user = rank1, height = 145.dp, pillarColor = Color(0xFFFFD700), crownText = "👑 1st")

        // Rank 3 (Bronze)
        PodiumPillar(user = rank3, height = 105.dp, pillarColor = Color(0xFFCD7F32), crownText = "🥉 3rd")
    }
}

@Composable
fun PodiumPillar(user: LeaderboardUser, height: androidx.compose.ui.unit.Dp, pillarColor: Color, crownText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Text(text = crownText, color = pillarColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(user.avatarBg)
                .border(2.dp, pillarColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = user.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user.name.split(" ").first(),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(text = "${user.trophies} 🏆", color = pillarColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(pillarColor.copy(alpha = 0.45f), Color(0xFF1E293B))
                    )
                )
                .border(1.dp, pillarColor.copy(alpha = 0.6f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )
    }
}

@Composable
fun LeaderboardRowItem(item: LeaderboardUser) {
    val isTop3 = item.rank <= 3
    val rankBadgeColor = when (item.rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFB0BEC5)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFF64748B)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (item.isCurrentUser) Color(0xFF1E3A8A) else Color(0xFF1E293B))
            .border(
                width = if (item.isCurrentUser) 1.5.dp else 1.dp,
                color = if (item.isCurrentUser) Color(0xFFFFD700) else Color(0xFF334155),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Rank Number
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(rankBadgeColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${item.rank}",
                    color = rankBadgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(item.avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = item.name,
                    color = if (item.isCurrentUser) Color(0xFFFFD700) else Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "উইন রেট: ${item.winRate}",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }
        }

        // Trophies
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${item.trophies}", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(3.dp))
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
        }
    }
}
