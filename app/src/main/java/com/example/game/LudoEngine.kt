package com.example.game

data class Coordinate(val col: Int, val row: Int)

object LudoTrackData {
    // 52 common perimeter cells in clockwise order
    val COMMON_TRACK = listOf(
        Coordinate(1, 6),   // 0: Red Start [Safe Star]
        Coordinate(2, 6),   // 1
        Coordinate(3, 6),   // 2
        Coordinate(4, 6),   // 3
        Coordinate(5, 6),   // 4
        Coordinate(6, 5),   // 5
        Coordinate(6, 4),   // 6
        Coordinate(6, 3),   // 7
        Coordinate(6, 2),   // 8: Safe Star
        Coordinate(6, 1),   // 9
        Coordinate(6, 0),   // 10
        Coordinate(7, 0),   // 11
        Coordinate(8, 0),   // 12
        Coordinate(8, 1),   // 13: Green Start [Safe Star]
        Coordinate(8, 2),   // 14
        Coordinate(8, 3),   // 15
        Coordinate(8, 4),   // 16
        Coordinate(8, 5),   // 17
        Coordinate(9, 6),   // 18
        Coordinate(10, 6),  // 19
        Coordinate(11, 6),  // 20
        Coordinate(12, 6),  // 21: Safe Star
        Coordinate(13, 6),  // 22
        Coordinate(14, 6),  // 23
        Coordinate(14, 7),  // 24
        Coordinate(14, 8),  // 25
        Coordinate(13, 8),  // 26: Yellow Start [Safe Star]
        Coordinate(12, 8),  // 27
        Coordinate(11, 8),  // 28
        Coordinate(10, 8),  // 29
        Coordinate(9, 8),   // 30
        Coordinate(8, 9),   // 31
        Coordinate(8, 10),  // 32
        Coordinate(8, 11),  // 33
        Coordinate(8, 12),  // 34: Safe Star
        Coordinate(8, 13),  // 35
        Coordinate(8, 14),  // 36
        Coordinate(7, 14),  // 37
        Coordinate(6, 14),  // 38
        Coordinate(6, 13),  // 39: Blue Start [Safe Star]
        Coordinate(6, 12),  // 40
        Coordinate(6, 11),  // 41
        Coordinate(6, 10),  // 42
        Coordinate(6, 9),   // 43
        Coordinate(5, 8),   // 44
        Coordinate(4, 8),   // 45
        Coordinate(3, 8),   // 46
        Coordinate(2, 8),   // 47: Safe Star
        Coordinate(1, 8),   // 48
        Coordinate(0, 8),   // 49
        Coordinate(0, 7),   // 50: Red Home Entry turn
        Coordinate(0, 6)    // 51
    )

    val SAFE_CELL_INDICES = setOf(0, 8, 13, 21, 26, 34, 39, 47)

    val HOME_PATHS = mapOf(
        PlayerColor.RED to listOf(
            Coordinate(1, 7),
            Coordinate(2, 7),
            Coordinate(3, 7),
            Coordinate(4, 7),
            Coordinate(5, 7),
            Coordinate(6, 7) // Home center
        ),
        PlayerColor.GREEN to listOf(
            Coordinate(7, 1),
            Coordinate(7, 2),
            Coordinate(7, 3),
            Coordinate(7, 4),
            Coordinate(7, 5),
            Coordinate(7, 6) // Home center
        ),
        PlayerColor.YELLOW to listOf(
            Coordinate(13, 7),
            Coordinate(12, 7),
            Coordinate(11, 7),
            Coordinate(10, 7),
            Coordinate(9, 7),
            Coordinate(8, 7) // Home center
        ),
        PlayerColor.BLUE to listOf(
            Coordinate(7, 13),
            Coordinate(7, 12),
            Coordinate(7, 11),
            Coordinate(7, 10),
            Coordinate(7, 9),
            Coordinate(7, 8) // Home center
        )
    )

