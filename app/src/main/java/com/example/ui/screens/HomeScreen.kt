package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MissionEntity
import com.example.data.UserEntity
import com.example.game.BET_OPTIONS
import com.example.game.BetOption
import com.example.game.GameMode
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()
    val missions by viewModel.dailyMissions.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val isBn = user?.languageBn ?: true

    var selectedModeForBet by remember { mutableStateOf<GameMode?>(null) }
    var showMissionsDrawer by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar: Profile & Currencies
            LobbyTopBar(
                user = user ?: UserEntity(),
                isBn = isBn,
                isSoundEnabled = isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onAddCoinsClick = { viewModel.showPaymentDialog.value = true },
                onRewardedAdClick = {
                    viewModel.isBankruptRecoveryAd.value = false
                    viewModel.showRewardedAdDialog.value = true
                },
                onLanguageToggle = { viewModel.toggleLanguage() },
                onSettingsClick = { viewModel.showSettingsDialog.value = true },
                onProfileClick = { viewModel.navigateTo(ScreenDestination.PROFILE) }
            )

            // 2. Main Scrollable Lobby Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Hero Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(136.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                            .shadow(8.dp, RoundedCornerShape(18.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_ludo_banner),
                            contentDescription = "Ludo King Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF090D16).copy(alpha = 0.85f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(
                                    text = if (isBn) "লুডু কিং প্রো" else "LUDO KING PRO",
                                    color = Color(0xFFFFD700),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (isBn) "রিয়েল-টাইম মাল্টিপ্লেয়ার ও ভয়েস চ্যাট" else "Real-Time Online & Voice Chat",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isBn) "১০০% ফেয়ার প্লে ও সিকিউরড" else "100% Provably Fair",
                                        color = Color(0xFF00E676),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Daily Missions Quick Pill Bar
                item {
                    val unclaimedCount = missions.count { it.isCompleted && !it.isClaimed }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMissionsDrawer = !showMissionsDrawer }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFFFFD700))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isBn) "দৈনিক মিশন ও রিওয়ার্ড" else "Daily Missions & Rewards",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isBn) "${missions.count { it.isCompleted }}/${missions.size} সম্পন্ন হয়েছে" else "${missions.count { it.isCompleted }}/${missions.size} Completed",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            if (unclaimedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF00E676))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "$unclaimedCount টি ক্লেইম করুন!" else "$unclaimedCount Claim!",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    text = if (showMissionsDrawer) "লুকান ▲" else "দেখুন ▼",
                                    color = Color(0xFFFFD700),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Expandable Missions Section
                if (showMissionsDrawer) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF131D2E))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            missions.forEach { mission ->
                                MissionCardItem(
                                    mission = mission,
                                    isBn = isBn,
                                    onClaim = { viewModel.claimMission(mission) }
                                )
                            }
                        }
                    }
                }

                // Special Features Row: Lucky Gift Chest & AI Assistant
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Lucky Gift Box Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.isOfflineGiftBoxMode.value = false
                                    viewModel.activeGiftBoxDialog.value = com.example.game.DEFAULT_GIFT_BOXES[0]
                                }
                                .testTag("home_lucky_gift_box")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎁", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isBn) "লাকি গিফট বক্স" else "Lucky Chest",
                                        color = Color(0xFFFFD700),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isBn) "এড দেখে ফ্রি কয়েন" else "Free Coins & Gems",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // AI Assistant Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.showAiAssistantDialog.value = true
                                }
                                .testTag("home_ai_assistant_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🤖", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isBn) "এআই গেম এজেন্ট" else "AI Assistant",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isBn) "যেকোনো প্রশ্ন করুন" else "Ask anything",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Tournament Golden Banner (Picture 5 Centerpiece)
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showTournamentDialog.value = true }
                            .testTag("home_tournament_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF78350F), Color(0xFFB45309), Color(0xFF78350F))
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isBn) "চ্যাম্পিয়নশিপ টুর্নামেন্ট" else "CHAMPIONSHIP TOURNAMENT",
                                        color = Color(0xFFFFD700),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = if (isBn) "৫০,০০০ কয়েন প্রাইজপুল • নকআউট ব্র্যাকেট" else "50,000 Coins Prize Pool • Knockout",
                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFFD700))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isBn) "খেলুন ▶" else "PLAY ▶",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 4. Primary 4 Golden Game Modes (Ludo King 2x2 Grid)
                item {
                    Text(
                        text = if (isBn) "মূল গেম মোডসমূহ:" else "Primary Game Modes:",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Play Online
                        LudoKingModeButton(
                            title = if (isBn) "অনলাইন\nমাল্টিপ্লেয়ার" else "PLAY\nONLINE",
                            liveCount = "● ১২৪,৫০০ অনলাইনে",
                            icon = "🌐",
                            bgGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                            modifier = Modifier.weight(1f),
                            onClick = { selectedModeForBet = GameMode.ONLINE_MULTIPLAYER },
                            testTag = "mode_play_online"
                        )
                        // Play With Friends
                        LudoKingModeButton(
                            title = if (isBn) "বন্ধুদের\nসাথে খেলুন" else "PLAY WITH\nFRIENDS",
                            liveCount = "● ১৮,৩৪০ রুমে",
                            icon = "👥",
                            bgGradient = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.showFriendInviteDialog.value = true },
                            testTag = "mode_play_with_friends"
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // VS Computer
                        LudoKingModeButton(
                            title = if (isBn) "কম্পিউটার\nবনাম (অফলাইন)" else "VS\nCOMPUTER",
                            liveCount = "⚡ অফলাইন মোড",
                            icon = "🤖",
                            bgGradient = listOf(Color(0xFF10B981), Color(0xFF047857)),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.startNewGame(GameMode.VS_COMPUTER, BET_OPTIONS[0]) },
                            testTag = "mode_vs_computer"
                        )
                        // Pass N Play
                        LudoKingModeButton(
                            title = if (isBn) "পাস অ্যান্ড প্লে\n(লোকাল)" else "PASS N\nPLAY",
                            liveCount = "👥 একই ডিভাইসে",
                            icon = "📱",
                            bgGradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.startNewGame(GameMode.PASS_AND_PLAY, BET_OPTIONS[0]) },
                            testTag = "mode_pass_and_play"
                        )
                    }
                }

                // 5. Special Game Modes & Mini Games Row
                item {
                    Text(
                        text = if (isBn) "স্পেশাল মোড ও মিনি গেমস:" else "Special Modes & Mini Games:",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Snakes & Ladders
                        MiniGameCard(
                            title = if (isBn) "সাপ-লুডু" else "Snakes",
                            subtitle = "১ থেকে ১০০",
                            icon = "🐍",
                            accentColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(ScreenDestination.SNAKES_AND_LADDERS) },
                            testTag = "mode_snakes_ladders"
                        )
                        // 6-Player Ludo
                        MiniGameCard(
                            title = if (isBn) "৬-প্লেয়ার" else "6-Player",
                            subtitle = "হেক্সা বোর্ড",
                            icon = "⭐",
                            accentColor = Color(0xFFEC4899),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(ScreenDestination.SIX_PLAYER_LUDO) },
                            testTag = "mode_six_player"
                        )
                        // 1v1 Audio Face-Off
                        MiniGameCard(
                            title = if (isBn) "১v১ অডিও" else "1v1 Audio",
                            subtitle = "ভয়েস রুম",
                            icon = "🎙️",
                            accentColor = Color(0xFF06B6D4),
                            modifier = Modifier.weight(1f),
                            onClick = { selectedModeForBet = GameMode.ONE_VS_ONE_AUDIO },
                            testTag = "mode_one_vs_one_audio"
                        )
                        // 7 Up 7 Down
                        MiniGameCard(
                            title = if (isBn) "৭ আপ ডাউন" else "7 Up Down",
                            subtitle = "লাকি ডাইস",
                            icon = "🎲",
                            accentColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.showSevenUpDownDialog.value = true },
                            testTag = "mode_seven_up_down"
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // 4. Bottom Navigation Bar
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                contentColor = Color(0xFFFFD700),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Lobby") },
                    label = { Text(if (isBn) "লবি" else "Lobby", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = Color(0xFFFFD700)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { viewModel.navigateTo(ScreenDestination.LEADERBOARD) },
                    icon = { Icon(Icons.Default.MilitaryTech, contentDescription = "Leaderboard") },
                    label = { Text(if (isBn) "র‍্যাঙ্কিং" else "Rank", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { viewModel.navigateTo(ScreenDestination.SHOP) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Shop") },
                    label = { Text(if (isBn) "শপ" else "Shop", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { viewModel.navigateTo(ScreenDestination.PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text(if (isBn) "প্রোফাইল" else "Profile", fontSize = 10.sp) }
                )
            }
        }

        // Floating AI Assistant Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8)),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .clickable { viewModel.showAiAssistantDialog.value = true }
                    .testTag("floating_ai_assistant_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🤖", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "এআই হেল্প" else "AI Help",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bet Selector Modal when Online Mode is tapped
        if (selectedModeForBet != null) {
            BetSelectorSheet(
                isBn = isBn,
                userCoins = user?.coins ?: 0L,
                onDismiss = { selectedModeForBet = null },
                onSelectBet = { bet ->
                    val mode = selectedModeForBet!!
                    selectedModeForBet = null
                    viewModel.startNewGame(mode, bet)
                }
            )
        }
    }
}

@Composable
fun LobbyTopBar(
    user: UserEntity,
    isBn: Boolean,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onAddCoinsClick: () -> Unit,
    onRewardedAdClick: () -> Unit,
    onLanguageToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = Color(0xFF0F172A),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile & Level
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                        .border(2.dp, Color(0xFFFFD700), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.firstOrNull()?.toString() ?: "L",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = user.name,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lv.${user.level}",
                        color = Color(0xFFFFD700),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Currencies & Ad Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Free Coin Ad Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF00C853))
                        .clickable { onRewardedAdClick() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("free_coins_ad_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isBn) "ফ্রি কয়েন" else "Free Coins",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Coins Box with + button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                    modifier = Modifier.clickable { onAddCoinsClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (user.coins >= 1000) "${user.coins / 1000}k" else "${user.coins}",
                            color = Color(0xFFFFD700),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD700)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(12.dp))
                        }
                    }
                }

                // Language switcher
                IconButton(
                    onClick = onLanguageToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Global Sound Mute/Unmute Toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.size(32.dp).testTag("home_sound_mute_toggle")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (isSoundEnabled) "Mute Sound" else "Unmute Sound",
                        tint = if (isSoundEnabled) Color(0xFF00E676) else Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Settings Button
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(32.dp).testTag("home_settings_button")
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GameModeCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradientColors))
                .border(1.5.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                color = accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MissionCardItem(
    mission: MissionEntity,
    isBn: Boolean,
    onClaim: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E293B))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isBn) mission.titleBn else mission.titleEn,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isBn) mission.descBn else mission.descEn,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (mission.currentProgress.toFloat() / mission.targetProgress).coerceIn(0f, 1f) },
                modifier = Modifier
                    .width(130.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFFFFD700),
                trackColor = Color(0xFF334155)
            )
        }

        if (mission.isClaimed) {
            Text(
                text = if (isBn) "ক্লেইমড ✓" else "Claimed ✓",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        } else if (mission.isCompleted) {
            Button(
                onClick = onClaim,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "+${mission.rewardCoins} 🪙",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = "${mission.currentProgress}/${mission.targetProgress}",
                color = Color(0xFFFFD700),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BetSelectorSheet(
    isBn: Boolean,
    userCoins: Long,
    onDismiss: () -> Unit,
    onSelectBet: (BetOption) -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isBn) "কয়েন বাজি নির্বাচন করুন" else "Select Coin Stake",
                    color = Color(0xFFFFD700),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBn) "বিজয়ী খেলোয়াড় সম্পূর্ণ প্রাইজ পট পাবেন" else "Winner takes the entire prize pot!",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BET_OPTIONS.forEach { bet ->
                        val canAfford = userCoins >= bet.entryCoins
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (canAfford) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.5f))
                                .border(1.dp, if (canAfford) Color(0xFFFFD700) else Color(0xFF475569), RoundedCornerShape(12.dp))
                                .clickable(enabled = canAfford) { onSelectBet(bet) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = bet.label,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) "এন্ট্রি ফি: ${bet.entryCoins} কয়েন" else "Entry: ${bet.entryCoins} Coins",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isBn) "প্রাইজ: +${bet.prizeCoins} 🪙" else "Prize: +${bet.prizeCoins} 🪙",
                                    color = Color(0xFFFFD700),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!canAfford) {
                                    Text(
                                        text = if (isBn) "কয়েন অপর্যাপ্ত" else "Not enough coins",
                                        color = Color(0xFFE53935),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isBn) "বাতিল করুন" else "Cancel", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LudoKingModeButton(
    title: String,
    liveCount: String,
    icon: String,
    bgGradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
        shadowElevation = 8.dp,
        modifier = modifier
            .height(108.dp)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(bgGradient))
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = icon, fontSize = 26.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = liveCount,
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun MiniGameCard(
    title: String,
    subtitle: String,
    icon: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor),
        shadowElevation = 4.dp,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}
