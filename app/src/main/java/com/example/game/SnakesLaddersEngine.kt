package com.example.game

import kotlin.random.Random

data class SnakesPlayer(
    val color: PlayerColor,
    val name: String,
    val isBot: Boolean = false,
    val countryFlag: String = "🇧🇩"
)

data class SnakesLaddersState(
    val players: List<SnakesPlayer> = listOf(
        SnakesPlayer(PlayerColor.RED, "আপনি (লাল)", isBot = false, countryFlag = "🇧🇩"),
        SnakesPlayer(PlayerColor.GREEN, "বট সবুজ", isBot = true, countryFlag = "🇮🇳"),
        SnakesPlayer(PlayerColor.YELLOW, "বট হলুদ", isBot = true, countryFlag = "🇵🇰"),
        SnakesPlayer(PlayerColor.BLUE, "বট নীল", isBot = true, countryFlag = "🇳🇬")
    ),
    val currentTurnIndex: Int = 0,
    val positions: Map<PlayerColor, Int> = mapOf(
        PlayerColor.RED to 1,
        PlayerColor.GREEN to 1,
        PlayerColor.YELLOW to 1,
        PlayerColor.BLUE to 1
    ),
    val currentDiceValue: Int? = null,
    val hasRolled: Boolean = false,
    val winner: PlayerColor? = null,
    val eventMessageBn: String = "সাপ-লুডু শুরু হয়েছে! ডাইস রোল করুন।",
    val isSnakeBite: Boolean = false,
    val isLadderClimb: Boolean = false
) {
    val currentPlayer: SnakesPlayer
        get() = players[currentTurnIndex % players.size]
}

object SnakesLaddersEngine {
    // Snakes: Head -> Tail
    val SNAKES: Map<Int, Int> = mapOf(
        98 to 28,
        95 to 56,
        92 to 51,
        83 to 19,
        73 to 15,
        69 to 33,
        64 to 36,
        59 to 17,
        52 to 11,
        48 to 9,
        46 to 5
    )

    // Ladders: Bottom -> Top
    val LADDERS: Map<Int, Int> = mapOf(
        4 to 14,
        9 to 31,
        20 to 38,
        28 to 84,
        40 to 59,
        51 to 67,
        63 to 81,
        71 to 91
    )

    fun rollDice(): Int = Random.nextInt(1, 7)

    fun handleRoll(state: SnakesLaddersState, rolledValue: Int): SnakesLaddersState {
        if (state.winner != null) return state

        val player = state.currentPlayer
        val currentPos = state.positions[player.color] ?: 1
        var newPos = currentPos + rolledValue

        var snakeBite = false
        var ladderClimb = false
        var msg = "${player.name} $rolledValue ফেলেছে।"

        if (newPos > 100) {
            // Cannot overshoot 100
            newPos = currentPos
            msg += " ১০০ পার হওয়ার কারণে চাল বাতিল!"
        } else if (newPos == 100) {
            msg = "🎉 অভিনন্দন! ${player.name} ১০০ ঘরে পৌঁছে বিজয়ী হয়েছে!"
            val updatedPositions = state.positions.toMutableMap()
            updatedPositions[player.color] = 100
            return state.copy(
                positions = updatedPositions,
                currentDiceValue = rolledValue,
                hasRolled = true,
                winner = player.color,
                eventMessageBn = msg,
                isSnakeBite = false,
                isLadderClimb = false
            )
        } else {
            // Check Snake
            if (SNAKES.containsKey(newPos)) {
                val tail = SNAKES[newPos]!!
                msg = "🐍 ওহ না! $newPos ঘরে সাপে কেটে $tail ঘরে নামিয়ে দিলো!"
                newPos = tail
                snakeBite = true
            } else if (LADDERS.containsKey(newPos)) {
                val top = LADDERS[newPos]!!
                msg = "🪜 দারুণ! $newPos ঘরে মই পেয়ে $top ঘরে উঠে গেল!"
                newPos = top
                ladderClimb = true
            }
        }

        val updatedPositions = state.positions.toMutableMap()
        updatedPositions[player.color] = newPos

        // Determine next turn (if rolled 6 and not won, extra turn)
        val grantExtraTurn = rolledValue == 6 && state.winner == null
        val nextTurnIndex = if (grantExtraTurn) state.currentTurnIndex else (state.currentTurnIndex + 1) % state.players.size

        if (grantExtraTurn) {
            msg += " ছক্কার জন্য অতিরিক্ত চাল!"
        }

        return state.copy(
            positions = updatedPositions,
            currentDiceValue = rolledValue,
            hasRolled = true,
            currentTurnIndex = nextTurnIndex,
            eventMessageBn = msg,
            isSnakeBite = snakeBite,
            isLadderClimb = ladderClimb
        )
    }

    /**
     * Converts a cell number 1..100 to grid coordinate (row, col)
     * Row 0 is at bottom (1..10), Row 9 is at top (100..91)
     */
    fun cellToRowCol(cell: Int): Pair<Int, Int> {
        val clamped = cell.coerceIn(1, 100)
        val zeroIndex = clamped - 1
        val rowFromBottom = zeroIndex / 10
        val col = if (rowFromBottom % 2 == 0) {
            zeroIndex % 10 // Left to right
        } else {
            9 - (zeroIndex % 10) // Right to left
        }
        val row = 9 - rowFromBottom // Top is row 0 in Compose Canvas/Grid
        return Pair(row, col)
    }
}