    // Base yard pocket coordinates for 4 tokens (Authentic Ludo King layout)
    val YARD_COORDINATES = mapOf(
        PlayerColor.RED to listOf(
            Coordinate(1, 1),
            Coordinate(4, 1),
            Coordinate(1, 4),
            Coordinate(4, 4)
        ),
        PlayerColor.GREEN to listOf(
            Coordinate(10, 1),
            Coordinate(13, 1),
            Coordinate(10, 4),
            Coordinate(13, 4)
        ),
        PlayerColor.YELLOW to listOf(
            Coordinate(10, 10),
            Coordinate(13, 10),
            Coordinate(10, 13),
            Coordinate(13, 13)
        ),
        PlayerColor.BLUE to listOf(
            Coordinate(1, 10),
            Coordinate(4, 10),
            Coordinate(1, 13),
            Coordinate(4, 13)
        )
    )

    fun getTrackIndexForColor(color: PlayerColor, stepCount: Int): Int {
        val startOffset = when (color) {
            PlayerColor.RED -> 0
            PlayerColor.GREEN -> 13
            PlayerColor.YELLOW -> 26
            PlayerColor.BLUE -> 39
            else -> 0
        }
        return (startOffset + stepCount) % 52
    }

    fun getTokenCoordinate(token: Token): Coordinate {
        return when (token.state) {
            TokenState.IN_YARD -> {
                val coords = YARD_COORDINATES[token.color] ?: YARD_COORDINATES[PlayerColor.RED]!!
                coords[token.id.coerceIn(0, 3)]
            }
            TokenState.ON_TRACK -> {
                val trackIndex = getTrackIndexForColor(token.color, token.stepCount)
                COMMON_TRACK[trackIndex]
            }
            TokenState.IN_HOME_STRETCH, TokenState.HOME -> {
                val path = HOME_PATHS[token.color] ?: HOME_PATHS[PlayerColor.RED]!!
                val homeIndex = (token.stepCount - 51).coerceIn(0, path.size - 1)
                path[homeIndex]
            }
        }
    }
}

data class GameState(
    val mode: GameMode = GameMode.ONLINE_MULTIPLAYER,
    val betAmount: Long = 500L,
    val potAmount: Long = 950L,
    val players: List<PlayerInfo> = emptyList(),
    val tokens: Map<PlayerColor, List<Token>> = emptyMap(),
    val currentTurnColor: PlayerColor = PlayerColor.RED,
    val currentDiceValue: Int? = null,
    val diceRollHash: String = "",
    val hasRolled: Boolean = false,
    val consecutiveSixes: Int = 0,
    val canSelectToken: Boolean = false,
    val selectableTokenIds: Set<Int> = emptySet(),
    val winner: PlayerColor? = null,
    val turnTimerSeconds: Int = 15,
    val fairPlaySession: FairPlaySession = FairPlaySecurity.generateSession(),
    val lastMoveDescriptionBn: String = "খেলা শুরু হয়েছে!",
    val lastMoveDescriptionEn: String = "Game started!",
    val rollIndex: Int = 0,
    val messages: List<ChatMessage> = emptyList(),
    val isVoiceChatActive: Boolean = false
)

object LudoEngine {

