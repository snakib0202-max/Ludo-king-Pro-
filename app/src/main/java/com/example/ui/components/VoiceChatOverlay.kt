package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ChatMessage
import com.example.game.DEDICATED_EMOJIS
import com.example.game.PlayerColor
import com.example.game.PlayerInfo

data class EmojiReactionItem(
    val emoji: String,
    val labelBn: String
)

data class EmojiReactionCategory(
    val id: String,
    val titleBn: String,
    val icon: String,
    val emojis: List<EmojiReactionItem>
)

val MATCH_EMOJI_CATEGORIES = listOf(
    EmojiReactionCategory(
        id = "mood",
        titleBn = "অনুভূতি",
        icon = "😂",
        emojis = listOf(
            EmojiReactionItem("😂", "হাসি"),
            EmojiReactionItem("🤣", "লোটপোট"),
            EmojiReactionItem("😎", "সোয়াগ"),
            EmojiReactionItem("🥳", "উৎসব"),
            EmojiReactionItem("🤩", "স্টার"),
            EmojiReactionItem("😭", "কান্না"),
            EmojiReactionItem("😡", "রাগ"),
            EmojiReactionItem("😱", "চমক"),
            EmojiReactionItem("🥺", "অনুরোধ"),
            EmojiReactionItem("🤯", "অবাক"),
            EmojiReactionItem("😴", "ঘুম"),
            EmojiReactionItem("🤐", "চুপ"),
            EmojiReactionItem("😈", "শয়তানি"),
            EmojiReactionItem("😇", "সাধু"),
            EmojiReactionItem("😍", "লাভ"),
            EmojiReactionItem("😜", "মজা")
        )
    ),
    EmojiReactionCategory(
        id = "match",
        titleBn = "ম্যাচ ও চাল",
        icon = "🎲",
        emojis = listOf(
            EmojiReactionItem("🎲", "ছক্কা"),
            EmojiReactionItem("🔥", "আগুন"),
            EmojiReactionItem("👑", "রাজা"),
            EmojiReactionItem("🏆", "ট্রফি"),
            EmojiReactionItem("🎯", "টার্গেট"),
            EmojiReactionItem("💥", "কাটা"),
            EmojiReactionItem("💣", "বোমা"),
            EmojiReactionItem("💀", "খতম"),
            EmojiReactionItem("🍅", "টমেটো"),
            EmojiReactionItem("💩", "দুর্ভাগ্য"),
            EmojiReactionItem("⚡", "ঝড়"),
            EmojiReactionItem("⏳", "দেরি"),
            EmojiReactionItem("⚔️", "যুদ্ধ"),
            EmojiReactionItem("🚀", "রকেট"),
            EmojiReactionItem("🥇", "ফার্স্ট"),
            EmojiReactionItem("🪙", "কয়েন")
        )
    ),
    EmojiReactionCategory(
        id = "sportsmanship",
        titleBn = "শুভেচ্ছা",
        icon = "👏",
        emojis = listOf(
            EmojiReactionItem("👏", "সাবাশ"),
            EmojiReactionItem("👍", "লাইক"),
            EmojiReactionItem("👎", "ডিসলাইক"),
            EmojiReactionItem("🤝", "বন্ধুত্ব"),
            EmojiReactionItem("🙏", "দোয়া"),
            EmojiReactionItem("💪", "শক্তি"),
            EmojiReactionItem("👋", "বিদায়"),
            EmojiReactionItem("❤️", "ভালোবাসা"),
            EmojiReactionItem("💔", "ভাঙা মন"),
            EmojiReactionItem("💯", "১০০%"),
            EmojiReactionItem("🎉", "পার্টি"),
            EmojiReactionItem("⭐", "স্টার"),
            EmojiReactionItem("✌️", "শান্তি"),
            EmojiReactionItem("🤞", "লাক"),
            EmojiReactionItem("🫡", "সালাম"),
            EmojiReactionItem("🍿", "পপকর্ন")
        )
    ),
    EmojiReactionCategory(
        id = "banter",
        titleBn = "ব্যঙ্গ ও কৌশল",
        icon = "🤫",
        emojis = listOf(
            EmojiReactionItem("🤫", "চুপচাপ"),
            EmojiReactionItem("🥱", "বোরিং"),
            EmojiReactionItem("🏃", "পালাও"),
            EmojiReactionItem("💨", "উধাও"),
            EmojiReactionItem("👀", "নজর"),
            EmojiReactionItem("🥶", "শীতল"),
            EmojiReactionItem("🥵", "ঘেমে গেছি"),
            EmojiReactionItem("🤑", "লোভ"),
            EmojiReactionItem("🤡", "জোকার"),
            EmojiReactionItem("👻", "ভূত"),
            EmojiReactionItem("🚨", "অ্যালার্ট"),
            EmojiReactionItem("🛡️", "সেইফ")
        )
    )
)

