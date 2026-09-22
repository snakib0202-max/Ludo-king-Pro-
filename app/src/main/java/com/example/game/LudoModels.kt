package com.example.game

enum class PlayerColor(
    val titleBn: String,
    val titleEn: String,
    val baseColorHex: Long,
    val lightColorHex: Long,
    val darkColorHex: Long,
    val startCellIndex: Int, // index on 52-cell track
    val homeEntryCellIndex: Int // index on 52-cell track where player turns into home stretch
) {
    RED(
        titleBn = "লাল",
        titleEn = "Red",
        baseColorHex = 0xFFE53935,
        lightColorHex = 0xFFFFCDD2,
        darkColorHex = 0xFFB71C1C,
        startCellIndex = 1,
        homeEntryCellIndex = 51
    ),
    GREEN(
        titleBn = "সবুজ",
        titleEn = "Green",
        baseColorHex = 0xFF43A047,
        lightColorHex = 0xFFC8E6C9,
        darkColorHex = 0xFF1B5E20,
        startCellIndex = 14,
        homeEntryCellIndex = 12
    ),
    YELLOW(
        titleBn = "হলুদ",
        titleEn = "Yellow",
        baseColorHex = 0xFFFDD835,
        lightColorHex = 0xFFFFF9C4,
        darkColorHex = 0xFFF57F17,
        startCellIndex = 27,
        homeEntryCellIndex = 25
    ),
    BLUE(
        titleBn = "নীল",
        titleEn = "Blue",
        baseColorHex = 0xFF1E88E5,
        lightColorHex = 0xFFBBDEFB,
        darkColorHex = 0xFF0D47A1,
        startCellIndex = 40,
        homeEntryCellIndex = 38
    ),
    PURPLE(
        titleBn = "বেগুনি",
        titleEn = "Purple",
        baseColorHex = 0xFF8E24AA,
        lightColorHex = 0xFFE1BEE7,
        darkColorHex = 0xFF4A148C,
        startCellIndex = 44,
        homeEntryCellIndex = 42
    ),
    ORANGE(
        titleBn = "কমলা",
        titleEn = "Orange",
        baseColorHex = 0xFFFB8C00,
        lightColorHex = 0xFFFFE0B2,
        darkColorHex = 0xFFE65100,
        startCellIndex = 30,
        homeEntryCellIndex = 28
    )
}

enum class TokenState {
    IN_YARD,
    ON_TRACK,
    IN_HOME_STRETCH,
    HOME
}

data class Token(
    val id: Int, // 0..3
    val color: PlayerColor,
    val state: TokenState = TokenState.IN_YARD,
    val stepCount: Int = 0 // 0 = In Yard, 1..51 = track progress, 52..56 = home stretch, 57 = Home
)

enum class GameMode(val titleBn: String, val titleEn: String) {
    ONLINE_MULTIPLAYER("অনলাইন মাল্টিপ্লেয়ার", "Online Multiplayer"),
    PLAY_WITH_FRIENDS("বন্ধুদের সাথে", "Play with Friends"),
    VS_COMPUTER("কম্পিউটার (বট)", "vs Computer AI"),
    PASS_AND_PLAY("পাস অ্যান্ড প্লে", "Pass & Play"),
    SIX_PLAYER_LUDO("৬-প্লেয়ার লুডু", "6-Player Ludo"),
    SNAKES_AND_LADDERS("সাপ-লুডু", "Snakes & Ladders"),
    ONE_VS_ONE_AUDIO("১v১ অডিও ফেস-অফ", "1v1 Audio Battle"),
    TOURNAMENT("টুর্নামেন্ট", "Tournament")
}

data class BetOption(
    val entryCoins: Long,
    val prizeCoins: Long,
    val label: String
)

val BET_OPTIONS = listOf(
    BetOption(500L, 950L, "500 Coins"),
    BetOption(2000L, 3800L, "2,000 Coins"),
    BetOption(10000L, 19000L, "10K Coins"),
    BetOption(50000L, 95000L, "50K Coins")
)

data class PlayerInfo(
    val color: PlayerColor,
    val name: String,
    val avatarId: String,
    val countryFlag: String = "🇧🇩",
    val countryName: String = "Bangladesh",
    val isBot: Boolean = false,
    val isLocalHuman: Boolean = false,
    val isSpeaking: Boolean = false,
    val rankTitle: String = "Pro Player",
    val tokensHome: Int = 0,
    val isWinner: Boolean = false
)

data class BoardCell(
    val col: Int,
    val row: Int,
    val isSafeStar: Boolean = false,
    val colorHint: PlayerColor? = null
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val senderName: String,
    val senderColor: PlayerColor,
    val text: String,
    val isEmoji: Boolean = false
)