    fun initGame(mode: GameMode, betOption: BetOption, userAvatarId: String, userName: String): GameState {
        val players = when (mode) {
            GameMode.ONLINE_MULTIPLAYER -> listOf(
                PlayerInfo(PlayerColor.RED, userName, userAvatarId, countryFlag = "🇧🇩", countryName = "Bangladesh", isLocalHuman = true, rankTitle = "Master"),
                PlayerInfo(PlayerColor.GREEN, "আরিফ খান", "avatar_boy2", countryFlag = "🇮🇳", countryName = "India", isBot = true, rankTitle = "Pro"),
                PlayerInfo(PlayerColor.YELLOW, "সাদিয়া রহমান", "avatar_girl1", countryFlag = "🇵🇰", countryName = "Pakistan", isBot = true, rankTitle = "Champion"),
                PlayerInfo(PlayerColor.BLUE, "রাতুল ইসলাম", "avatar_boy3", countryFlag = "🇳🇬", countryName = "Nigeria", isBot = true, rankTitle = "Grandmaster")
            )
            GameMode.PLAY_WITH_FRIENDS -> listOf(
                PlayerInfo(PlayerColor.RED, userName, userAvatarId, countryFlag = "🇧🇩", countryName = "Bangladesh", isLocalHuman = true, rankTitle = "Host"),
                PlayerInfo(PlayerColor.GREEN, "বন্ধু তানভীর", "avatar_boy1", countryFlag = "🇧🇩", countryName = "Bangladesh", isBot = true, rankTitle = "Friend"),
                PlayerInfo(PlayerColor.YELLOW, "বন্ধু নুসরাত", "avatar_girl2", countryFlag = "🇧🇩", countryName = "Bangladesh", isBot = true, rankTitle = "Friend"),
                PlayerInfo(PlayerColor.BLUE, "বন্ধু সাকিব", "avatar_boy4", countryFlag = "🇧🇩", countryName = "Bangladesh", isBot = true, rankTitle = "Friend")
            )
            GameMode.VS_COMPUTER -> listOf(
                PlayerInfo(PlayerColor.RED, userName, userAvatarId, countryFlag = "🇧🇩", countryName = "Bangladesh", isLocalHuman = true, rankTitle = "Player"),
                PlayerInfo(PlayerColor.GREEN, "AI সবুজ", "avatar_bot1", countryFlag = "🤖", countryName = "Bot", isBot = true, rankTitle = "Smart AI"),
                PlayerInfo(PlayerColor.YELLOW, "AI হলুদ", "avatar_bot2", countryFlag = "🤖", countryName = "Bot", isBot = true, rankTitle = "Smart AI"),
                PlayerInfo(PlayerColor.BLUE, "AI নীল", "avatar_bot3", countryFlag = "🤖", countryName = "Bot", isBot = true, rankTitle = "Smart AI")
            )
            GameMode.PASS_AND_PLAY -> listOf(
                PlayerInfo(PlayerColor.RED, "প্লেয়ার ১ (লাল)", "avatar_king", countryFlag = "🇧🇩", isLocalHuman = true),
                PlayerInfo(PlayerColor.GREEN, "প্লেয়ার ২ (সবুজ)", "avatar_boy2", countryFlag = "🇮🇳", isLocalHuman = true),
                PlayerInfo(PlayerColor.YELLOW, "প্লেয়ার ৩ (হলুদ)", "avatar_girl1", countryFlag = "🇵🇰", isLocalHuman = true),
                PlayerInfo(PlayerColor.BLUE, "প্লেয়ার ৪ (নীল)", "avatar_boy3", countryFlag = "🇳🇬", isLocalHuman = true)
            )
            GameMode.ONE_VS_ONE_AUDIO -> listOf(
                PlayerInfo(PlayerColor.RED, userName, userAvatarId, countryFlag = "🇧🇩", countryName = "Bangladesh", isLocalHuman = true, rankTitle = "Pro Duelist"),
                PlayerInfo(PlayerColor.YELLOW, "ফারহানা জাহান", "avatar_girl1", countryFlag = "🇮🇳", countryName = "India", isBot = true, isSpeaking = true, rankTitle = "Audio Master")
            )
            GameMode.TOURNAMENT -> listOf(
                PlayerInfo(PlayerColor.RED, userName, userAvatarId, countryFlag = "🇧🇩", countryName = "Bangladesh", isLocalHuman = true, rankTitle = "Challenger"),
                PlayerInfo(PlayerColor.GREEN, "আরিফ খান", "avatar_boy2", countryFlag = "🇮🇳", countryName = "India", isBot = true, rankTitle = "Tourney Seed 2"),
                PlayerInfo(PlayerColor.YELLOW, "সাদিয়া রহমান", "avatar_girl1", countryFlag = "🇵🇰", countryName = "Pakistan", isBot = true, rankTitle = "Tourney Seed 3"),
                PlayerInfo(PlayerColor.BLUE, "রাতুল ইসলাম", "avatar_boy3", countryFlag = "🇳🇬", countryName = "Nigeria", isBot = true, rankTitle = "Tourney Seed 4")
            )
            GameMode.SIX_PLAYER_LUDO -> listOf(
                PlayerInfo(PlayerColor.RED, userName, userAvatarId, countryFlag = "🇧🇩", countryName = "Bangladesh", isLocalHuman = true, rankTitle = "Master"),
                PlayerInfo(PlayerColor.GREEN, "আরিফ খান", "avatar_boy2", countryFlag = "🇮🇳", countryName = "India", isBot = true, rankTitle = "Pro"),
                PlayerInfo(PlayerColor.YELLOW, "সাদিয়া রহমান", "avatar_girl1", countryFlag = "🇵🇰", countryName = "Pakistan", isBot = true, rankTitle = "Champion"),
                PlayerInfo(PlayerColor.BLUE, "রাতুল ইসলাম", "avatar_boy3", countryFlag = "🇳🇬", countryName = "Nigeria", isBot = true, rankTitle = "Grandmaster"),
                PlayerInfo(PlayerColor.PURPLE, "সুমি আক্তার", "avatar_girl2", countryFlag = "🇦🇱", countryName = "Albania", isBot = true, rankTitle = "Expert"),
                PlayerInfo(PlayerColor.ORANGE, "কবীর হোসেন", "avatar_boy4", countryFlag = "🇲🇾", countryName = "Malaysia", isBot = true, rankTitle = "Veteran")
            )
            GameMode.SNAKES_AND_LADDERS -> listOf(
                PlayerInfo(PlayerColor.RED, userName, userAvatarId, countryFlag = "🇧🇩", countryName = "Bangladesh", isLocalHuman = true, rankTitle = "Player"),
                PlayerInfo(PlayerColor.GREEN, "সবুজ বট", "avatar_boy2", countryFlag = "🇮🇳", countryName = "India", isBot = true, rankTitle = "Bot")
            )
        }

        val initialTokens = PlayerColor.entries.associateWith { color ->
            (0..3).map { id -> Token(id = id, color = color, state = TokenState.IN_YARD, stepCount = 0) }
        }

        val fairSession = FairPlaySecurity.generateSession()

        return GameState(
            mode = mode,
            betAmount = betOption.entryCoins,
            potAmount = betOption.prizeCoins,
            players = players,
            tokens = initialTokens,
            currentTurnColor = PlayerColor.RED,
            turnTimerSeconds = 15,
            fairPlaySession = fairSession,
            messages = listOf(
                ChatMessage(senderName = "সিস্টেম", senderColor = PlayerColor.RED, text = "খেলা শুরু হয়েছে! ফেয়ার প্লে এনক্রিপশন সক্রিয়।")
            )
        )
    }

