package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FriendEntity
import com.example.data.GameRepository
import com.example.data.MissionEntity
import com.example.data.ShopItemEntity
import com.example.data.UserEntity
import com.example.game.BET_OPTIONS
import com.example.game.BetOption
import com.example.game.ChatMessage
import com.example.game.FairPlaySecurity
import com.example.game.GameMode
import com.example.game.GameState
import com.example.game.LudoEngine
import com.example.game.PlayerColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object HOME : ScreenDestination()
    object GAME : ScreenDestination()
    object SNAKES_AND_LADDERS : ScreenDestination()
    object SIX_PLAYER_LUDO : ScreenDestination()
    object LEADERBOARD : ScreenDestination()
    object SHOP : ScreenDestination()
    object PROFILE : ScreenDestination()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = GameRepository(database)

    val userProfile: StateFlow<UserEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dailyMissions: StateFlow<List<MissionEntity>> = repository.dailyMissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shopItems: StateFlow<List<ShopItemEntity>> = repository.shopItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friends: StateFlow<List<FriendEntity>> = repository.friends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.HOME)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _isDiceRolling = MutableStateFlow(false)
    val isDiceRolling: StateFlow<Boolean> = _isDiceRolling.asStateFlow()

    private val _floatingEmoji = MutableStateFlow<String?>(null)
    val floatingEmoji: StateFlow<String?> = _floatingEmoji.asStateFlow()

    // Dedicated Emoji Throw state
    private val _activeEmojiThrow = MutableStateFlow<com.example.game.ActiveEmojiThrow?>(null)
    val activeEmojiThrow: StateFlow<com.example.game.ActiveEmojiThrow?> = _activeEmojiThrow.asStateFlow()

    // Active player chat bubble state
    private val _playerChatBubbles = MutableStateFlow<Map<PlayerColor, String>>(emptyMap())
    val playerChatBubbles: StateFlow<Map<PlayerColor, String>> = _playerChatBubbles.asStateFlow()

    // Sound Effects Manager
    val soundManager = com.example.game.SoundManager.getInstance(application)
    val isSoundEnabled = soundManager.isSoundEnabled
    val isVibrationEnabled = soundManager.isVibrationEnabled
    val showSettingsDialog = MutableStateFlow(false)

    // Dialog control states
    val showPaymentDialog = MutableStateFlow(false)
    val showRewardedAdDialog = MutableStateFlow(false)
    val isBankruptRecoveryAd = MutableStateFlow(false)
    val showAntiCheatDialog = MutableStateFlow(false)
    val showGameOverDialog = MutableStateFlow(false)
    val showFeedbackDialog = MutableStateFlow(false)
    val showFriendInviteDialog = MutableStateFlow(false)
    val showAiAssistantDialog = MutableStateFlow(false)
    val showTournamentDialog = MutableStateFlow(false)
    val showSevenUpDownDialog = MutableStateFlow(false)
    val activeGiftBoxDialog = MutableStateFlow<com.example.game.GiftBoxItem?>(null)
    val isOfflineGiftBoxMode = MutableStateFlow(false)
    val selectedBetOption = MutableStateFlow(BET_OPTIONS[0])
    val generatedRoomCode = MutableStateFlow("LUDO-8842")

    private var turnTimerJob: Job? = null
    private var botTurnJob: Job? = null

    fun navigateTo(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun handleMiniGameWin(coins: Long) {
        viewModelScope.launch {
            repository.addCoins(coins)
        }
    }

    fun startNewGame(mode: GameMode, betOption: BetOption) {
        val user = userProfile.value ?: UserEntity()
        // Deduct bet coins for user in online/friend mode
        if (mode == GameMode.ONLINE_MULTIPLAYER || mode == GameMode.PLAY_WITH_FRIENDS) {
            if (user.coins < betOption.entryCoins) {
                // Not enough coins, prompt bankrupt recovery or payment
                isBankruptRecoveryAd.value = true
                showRewardedAdDialog.value = true
                return
            }
            viewModelScope.launch {
                repository.addCoins(-betOption.entryCoins)
            }
        }

        val initial = LudoEngine.initGame(
            mode = mode,
            betOption = betOption,
            userAvatarId = user.avatarId,
            userName = user.name
        )
        _gameState.value = initial
        _currentScreen.value = ScreenDestination.GAME
        showGameOverDialog.value = false

        startTurnTimer()
        checkBotTurn()
    }

    fun rollDice() {
        val state = _gameState.value ?: return
        if (_isDiceRolling.value || state.hasRolled || state.winner != null) return

        val currentPlayer = state.players.find { it.color == state.currentTurnColor }
        if (currentPlayer == null) return

        _isDiceRolling.value = true
        soundManager.playDiceRoll()
        viewModelScope.launch {
            // Cryptographic provably fair roll
            val (diceValue, fairHash) = FairPlaySecurity.calculateFairDiceRoll(
                serverSeed = state.fairPlaySession.serverSeed,
                clientNonce = state.rollIndex,
                rollCount = state.rollIndex + 1
            )

            delay(650) // Dice rolling visual duration
            _isDiceRolling.value = false

            if (diceValue == 6) {
                soundManager.playSixRolled()
            }

            // Record in analytics
            repository.recordDiceRoll(diceValue)

            val currentTokens = state.tokens[state.currentTurnColor] ?: emptyList()
            val selectableIds = LudoEngine.getSelectableTokens(currentTokens, diceValue)

            if (selectableIds.isEmpty()) {
                // No moves possible with this roll, switch turn to next player
                val nextColor = LudoEngine.getNextColor(state.currentTurnColor, state.players.map { it.color })
                val descBn = "${state.currentTurnColor.titleBn} দল $diceValue পেয়েছে কিন্তু কোনো চাল সম্ভব নয়।"
                val descEn = "${state.currentTurnColor.titleEn} rolled $diceValue, no valid moves."

                _gameState.value = state.copy(
                    currentDiceValue = diceValue,
                    diceRollHash = fairHash,
                    hasRolled = false,
                    canSelectToken = false,
                    selectableTokenIds = emptySet(),
                    currentTurnColor = nextColor,
                    lastMoveDescriptionBn = descBn,
                    lastMoveDescriptionEn = descEn,
                    rollIndex = state.rollIndex + 1
                )
                startTurnTimer()
                checkBotTurn()
            } else if (selectableIds.size == 1 && (currentPlayer.isBot || state.mode == GameMode.VS_COMPUTER || selectableIds.first() != null)) {
                // Exactly 1 possible move - auto execute if bot or if single yard exit
                _gameState.value = state.copy(
                    currentDiceValue = diceValue,
                    diceRollHash = fairHash,
                    hasRolled = true,
                    canSelectToken = true,
                    selectableTokenIds = selectableIds,
                    rollIndex = state.rollIndex + 1
                )
                if (currentPlayer.isBot) {
                    delay(500)
                    executeTokenMove(selectableIds.first())
                }
            } else {
                // Waiting for user/bot to pick token
                _gameState.value = state.copy(
                    currentDiceValue = diceValue,
                    diceRollHash = fairHash,
                    hasRolled = true,
                    canSelectToken = true,
                    selectableTokenIds = selectableIds,
                    rollIndex = state.rollIndex + 1
                )

                if (currentPlayer.isBot) {
                    delay(700)
                    val bestTokenId = LudoEngine.chooseBestBotToken(
                        tokens = currentTokens,
                        selectableIds = selectableIds,
                        diceValue = diceValue,
                        allTokens = state.tokens,
                        botColor = state.currentTurnColor
                    )
                    bestTokenId?.let { executeTokenMove(it) }
                }
            }
        }
    }

    fun executeTokenMove(tokenId: Int) {
        val state = _gameState.value ?: return
        if (!state.canSelectToken || !state.selectableTokenIds.contains(tokenId)) return

        val result = LudoEngine.executeMove(state, tokenId)

        if (result.isCapture) {
            soundManager.playTokenCapture()
            viewModelScope.launch {
                repository.recordCapture()
            }
        } else if (result.isHomeArrival || result.isSafeStar) {
            soundManager.playSafeLanding()
        } else {
            soundManager.playTokenStep()
        }

        _gameState.value = result.nextState

        // Check if victory or loss occurred
        if (result.nextState.winner != null) {
            turnTimerJob?.cancel()
            val isUserWinner = result.nextState.winner == PlayerColor.RED
            if (isUserWinner) {
                soundManager.playVictory()
            } else {
                soundManager.playLoss()
            }
            val coinNet = if (isUserWinner) result.nextState.potAmount else 0L
            viewModelScope.launch {
                repository.recordMatchEnd(isWin = isUserWinner, coinNetChange = coinNet)
            }
            showGameOverDialog.value = true

            // If offline match (VS_COMPUTER or PASS_AND_PLAY), award an offline mystery gift box!
            val isOffline = result.nextState.mode == GameMode.VS_COMPUTER || result.nextState.mode == GameMode.PASS_AND_PLAY
            if (isOffline && isUserWinner) {
                isOfflineGiftBoxMode.value = true
                activeGiftBoxDialog.value = com.example.game.DEFAULT_GIFT_BOXES.random()
            }
        } else {
            startTurnTimer()
            checkBotTurn()
        }
    }

    private fun checkBotTurn() {
        botTurnJob?.cancel()
        val state = _gameState.value ?: return
        if (state.winner != null) return

        val currentPlayer = state.players.find { it.color == state.currentTurnColor }
        if (currentPlayer != null && currentPlayer.isBot) {
            botTurnJob = viewModelScope.launch {
                delay(800) // Simulated human-like pause
                rollDice()
            }
        }
    }

    private fun startTurnTimer() {
        turnTimerJob?.cancel()
        turnTimerJob = viewModelScope.launch {
            for (sec in 15 downTo 0) {
                _gameState.value = _gameState.value?.copy(turnTimerSeconds = sec)
                delay(1000)
            }
            // Timeout: auto skip or random move
            val state = _gameState.value
            if (state != null && state.winner == null) {
                if (state.canSelectToken && state.selectableTokenIds.isNotEmpty()) {
                    executeTokenMove(state.selectableTokenIds.first())
                } else {
                    val nextColor = LudoEngine.getNextColor(state.currentTurnColor)
                    _gameState.value = state.copy(
                        currentTurnColor = nextColor,
                        currentDiceValue = null,
                        hasRolled = false,
                        canSelectToken = false,
                        selectableTokenIds = emptySet()
                    )
                    startTurnTimer()
                    checkBotTurn()
                }
            }
        }
    }

    fun toggleVoiceChatMic() {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(isVoiceChatActive = !current.isVoiceChatActive)
    }

    fun sendQuickEmoji(emoji: String) {
        val state = _gameState.value ?: return
        soundManager.playEmojiPop()
        _floatingEmoji.value = emoji

        val currentBubbles = _playerChatBubbles.value.toMutableMap()
        currentBubbles[PlayerColor.RED] = emoji
        _playerChatBubbles.value = currentBubbles

        val updatedMsgs = state.messages + ChatMessage(
            senderName = "আপনি (লাল)",
            senderColor = PlayerColor.RED,
            text = emoji,
            isEmoji = true
        )
        _gameState.value = state.copy(messages = updatedMsgs)

        viewModelScope.launch {
            delay(2800)
            if (_floatingEmoji.value == emoji) {
                _floatingEmoji.value = null
            }
            val bubbles = _playerChatBubbles.value.toMutableMap()
            if (bubbles[PlayerColor.RED] == emoji) {
                bubbles.remove(PlayerColor.RED)
                _playerChatBubbles.value = bubbles
            }
        }

        // Real-time opponent bot reaction during match
        val botOpponents = state.players.filter { it.color != PlayerColor.RED && it.isBot }
        if (botOpponents.isNotEmpty() && (1..3).random() <= 2) {
            viewModelScope.launch {
                delay((1200..2200).random().toLong())
                val respondingBot = botOpponents.random()
                val botReaction = when (emoji) {
                    "😂", "🤣" -> listOf("😂", "🤣", "😏", "👏").random()
                    "😡", "😭" -> listOf("😏", "😂", "🤫", "🍿").random()
                    "🔥", "👑" -> listOf("👏", "💪", "😎", "🫡").random()
                    "🎲" -> listOf("🎲", "🤞", "🍀", "⚡").random()
                    else -> listOf("👍", "😊", "🔥", "🤝").random()
                }
                val botBubbles = _playerChatBubbles.value.toMutableMap()
                botBubbles[respondingBot.color] = botReaction
                _playerChatBubbles.value = botBubbles

                val stateNow = _gameState.value
                if (stateNow != null) {
                    _gameState.value = stateNow.copy(
                        messages = stateNow.messages + ChatMessage(
                            senderName = respondingBot.name,
                            senderColor = respondingBot.color,
                            text = botReaction,
                            isEmoji = true
                        )
                    )
                }

                delay(3000)
                val clearBubbles = _playerChatBubbles.value.toMutableMap()
                if (clearBubbles[respondingBot.color] == botReaction) {
                    clearBubbles.remove(respondingBot.color)
                    _playerChatBubbles.value = clearBubbles
                }
            }
        }
    }

    fun sendDedicatedEmoji(emoji: String, targetColor: PlayerColor, targetName: String) {
        val state = _gameState.value ?: return
        val throwAction = com.example.game.ActiveEmojiThrow(
            senderColor = PlayerColor.RED,
            senderName = "আপনি",
            targetColor = targetColor,
            targetName = targetName,
            emoji = emoji,
            actionTextBn = "আপনি ➔ $targetName কে $emoji উৎসর্গ করেছেন!"
        )
        _activeEmojiThrow.value = throwAction

        // Also add to chat and player chat bubble
        val currentBubbles = _playerChatBubbles.value.toMutableMap()
        currentBubbles[targetColor] = emoji
        _playerChatBubbles.value = currentBubbles

        val updatedMsgs = state.messages + ChatMessage(
            senderName = "আপনি",
            senderColor = PlayerColor.RED,
            text = "উৎসর্গ ➔ $targetName: $emoji",
            isEmoji = true
        )
        _gameState.value = state.copy(messages = updatedMsgs)

        viewModelScope.launch {
            delay(3200)
            if (_activeEmojiThrow.value?.id == throwAction.id) {
                _activeEmojiThrow.value = null
            }
            delay(1500)
            val bubblesAfter = _playerChatBubbles.value.toMutableMap()
            bubblesAfter.remove(targetColor)
            _playerChatBubbles.value = bubblesAfter
        }

        // Opponent bot reply with counter emoji if playing against bot!
        val targetPlayer = state.players.find { it.color == targetColor }
        if (targetPlayer != null && targetPlayer.isBot) {
            viewModelScope.launch {
                delay(2000)
                val replyEmojis = listOf("😂", "🔥", "😏", "👏", "😡", "🍅")
                val replyEmoji = replyEmojis.random()
                val botCounterThrow = com.example.game.ActiveEmojiThrow(
                    senderColor = targetColor,
                    senderName = targetName,
                    targetColor = PlayerColor.RED,
                    targetName = "আপনাকে",
                    emoji = replyEmoji,
                    actionTextBn = "$targetName ➔ আপনাকে $replyEmoji ছুড়ে মেরেছে!"
                )
                _activeEmojiThrow.value = botCounterThrow

                val botBubbles = _playerChatBubbles.value.toMutableMap()
                botBubbles[PlayerColor.RED] = replyEmoji
                _playerChatBubbles.value = botBubbles

                delay(3200)
                if (_activeEmojiThrow.value?.id == botCounterThrow.id) {
                    _activeEmojiThrow.value = null
                }
                delay(1500)
                val bubblesClear = _playerChatBubbles.value.toMutableMap()
                bubblesClear.remove(PlayerColor.RED)
                _playerChatBubbles.value = bubblesClear
            }
        }
    }

    fun sendCustomChatMessage(text: String) {
        val state = _gameState.value ?: return
        if (text.isBlank()) return

        val currentBubbles = _playerChatBubbles.value.toMutableMap()
        currentBubbles[PlayerColor.RED] = text
        _playerChatBubbles.value = currentBubbles

        val updatedMsgs = state.messages + ChatMessage(
            senderName = "আপনি (লাল)",
            senderColor = PlayerColor.RED,
            text = text,
            isEmoji = false
        )
        _gameState.value = state.copy(messages = updatedMsgs)

        viewModelScope.launch {
            delay(4000)
            val bubbles = _playerChatBubbles.value.toMutableMap()
            bubbles.remove(PlayerColor.RED)
            _playerChatBubbles.value = bubbles
        }
    }

    fun sendQuickPresetText(text: String) {
        sendCustomChatMessage(text)
    }

    fun handleGiftBoxClaim(coins: Long, gems: Int, offlinePoints: Long) {
        viewModelScope.launch {
            repository.addCoins(coins)
            if (gems > 0) repository.addGems(gems)
        }
    }

    fun openGiftBoxByAd(box: com.example.game.GiftBoxItem) {
        viewModelScope.launch {
            repository.markAdWatched()
            isOfflineGiftBoxMode.value = false
            activeGiftBoxDialog.value = box
        }
    }

    fun purchaseOrEquipShopItem(item: ShopItemEntity) {
        viewModelScope.launch {
            if (item.isUnlocked) {
                when (item.category) {
                    "BOARD" -> repository.equipBoardSkin(item.id)
                    "DICE" -> repository.equipDiceSkin(item.id)
                    "FRAME" -> repository.equipFrame(item.id)
                    "AVATAR" -> repository.equipAvatar(item.id)
                }
            } else {
                val user = userProfile.value ?: return@launch
                if (user.coins >= item.priceCoins && user.gems >= item.priceGems) {
                    repository.unlockShopItem(item.id, item.priceCoins, item.priceGems)
                }
            }
        }
    }

    fun claimMission(mission: MissionEntity) {
        viewModelScope.launch {
            repository.claimMissionReward(mission.id, mission.rewardCoins, mission.rewardXp)
        }
    }

    fun handleAdRewardEarned(coins: Long, gems: Int) {
        viewModelScope.launch {
            repository.markAdWatched()
            repository.addCoins(coins)
            repository.addGems(gems)
        }
    }

    fun handlePaymentSuccess(coinsEarned: Long) {
        viewModelScope.launch {
            repository.addCoins(coinsEarned)
        }
    }

    fun toggleLanguage() {
        val user = userProfile.value ?: return
        viewModelScope.launch {
            repository.toggleLanguage(!user.languageBn)
        }
    }

    fun sendGiftToFriend(friend: FriendEntity) {
        viewModelScope.launch {
            repository.sendGiftToFriend(friend.id)
        }
    }

    fun toggleSound(enabled: Boolean? = null): Boolean {
        return if (enabled != null) {
            soundManager.setSoundEnabled(enabled)
            enabled
        } else {
            soundManager.toggleSound()
        }
    }

    fun toggleMute(): Boolean {
        return soundManager.toggleMute()
    }

    fun toggleVibration(enabled: Boolean? = null): Boolean {
        return if (enabled != null) {
            soundManager.setVibrationEnabled(enabled)
            enabled
        } else {
            soundManager.toggleVibration()
        }
    }

    fun testSoundEffects() {
        soundManager.playDiceRoll()
        viewModelScope.launch {
            delay(380)
            soundManager.playTokenStep()
            delay(250)
            soundManager.playTokenCapture()
        }
    }

    fun testWinSound() {
        soundManager.playVictory()
    }

    fun testLossSound() {
        soundManager.playLoss()
    }

    override fun onCleared() {
        super.onCleared()
        turnTimerJob?.cancel()
        botTurnJob?.cancel()
    }
}
