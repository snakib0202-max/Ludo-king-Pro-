package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Ludo Master",
    val playerTag: String = "#8829",
    val avatarId: String = "avatar_king",
    val frameId: String = "frame_gold",
    val boardSkinId: String = "board_classic",
    val diceSkinId: String = "dice_gold",
    val coins: Long = 15000L,
    val gems: Int = 120,
    val level: Int = 5,
    val currentXp: Int = 340,
    val maxXp: Int = 500,
    val totalGames: Int = 42,
    val wins: Int = 28,
    val winStreak: Int = 4,
    val totalCaptures: Int = 89,
    val onesRolled: Int = 35,
    val twosRolled: Int = 38,
    val threesRolled: Int = 40,
    val foursRolled: Int = 42,
    val fivesRolled: Int = 45,
    val sixesRolled: Int = 48,
    val languageBn: Boolean = true
)

@Entity(tableName = "daily_missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val titleBn: String,
    val titleEn: String,
    val descBn: String,
    val descEn: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardCoins: Int,
    val rewardXp: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false
)

@Entity(tableName = "shop_items")
data class ShopItemEntity(
    @PrimaryKey val id: String,
    val category: String, // "BOARD", "DICE", "FRAME", "AVATAR"
    val nameBn: String,
    val nameEn: String,
    val description: String,
    val priceCoins: Long = 0L,
    val priceGems: Int = 0,
    val isUnlocked: Boolean = false,
    val isEquipped: Boolean = false,
    val accentColorHex: Long = 0xFFFFD700
)

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarId: String,
    val frameId: String,
    val level: Int,
    val status: String, // "Online", "In Match", "Offline"
    val winRate: Int,
    val lastGiftSentTimestamp: Long = 0L
)