    /**
     * Compute selectable tokens for current player based on dice value
     */
    fun getSelectableTokens(tokens: List<Token>, diceValue: Int): Set<Int> {
        val selectable = mutableSetOf<Int>()
        for (token in tokens) {
            when (token.state) {
                TokenState.IN_YARD -> {
                    if (diceValue == 6) {
                        selectable.add(token.id)
                    }
                }
                TokenState.ON_TRACK -> {
                    if (token.stepCount + diceValue <= 56) {
                        selectable.add(token.id)
                    }
                }
                TokenState.IN_HOME_STRETCH -> {
                    if (token.stepCount + diceValue <= 56) {
                        selectable.add(token.id)
                    }
                }
                TokenState.HOME -> {
                    // Already completed, cannot move
                }
            }
        }
        return selectable
    }

    /**
     * Move token and calculate captures, home arrival, and extra turns
     */
    fun executeMove(
        currentState: GameState,
        tokenId: Int
    ): MoveResult {
        val currentColor = currentState.currentTurnColor
        val currentDice = currentState.currentDiceValue ?: return MoveResult(currentState, false, null)
        val playerTokens = currentState.tokens[currentColor]?.toMutableList() ?: return MoveResult(currentState, false, null)
        val tokenIndex = playerTokens.indexOfFirst { it.id == tokenId }
        if (tokenIndex == -1) return MoveResult(currentState, false, null)

        val oldToken = playerTokens[tokenIndex]
        val newToken: Token
        var isCapture = false
        var capturedColor: PlayerColor? = null
        var isHomeArrival = false
        var isSafeStarArrival = false

        if (oldToken.state == TokenState.IN_YARD && currentDice == 6) {
            // Move out of yard to start position (step 0)
            newToken = oldToken.copy(state = TokenState.ON_TRACK, stepCount = 0)
            isSafeStarArrival = true
        } else {
            val nextStep = oldToken.stepCount + currentDice
            if (nextStep < 51) {
                newToken = oldToken.copy(state = TokenState.ON_TRACK, stepCount = nextStep)
            } else if (nextStep in 51..55) {
                newToken = oldToken.copy(state = TokenState.IN_HOME_STRETCH, stepCount = nextStep)
            } else if (nextStep == 56) {
                newToken = oldToken.copy(state = TokenState.HOME, stepCount = 56)
                isHomeArrival = true
            } else {
                return MoveResult(currentState, false, null) // Cannot overshoot home
            }
        }

        playerTokens[tokenIndex] = newToken
        val updatedTokensMap = currentState.tokens.toMutableMap()
        updatedTokensMap[currentColor] = playerTokens

        // Check for opponent capture if on track
        if (newToken.state == TokenState.ON_TRACK) {
            val landTrackIndex = LudoTrackData.getTrackIndexForColor(currentColor, newToken.stepCount)
            val isSafe = LudoTrackData.SAFE_CELL_INDICES.contains(landTrackIndex)
            if (isSafe) {
                isSafeStarArrival = true
            } else {
                for (otherColor in PlayerColor.entries) {
                    if (otherColor == currentColor) continue
                    val otherTokens = updatedTokensMap[otherColor]?.toMutableList() ?: continue
                    for (i in otherTokens.indices) {
                        val otherToken = otherTokens[i]
                        if (otherToken.state == TokenState.ON_TRACK) {
                            val otherTrackIndex = LudoTrackData.getTrackIndexForColor(otherColor, otherToken.stepCount)
                            if (otherTrackIndex == landTrackIndex) {
                                // Captured!
                                otherTokens[i] = otherToken.copy(state = TokenState.IN_YARD, stepCount = 0)
                                isCapture = true
                                capturedColor = otherColor
                                break
                            }
                        }
                    }
                    updatedTokensMap[otherColor] = otherTokens
                    if (isCapture) break
                }
            }
        }

        // Check if current player won (all 4 tokens in HOME)
        val allHome = playerTokens.all { it.state == TokenState.HOME }
        val winner = if (allHome) currentColor else null

        // Extra turn rules: rolling a 6, capturing an opponent token, or reaching home grants an extra roll
        val grantExtraTurn = (currentDice == 6 && currentState.consecutiveSixes < 2) || isCapture || isHomeArrival

        val nextPlayerColor = if (grantExtraTurn && winner == null) {
            currentColor
        } else {
            getNextColor(currentColor, currentState.players.map { it.color })
        }

        val moveDescBn = when {
            winner != null -> "${currentColor.titleBn} দল বিজয়ী হয়েছে!"
            isCapture -> "${currentColor.titleBn} দল ${capturedColor?.titleBn} দলের গুটি কেটেছে! অতিরিক্ত চাল!"
            isHomeArrival -> "${currentColor.titleBn} দলের গুটি ঘরে পৌঁছেছে! অতিরিক্ত চাল!"
            currentDice == 6 -> "ছক্কা পড়েছে! অতিরিক্ত চাল পেয়েছেন।"
            else -> "${currentColor.titleBn} দল $currentDice ঘর এগিয়েছে।"
        }

        val moveDescEn = when {
            winner != null -> "${currentColor.titleEn} won the game!"
            isCapture -> "${currentColor.titleEn} captured ${capturedColor?.titleEn}'s token! Extra turn!"
            isHomeArrival -> "${currentColor.titleEn} reached Home! Extra turn!"
            currentDice == 6 -> "Rolled a 6! Extra turn awarded."
            else -> "${currentColor.titleEn} moved $currentDice steps."
        }

        val updatedState = currentState.copy(
            tokens = updatedTokensMap,
            currentTurnColor = nextPlayerColor,
            currentDiceValue = null,
            hasRolled = false,
            canSelectToken = false,
            selectableTokenIds = emptySet(),
            winner = winner,
            consecutiveSixes = if (currentDice == 6 && grantExtraTurn) currentState.consecutiveSixes + 1 else 0,
            lastMoveDescriptionBn = moveDescBn,
            lastMoveDescriptionEn = moveDescEn
        )

        return MoveResult(updatedState, isCapture, capturedColor, isHomeArrival, isSafeStarArrival)
    }

