package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("UPDATE user_profile SET coins = coins + :delta WHERE id = 1")
    suspend fun addCoins(delta: Long)

    @Query("UPDATE user_profile SET gems = gems + :delta WHERE id = 1")
    suspend fun addGems(delta: Int)

    @Query("UPDATE user_profile SET boardSkinId = :skinId WHERE id = 1")
    suspend fun equipBoardSkin(skinId: String)

    @Query("UPDATE user_profile SET diceSkinId = :skinId WHERE id = 1")
    suspend fun equipDiceSkin(skinId: String)

    @Query("UPDATE user_profile SET avatarId = :avatarId WHERE id = 1")
    suspend fun equipAvatar(avatarId: String)

    @Query("UPDATE user_profile SET frameId = :frameId WHERE id = 1")
    suspend fun equipFrame(frameId: String)

    @Query("UPDATE user_profile SET languageBn = :isBn WHERE id = 1")
    suspend fun setLanguage(isBn: Boolean)
}

@Dao
interface MissionDao {
    @Query("SELECT * FROM daily_missions")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMissions(missions: List<MissionEntity>)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Query("UPDATE daily_missions SET currentProgress = currentProgress + :increment WHERE id = :missionId AND isClaimed = 0")
    suspend fun incrementProgress(missionId: String, increment: Int)

    @Query("UPDATE daily_missions SET isCompleted = 1 WHERE currentProgress >= targetProgress")
    suspend fun checkCompletions()

    @Query("UPDATE daily_missions SET isClaimed = 1 WHERE id = :missionId")
    suspend fun markClaimed(missionId: String)
}

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop_items")
    fun getAllShopItems(): Flow<List<ShopItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertShopItems(items: List<ShopItemEntity>)

    @Query("UPDATE shop_items SET isUnlocked = 1 WHERE id = :itemId")
    suspend fun unlockItem(itemId: String)

    @Query("UPDATE shop_items SET isEquipped = CASE WHEN id = :itemId THEN 1 ELSE 0 END WHERE category = :category")
    suspend fun equipItem(itemId: String, category: String)
}

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Query("UPDATE friends SET lastGiftSentTimestamp = :timestamp WHERE id = :friendId")
    suspend fun markGiftSent(friendId: String, timestamp: Long)
}
