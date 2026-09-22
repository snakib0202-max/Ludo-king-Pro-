package com.example.game

import kotlin.random.Random

data class SixPlayerToken(
    val id: Int, // 0..3
    val color: PlayerColor,
    val stepCount: Int = 0 // 0 = Yard, 1..65 = Track, 66..71 = Home Runway, 72 = Finished Home
)

data class SixPlayerState(
    val players: List<PlayerInfo> = listOf(
        PlayerInfo(PlayerColor.RED, "আপনি (লাল)", "avatar_king", countryFlag = "🇧🇩", isLocalHuman = true, rankTitle = "Champion"),
        PlayerInfo(PlayerColor.GREEN, "আরিফ (সবুজ)", "avatar_boy2", countryFlag = "🇮🇳", isBot = true, rankTitle = "Pro"),
        PlayerInfo(PlayerColor.YELLOW, "সাদিয়া (হলুদ)", "avatar_girl1", countryFlag = "🇵🇰", isBot = true, rankTitle = "Master"),
        PlayerInfo(PlayerColor.BLUE, "রাতুল (নীল)", "avatar_boy3", countryFlag = "🇳🇬", isBot = true, rankTitle = "Elite"),
        PlayerInfo(PlayerColor.PURPLE, "সুমি (বেগুনি)", "avatar_girl2", countryFlag = "🇦🇱", isBot = true, rankTitle = "Expert"),
        PlayerInfo(PlayerColor.ORANGE, "কবীর (কমলা)", "avatar_boy4", countryFlag = "🇲🇾", isBot = true, rankTitle = "Veteran")
    ),
    val currentTurnIndex: Int = 0,
    val tokens: Map<PlayerColor, List<SixPlayerToken>> = listOf(
        PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE, PlayerColor.PURPLE, PlayerColor.ORANGE
    ).associateWith { col ->
        (0..3).map { SixPlayerToken(id = it, color = col, stepCount = 0) }
    },
    val currentDiceValue: Int? = null,
    val hasRolled: Boolean = false,
    val selectableTokenIds: Set<Int> = emptySet(),
    val winner: PlayerColor? = null,
    val eventMessage: String = "৬-প্লেয়ার মহা ম্যাচ শুরু হয়েছে! ডাইস রোল করুন।"
) {
    val currentPlayer: PlayerInfo
        get() = players[currentTurnIndex % players.size]
}

object SixPlayerLudoEngine {
    const val MAX_STEPS = 56 // Standard ludo run length to home

    fun rollDice(): Int = Random.nextInt(1, 7)

    fun calculateSelectableTokens(tokens: List<SixPlayerToken>, dice: Int): Set<Int> {
        val result = mutableSetOf<Int>()
        tokens.forEach { t ->
            if (t.stepCount == 0 && dice == 6) {
                result.add(t.id) // Can leave yard on a 6
            } else if (t.stepCount > 0 && t.stepCount + dice <= MAX_STEPS) {
                result.add(t.id)
            }
        }
        return result
    }

    fun moveToken(state: SixPlayerState, tokenId: Int): SixPlayerState {
        val dice = state.currentDiceValue ?: return state
        val color = state.currentPlayer.color
        val colorTokens = state.tokens[color] ?: return state

        val updatedColorTokens = colorTokens.map { token ->
            if (token.id == tokenId) {
                if (token.stepCount == 0 && dice == 6) {
                    token.copy(stepCount = 1) // Enters track
                } else {
                    token.copy(stepCount = token.stepCount + dice)
                }
            } else token
        }

        val allTokens = state.tokens.toMutableMap()
        allTokens[color] = updatedColorTokens

        // Check if all 4 tokens reached home
        val hasWon = updatedColorTokens.all { it.stepCount >= MAX_STEPS }
        val winner = if (hasWon) color else null

        val grantExtraTurn = dice == 6 && winner == null
        val nextTurnIndex = if (grantExtraTurn) state.currentTurnIndex else (state.currentTurnIndex + 1) % state.players.size

        val msg = if (winner != null) {
            "🎉 অভিনন্দন! ${state.currentPlayer.name} খেলায় বিজয়ী হয়েছেন!"
        } else if (grantExtraTurn) {
            "${state.currentPlayer.name} ছক্কার জন্য অতিরিক্ত চাল পেয়েছেন!"
        } else {
            "${state.currentPlayer.name} চাল সম্পন্ন করেছেন।"
        }

        return state.copy(
            tokens = allTokens,
            currentDiceValue = null,
            hasRolled = false,
            selectableTokenIds = emptySet(),
            currentTurnIndex = nextTurnIndex,
            winner = winner,
            eventMessage = msg
        )
    }
}