    fun getNextColor(current: PlayerColor, activeColors: List<PlayerColor>? = null): PlayerColor {
        val list = if (!activeColors.isNullOrEmpty()) activeColors else listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE)
        val currentIndex = list.indexOf(current)
        val nextIndex = if (currentIndex != -1) (currentIndex + 1) % list.size else 0
        return list[nextIndex]
    }

    /**
     * Intelligent AI decision making for bot players
     */
    fun chooseBestBotToken(tokens: List<Token>, selectableIds: Set<Int>, diceValue: Int, allTokens: Map<PlayerColor, List<Token>>, botColor: PlayerColor): Int? {
        if (selectableIds.isEmpty()) return null
        if (selectableIds.size == 1) return selectableIds.first()

        var bestTokenId = selectableIds.first()
        var highestScore = -100

        for (id in selectableIds) {
            val token = tokens.find { it.id == id } ?: continue
            var score = 0

            // 1. Entering Home is top priority (score +100)
            if (token.stepCount + diceValue == 56) {
                score += 100
            }

            // 2. Capturing opponent token (score +80)
            if (token.state == TokenState.ON_TRACK) {
                val nextStep = token.stepCount + diceValue
                if (nextStep < 51) {
                    val landTrack = LudoTrackData.getTrackIndexForColor(botColor, nextStep)
                    if (!LudoTrackData.SAFE_CELL_INDICES.contains(landTrack)) {
                        for ((otherCol, otherList) in allTokens) {
                            if (otherCol == botColor) continue
                            for (ot in otherList) {
                                if (ot.state == TokenState.ON_TRACK) {
                                    val otTrack = LudoTrackData.getTrackIndexForColor(otherCol, ot.stepCount)
                                    if (otTrack == landTrack) {
                                        score += 80
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Releasing token from Yard on a 6 (score +60)
            if (token.state == TokenState.IN_YARD && diceValue == 6) {
                score += 60
            }

            // 4. Moving to safe star (score +40)
            if (token.state == TokenState.ON_TRACK) {
                val nextStep = token.stepCount + diceValue
                if (nextStep < 51) {
                    val landTrack = LudoTrackData.getTrackIndexForColor(botColor, nextStep)
                    if (LudoTrackData.SAFE_CELL_INDICES.contains(landTrack)) {
                        score += 40
                    }
                }
            }

            // 5. Entering Home Stretch (score +30)
            if (token.state == TokenState.ON_TRACK && token.stepCount + diceValue >= 51) {
                score += 30
            }

            // 6. Prefer moving the token furthest ahead
            score += token.stepCount

            if (score > highestScore) {
                highestScore = score
                bestTokenId = id
            }
        }

        return bestTokenId
    }
}

data class MoveResult(
    val nextState: GameState,
    val isCapture: Boolean,
    val capturedColor: PlayerColor?,
    val isHomeArrival: Boolean = false,
    val isSafeStar: Boolean = false
)
