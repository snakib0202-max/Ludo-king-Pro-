package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val database: AppDatabase) {
    val userProfile: Flow<UserEntity?> = database.userDao().getUserProfile()
    val dailyMissions: Flow<List<MissionEntity>> = database.missionDao().getAllMissions()
    val shopItems: Flow<List<ShopItemEntity>> = database.shopDao().getAllShopItems()
    val friends: Flow<List<FriendEntity>> = database.friendDao().getAllFriends()

    suspend fun getUserOnce(): UserEntity? = database.userDao().getUserProfileOnce()

    suspend fun saveUser(user: UserEntity) = database.userDao().insertOrUpdate(user)

    suspend fun addCoins(amount: Long) = database.userDao().addCoins(amount)

    suspend fun addGems(amount: Int) = database.userDao().addGems(amount)

    suspend fun equipBoardSkin(skinId: String) {
        database.userDao().equipBoardSkin(skinId)
        database.shopDao().equipItem(skinId, "BOARD")
    }

    suspend fun equipDiceSkin(skinId: String) {
        database.userDao().equipDiceSkin(skinId)
        database.shopDao().equipItem(skinId, "DICE")
    }

    suspend fun equipAvatar(avatarId: String) {
        database.userDao().equipAvatar(avatarId)
    }

    suspend fun equipFrame(frameId: String) {
        database.userDao().equipFrame(frameId)
        database.shopDao().equipItem(frameId, "FRAME")
    }

    suspend fun toggleLanguage(isBn: Boolean) {
        database.userDao().setLanguage(isBn)
    }

    suspend fun unlockShopItem(itemId: String, costCoins: Long, costGems: Int) {
        if (costCoins > 0) {
            database.userDao().addCoins(-costCoins)
        }
        if (costGems > 0) {
            database.userDao().addGems(-costGems)
        }
        database.shopDao().unlockItem(itemId)
    }

    suspend fun claimMissionReward(missionId: String, rewardCoins: Int, rewardXp: Int) {
        database.missionDao().markClaimed(missionId)
        database.userDao().addCoins(rewardCoins.toLong())
        // Add XP and handle level up
        val user = database.userDao().getUserProfileOnce() ?: return
        var newXp = user.currentXp + rewardXp
        var newLevel = user.level
        var newMaxXp = user.maxXp
        while (newXp >= newMaxXp) {
            newXp -= newMaxXp
            newLevel++
            newMaxXp = (newMaxXp * 1.25f).toInt()
        }
        database.userDao().insertOrUpdate(
            user.copy(
                level = newLevel,
                currentXp = newXp,
                maxXp = newMaxXp
            )
        )
    }

    suspend fun recordDiceRoll(value: Int) {
        val user = database.userDao().getUserProfileOnce() ?: return
        val updated = when (value) {
            1 -> user.copy(onesRolled = user.onesRolled + 1)
            2 -> user.copy(twosRolled = user.twosRolled + 1)
            3 -> user.copy(threesRolled = user.threesRolled + 1)
            4 -> user.copy(foursRolled = user.foursRolled + 1)
            5 -> user.copy(fivesRolled = user.fivesRolled + 1)
            6 -> {
                database.missionDao().incrementProgress("m1", 1)
                user.copy(sixesRolled = user.sixesRolled + 1)
            }
            else -> user
        }
        database.userDao().insertOrUpdate(updated)
        database.missionDao().checkCompletions()
    }

    suspend fun recordCapture() {
        val user = database.userDao().getUserProfileOnce() ?: return
        database.missionDao().incrementProgress("m2", 1)
        database.missionDao().checkCompletions()
        database.userDao().insertOrUpdate(user.copy(totalCaptures = user.totalCaptures + 1))
    }

    suspend fun recordMatchEnd(isWin: Boolean, coinNetChange: Long) {
        val user = database.userDao().getUserProfileOnce() ?: return
        val newCoins = (user.coins + coinNetChange).coerceAtLeast(0L)
        val newWins = if (isWin) user.wins + 1 else user.wins
        val newStreak = if (isWin) user.winStreak + 1 else 0
        val earnedXp = if (isWin) 120 else 40

        var newXp = user.currentXp + earnedXp
        var newLevel = user.level
        var newMaxXp = user.maxXp
        while (newXp >= newMaxXp) {
            newXp -= newMaxXp
            newLevel++
            newMaxXp = (newMaxXp * 1.25f).toInt()
        }

        if (isWin) {
            database.missionDao().incrementProgress("m3", 1)
            database.missionDao().checkCompletions()
        }

        database.userDao().insertOrUpdate(
            user.copy(
                coins = newCoins,
                wins = newWins,
                totalGames = user.totalGames + 1,
                winStreak = newStreak,
                level = newLevel,
                currentXp = newXp,
                maxXp = newMaxXp
            )
        )
    }

    suspend fun markAdWatched() {
        database.missionDao().incrementProgress("m4", 1)
        database.missionDao().checkCompletions()
        database.userDao().addCoins(1000L)
        database.userDao().addGems(5)
    }

    suspend fun sendGiftToFriend(friendId: String) {
        database.friendDao().markGiftSent(friendId, System.currentTimeMillis())
        database.userDao().addCoins(-100L) // Small gift fee
    }
}