val POPULAR_QUICK_REACTIONS = listOf(
    "🔥", "😂", "👏", "😭", "😎", "😡", "🎲", "👑", "👍", "🥳", "🤫", "💀", "😱", "💪", "🍅"
)

@Composable
fun VoiceChatBar(
    isMicOn: Boolean,
    isSpeakerOn: Boolean,
    speakingPlayerName: String?,
    players: List<PlayerInfo>,
    messages: List<ChatMessage> = emptyList(),
    onToggleMic: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onSendEmoji: (String) -> Unit,
    onDedicateEmoji: (emoji: String, targetColor: PlayerColor, targetName: String) -> Unit,
    onSendCustomText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickChat by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(1) } // Default to Real-Time Chat
    var showEmojiPanel by remember { mutableStateOf(false) }
    var selectedEmojiCategoryIndex by remember { mutableIntStateOf(0) }
    var isInstantReactionMode by remember { mutableStateOf(true) }
    var customText by remember { mutableStateOf("") }

    // Opponent players (excluding Red / local user)
    val opponentPlayers = remember(players) {
        players.filter { it.color != PlayerColor.RED }
    }
    var selectedOpponent by remember(opponentPlayers) {
        mutableStateOf(opponentPlayers.firstOrNull() ?: players.firstOrNull())
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Voice Chat Pill and Chat Toggle Pill Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Chat controls pill
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mic Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isMicOn) Color(0xFF00E676) else Color(0xFF475569))
                            .clickable { onToggleMic() }
                            .testTag("voice_chat_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "Voice Chat Mic",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Waveform equalizer animation when mic is active
                    if (isMicOn) {
                        VoiceWaveformAnimation()
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Speaker Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSpeakerOn) Color(0xFF3B82F6) else Color(0xFF475569))
                            .clickable { onToggleSpeaker() }
                            .testTag("voice_chat_speaker_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Speaker indicator text if someone is talking
                    if (speakingPlayerName != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$speakingPlayerName 🗣️",
                            color = Color(0xFF4ADE80),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Chat & Real-Time Emoji Toggle Pill
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (showQuickChat) Color(0xFFFFD700) else Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFFD700)),
                modifier = Modifier
                    .clickable { showQuickChat = !showQuickChat }
                    .testTag("chat_overlay_toggle_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showQuickChat) "বন্ধ ✕" else "💬 চ্যাট ও ইমোজি",
                        color = if (showQuickChat) Color(0xFF0F172A) else Color(0xFFFFD700),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Quick Chat & Emoji Dedication Drawer
        AnimatedVisibility(
            visible = showQuickChat,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8)),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Navigation Tabs: 1. লাইভ চ্যাট ও ইমোজি | 2. ইমোজি উৎসর্গ
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFFFD700),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color(0xFFFFD700),
                                height = 2.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "💬 লাইভ চ্যাট ও ইমোজি",
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == 1) Color(0xFFFFD700) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    text = "🎯 ইমোজি উৎসর্গ (Throw)",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 0) Color(0xFFFFD700) else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedTab == 1) {
                        // ==========================================
                        // TAB 1: REAL-TIME MATCH CHAT & EMOJI PANEL
                        // ==========================================

                        // 1. In-Game Chat Feed (Last 3-4 messages)
                        if (messages.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1E293B).copy(alpha = 0.85f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 84.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(messages.takeLast(4)) { msg ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            val badgeColor = when (msg.senderColor) {
                                                PlayerColor.RED -> Color(0xFFEF4444)
                                                PlayerColor.GREEN -> Color(0xFF10B981)
                                                PlayerColor.YELLOW -> Color(0xFFFBBF24)
                                                PlayerColor.BLUE -> Color(0xFF3B82F6)
                                                PlayerColor.PURPLE -> Color(0xFF9333EA)
                                                PlayerColor.ORANGE -> Color(0xFFF97316)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(badgeColor)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "${msg.senderName}: ",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = msg.text,
                                                color = if (msg.isEmoji) Color(0xFFFFD700) else Color.White,
                                                fontSize = if (msg.isEmoji && msg.text.length <= 2) 16.sp else 11.sp,
                                                fontWeight = if (msg.isEmoji) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // 2. Quick Reactions Bar (Always accessible one-tap bar)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "তাৎক্ষণিক প্রতিক্রিয়া:",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Toggle button to open/close Full Emoji Selection Panel
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (showEmojiPanel) Color(0xFFFFD700) else Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (showEmojiPanel) Color(0xFFFFD700) else Color(0xFF38BDF8)
                                ),
                                modifier = Modifier
                                    .clickable { showEmojiPanel = !showEmojiPanel }
                                    .testTag("chat_emoji_panel_toggle")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (showEmojiPanel) "⌨️ বন্ধ প্যানেল" else "😊 সব ইমোজি ▾",
                                        fontSize = 10.sp,
                                        color = if (showEmojiPanel) Color(0xFF0F172A) else Color(0xFF38BDF8),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Horizontal Row of Quick Emojis
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(POPULAR_QUICK_REACTIONS) { emo ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B))
                                        .border(1.dp, Color(0xFF334155), CircleShape)
                                        .clickable {
                                            onSendEmoji(emo)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emo, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. Expandable Full Emoji Selection Panel
                        AnimatedVisibility(
                            visible = showEmojiPanel,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    // Header: Mode Switcher & Close
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Mode switch: Instant Send vs Insert into Text
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFF0F172A))
                                                .padding(2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(if (isInstantReactionMode) Color(0xFFFFD700) else Color.Transparent)
                                                    .clickable { isInstantReactionMode = true }
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "⚡ সাথে সাথে পাঠান",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isInstantReactionMode) Color(0xFF0F172A) else Color(0xFF94A3B8)
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(if (!isInstantReactionMode) Color(0xFF38BDF8) else Color.Transparent)
                                                    .clickable { isInstantReactionMode = false }
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "✏️ মেসেজে যোগ",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (!isInstantReactionMode) Color(0xFF0F172A) else Color(0xFF94A3B8)
                                                )
                                            }
                                        }

                                        // Close panel icon
                                        IconButton(
                                            onClick = { showEmojiPanel = false },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close Emoji Panel",
                                                tint = Color(0xFF94A3B8),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Category Filter Tabs
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(MATCH_EMOJI_CATEGORIES.indices.toList()) { index ->
                                            val cat = MATCH_EMOJI_CATEGORIES[index]
                                            val isSelected = selectedEmojiCategoryIndex == index
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (isSelected) Color(0xFF334155) else Color(0xFF0F172A),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (isSelected) Color(0xFFFFD700) else Color(0xFF334155)
                                                ),
                                                modifier = Modifier.clickable { selectedEmojiCategoryIndex = index }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = cat.icon, fontSize = 12.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = cat.titleBn,
                                                        color = if (isSelected) Color(0xFFFFD700) else Color(0xFF94A3B8),
                                                        fontSize = 10.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Category Emojis Grid
                                    val currentCategory = MATCH_EMOJI_CATEGORIES[selectedEmojiCategoryIndex]
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(5),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                    ) {
                                        items(currentCategory.emojis) { item ->
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF0F172A))
                                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        if (isInstantReactionMode) {
                                                            onSendEmoji(item.emoji)
                                                        } else {
                                                            customText += item.emoji
                                                        }
                                                    }
                                                    .padding(vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = item.emoji,
                                                    fontSize = 22.sp
                                                )
                                                Text(
                                                    text = item.labelBn,
                                                    fontSize = 8.sp,
                                                    color = Color(0xFF94A3B8),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 4. Message Input & Action Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Emoji toggle button inside text bar
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (showEmojiPanel) Color(0xFFFFD700) else Color(0xFF1E293B))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                    .clickable { showEmojiPanel = !showEmojiPanel },
                                contentAlignment = Alignment.Center
                            ) {
                                if (showEmojiPanel) {
                                    Icon(
                                        imageVector = Icons.Default.Keyboard,
                                        contentDescription = "Show Keyboard",
                                        tint = Color(0xFF0F172A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(text = "😊", fontSize = 20.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            OutlinedTextField(
                                value = customText,
                                onValueChange = { customText = it },
                                placeholder = {
                                    Text(
                                        text = "মেসেজ বা প্রতিক্রিয়া লিখুন...",
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFD700),
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (customText.isNotBlank()) Color(0xFFFFD700) else Color(0xFF334155))
                                    .clickable(enabled = customText.isNotBlank()) {
                                        onSendCustomText(customText)
                                        customText = ""
                                        showEmojiPanel = false
                                        showQuickChat = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = if (customText.isNotBlank()) Color.Black else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 5. Quick Preset Text Chips
                        val quickMessages = listOf(
                            "ভালো খেলেছেন! 👏",
                            "তাড়াতাড়ি চালুন! ⏳",
                            "ছক্কা চাই! 🎲",
                            "দারুণ চাল! 🔥",
                            "ধন্যবাদ! 😊",
                            "Well Played! 👍",
                            "চ্যালেঞ্জ কবুল! ⚔️"
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickMessages) { msg ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E293B))
                                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                        .clickable {
                                            onSendCustomText(msg)
                                            showQuickChat = false
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(text = msg, color = Color.White, fontSize = 10.sp)
                                    }
                            }
                        }
                    } else {
                        // ==========================================
                        // TAB 0: TARGETED EMOJI DEDICATION (THROW)
                        // ==========================================
                        Text(
                            text = "🎯 কাকে পাঠাবেন (Choose Opponent):",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(opponentPlayers) { p ->
                                val isSelected = selectedOpponent?.color == p.color
                                val pColor = when (p.color) {
                                    PlayerColor.GREEN -> Color(0xFF00C853)
                                    PlayerColor.YELLOW -> Color(0xFFFFD700)
                                    PlayerColor.BLUE -> Color(0xFF2979FF)
                                    else -> Color(0xFFE53935)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) pColor.copy(alpha = 0.35f) else Color(0xFF1E293B))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) pColor else Color(0xFF334155),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedOpponent = p }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(pColor)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = p.name,
                                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dedicated Emojis Grid (10 interactive emojis)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DEDICATED_EMOJIS.take(5).forEach { de ->
                                EmojiDedicateItem(
                                    emoji = de.emoji,
                                    label = de.nameBn,
                                    onClick = {
                                        val target = selectedOpponent ?: opponentPlayers.firstOrNull()
                                        if (target != null) {
                                            onDedicateEmoji(de.emoji, target.color, target.name)
                                        } else {
                                            onSendEmoji(de.emoji)
                                        }
                                        showQuickChat = false
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DEDICATED_EMOJIS.drop(5).forEach { de ->
                                EmojiDedicateItem(
                                    emoji = de.emoji,
                                    label = de.nameBn,
                                    onClick = {
                                        val target = selectedOpponent ?: opponentPlayers.firstOrNull()
                                        if (target != null) {
                                            onDedicateEmoji(de.emoji, target.color, target.name)
                                        } else {
                                            onSendEmoji(de.emoji)
                                        }
                                        showQuickChat = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmojiDedicateItem(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(1.dp, Color(0xFF334155), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 9.sp)
    }
}

@Composable
fun VoiceWaveformAnimation() {
    val transition = rememberInfiniteTransition(label = "waveform")
    val bar1 by transition.animateFloat(
        initialValue = 4f, targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(250, easing = LinearEasing), RepeatMode.Reverse),
        label = "bar1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 16f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "bar2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 8f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(200, easing = LinearEasing), RepeatMode.Reverse),
        label = "bar3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(20.dp)
    ) {
        Box(modifier = Modifier.width(3.dp).height(bar1.dp).background(Color(0xFF00E676), RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(3.dp).height(bar2.dp).background(Color(0xFF00E676), RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(3.dp).height(bar3.dp).background(Color(0xFF00E676), RoundedCornerShape(1.dp)))
    }
}
