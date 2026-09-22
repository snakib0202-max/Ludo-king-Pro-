package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.dialogs.AiAssistantDialog
import com.example.ui.dialogs.FriendInviteDialog
import com.example.ui.dialogs.GiftBoxDialog
import com.example.ui.dialogs.PaymentGatewayDialog
import com.example.ui.dialogs.RewardedAdDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.SevenUpDownDialog
import com.example.ui.dialogs.TournamentDialog
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.SixPlayerLudoScreen
import com.example.ui.screens.SnakesLaddersScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF090D16)
                ) {
                    LudoAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun LudoAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val showPaymentDialog by viewModel.showPaymentDialog.collectAsState()
    val showRewardedAdDialog by viewModel.showRewardedAdDialog.collectAsState()
    val isBankruptRecovery by viewModel.isBankruptRecoveryAd.collectAsState()
    val showFriendInviteDialog by viewModel.showFriendInviteDialog.collectAsState()
    val showAiAssistantDialog by viewModel.showAiAssistantDialog.collectAsState()
    val activeGiftBox by viewModel.activeGiftBoxDialog.collectAsState()
    val isOfflineGiftBoxMode by viewModel.isOfflineGiftBoxMode.collectAsState()
    val showTournamentDialog by viewModel.showTournamentDialog.collectAsState()
    val showSevenUpDownDialog by viewModel.showSevenUpDownDialog.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val roomCode by viewModel.generatedRoomCode.collectAsState()

    // Back handling
    if (currentScreen != ScreenDestination.HOME) {
        BackHandler {
            viewModel.navigateTo(ScreenDestination.HOME)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            ScreenDestination.HOME -> HomeScreen(viewModel = viewModel)
            ScreenDestination.GAME -> GameScreen(viewModel = viewModel)
            ScreenDestination.SNAKES_AND_LADDERS -> SnakesLaddersScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateTo(ScreenDestination.HOME) }
            )
            ScreenDestination.SIX_PLAYER_LUDO -> SixPlayerLudoScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateTo(ScreenDestination.HOME) }
            )
            ScreenDestination.LEADERBOARD -> LeaderboardScreen(viewModel = viewModel)
            ScreenDestination.SHOP -> ShopScreen(viewModel = viewModel)
            ScreenDestination.PROFILE -> ProfileScreen(viewModel = viewModel)
        }

        // Global Dialogs
        if (showPaymentDialog) {
            PaymentGatewayDialog(
                onDismiss = { viewModel.showPaymentDialog.value = false },
                onPaymentSuccess = { coinsEarned ->
                    viewModel.handlePaymentSuccess(coinsEarned)
                }
            )
        }

        if (showRewardedAdDialog) {
            RewardedAdDialog(
                isBankruptRecovery = isBankruptRecovery,
                onDismiss = { viewModel.showRewardedAdDialog.value = false },
                onRewardEarned = { coins, gems ->
                    viewModel.handleAdRewardEarned(coins, gems)
                }
            )
        }

        if (showFriendInviteDialog) {
            FriendInviteDialog(
                roomCode = roomCode,
                friends = friends,
                onDismiss = { viewModel.showFriendInviteDialog.value = false },
                onInviteFriend = { friend ->
                    viewModel.startNewGame(
                        com.example.game.GameMode.PLAY_WITH_FRIENDS,
                        viewModel.selectedBetOption.value
                    )
                    viewModel.showFriendInviteDialog.value = false
                },
                onSendGift = { friend ->
                    viewModel.sendGiftToFriend(friend)
                }
            )
        }

        // AI Assistant Dialog (বাংলা ও English এআই এজেন্ট)
        if (showAiAssistantDialog) {
            AiAssistantDialog(
                onDismiss = { viewModel.showAiAssistantDialog.value = false }
            )
        }

        // Gift Box Dialog (অফলাইন বিজয়ী ও অনলাইন এডস দেখে উপহার বক্স)
        if (activeGiftBox != null) {
            val isBn = userProfile?.languageBn ?: true
            GiftBoxDialog(
                box = activeGiftBox!!,
                isOfflineMode = isOfflineGiftBoxMode,
                isBn = isBn,
                onDismiss = { viewModel.activeGiftBoxDialog.value = null },
                onRewardClaimed = { coins: Long, gems: Int, offlinePoints: Long ->
                    viewModel.handleGiftBoxClaim(coins, gems, offlinePoints)
                    viewModel.activeGiftBoxDialog.value = null
                },
                onWatchAdForBonusBox = {
                    viewModel.openGiftBoxByAd(activeGiftBox!!)
                }
            )
        }

        // Championship Tournament Dialog
        if (showTournamentDialog) {
            TournamentDialog(
                userCoins = userProfile?.coins ?: 10000L,
                isBn = userProfile?.languageBn ?: true,
                onDismiss = { viewModel.showTournamentDialog.value = false },
                onStartTournamentMatch = { roundName ->
                    viewModel.showTournamentDialog.value = false
                    viewModel.startNewGame(
                        com.example.game.GameMode.TOURNAMENT,
                        viewModel.selectedBetOption.value
                    )
                }
            )
        }

        // 7 Up 7 Down Lucky Dice Dialog
        if (showSevenUpDownDialog) {
            SevenUpDownDialog(
                userCoins = userProfile?.coins ?: 10000L,
                isBn = userProfile?.languageBn ?: true,
                onDismiss = { viewModel.showSevenUpDownDialog.value = false },
                onWinCoins = { winCoins ->
                    viewModel.handleMiniGameWin(winCoins)
                }
            )
        }

        // Settings & Sound Effects Management Dialog
        if (showSettingsDialog) {
            val isBn = userProfile?.languageBn ?: true
            SettingsDialog(
                isBn = isBn,
                isSoundEnabled = isSoundEnabled,
                isVibrationEnabled = isVibrationEnabled,
                onToggleSound = { viewModel.toggleSound(it) },
                onToggleVibration = { viewModel.toggleVibration(it) },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onTestSound = { viewModel.testSoundEffects() },
                onDismiss = { viewModel.showSettingsDialog.value = false }
            )
        }
    }
}

