package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        MissionEntity::class,
        ShopItemEntity::class,
        FriendEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun missionDao(): MissionDao
    abstract fun shopDao(): ShopDao
    abstract fun friendDao(): FriendDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ludo_king_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                val userDao = database.userDao()
                val missionDao = database.missionDao()
                val shopDao = database.shopDao()
                val friendDao = database.friendDao()

                // Initial user
                userDao.insertOrUpdate(
                    UserEntity(
                        id = 1,
                        name = "Ludo Master",
                        coins = 20000L,
                        gems = 150,
                        level = 5,
                        currentXp = 320,
                        maxXp = 500,
                        wins = 24,
                        totalGames = 38
                    )
                )

                // Initial Daily Missions
                missionDao.insertMissions(
                    listOf(
                        MissionEntity(
                            id = "m1",
                            titleBn = "তিনটি ৬ ফেলুন",
                            titleEn = "Roll 6 Three Times",
                            descBn = "যেকোনো ম্যাচে ৩ বার ৬ ফেলুন",
                            descEn = "Roll six 3 times in any match",
                            currentProgress = 1,
                            targetProgress = 3,
                            rewardCoins = 1000,
                            rewardXp = 50
                        ),
                        MissionEntity(
                            id = "m2",
                            titleBn = "২টি গুটি কাটুন",
                            titleEn = "Capture 2 Tokens",
                            descBn = "প্রতিপক্ষের ২ টি গুটি ঘরে পাঠান",
                            descEn = "Cut 2 opponent tokens back to base",
                            currentProgress = 0,
                            targetProgress = 2,
                            rewardCoins = 1500,
                            rewardXp = 75
                        ),
                        MissionEntity(
                            id = "m3",
                            titleBn = "১টি অনলাইন ম্যাচ জিতুন",
                            titleEn = "Win 1 Online Match",
                            descBn = "কয়েন বাজির অনলাইন ম্যাচে জয়ী হোন",
                            descEn = "Win 1 multiplayer coin staking match",
                            currentProgress = 0,
                            targetProgress = 1,
                            rewardCoins = 3000,
                            rewardXp = 120
                        ),
                        MissionEntity(
                            id = "m4",
                            titleBn = "রিওয়ার্ডেড ভিডিও দেখুন",
                            titleEn = "Watch 1 Rewarded Ad",
                            descBn = "বিজ্ঞাপন দেখে বিনামূল্যে কয়েন ও জেম আয় করুন",
                            descEn = "Watch a video ad to earn bonus reward",
                            currentProgress = 0,
                            targetProgress = 1,
                            rewardCoins = 800,
                            rewardXp = 40
                        )
                    )
                )

                // Initial Shop Items
                shopDao.insertShopItems(
                    listOf(
                        // Boards
                        ShopItemEntity("board_classic", "BOARD", "ক্লাসিক উডেন", "Classic Wooden", "পরম্পরাগত কাঠের বোর্ড", 0L, 0, isUnlocked = true, isEquipped = true, accentColorHex = 0xFF8D6E63),
                        ShopItemEntity("board_royal", "BOARD", "রয়্যাল গোল্ড", "Royal Gold", "স্বর্ণখচিত রাজকীয় বোর্ড", 25000L, 0, isUnlocked = false, isEquipped = false, accentColorHex = 0xFFFFD700),
                        ShopItemEntity("board_cyber", "BOARD", "সাইবার নিয়ন", "Cyber Neon", "ভবিষ্যতবাদী নিয়ন লাইট বোর্ড", 45000L, 50, isUnlocked = false, isEquipped = false, accentColorHex = 0xFF00E5FF),
                        ShopItemEntity("board_marble", "BOARD", "মার্বেল প্যালেস", "Marble Palace", "সাদা মার্বেল পাথরের তৈরি রাজপ্রাসাদ", 60000L, 100, isUnlocked = false, isEquipped = false, accentColorHex = 0xFFE0E0E0),

                        // Dice
                        ShopItemEntity("dice_gold", "DICE", "স্বর্ণ ডাইস", "Gold Royale", "ঝলমলে সোনার ছক্কা", 0L, 0, isUnlocked = true, isEquipped = true, accentColorHex = 0xFFFFC107),
                        ShopItemEntity("dice_ruby", "DICE", "রুবি ক্রিস্টাল", "Ruby Crystal", "উজ্জ্বল লাল রুবি পাথরের ছক্কা", 15000L, 0, isUnlocked = false, isEquipped = false, accentColorHex = 0xFFE53935),
                        ShopItemEntity("dice_fire", "DICE", "ফায়ার ফ্লেম", "Fire Flame", "আগুনের স্ফুলিঙ্গ ছড়ানো ছক্কা", 30000L, 40, isUnlocked = false, isEquipped = false, accentColorHex = 0xFFFF5722),
                        ShopItemEntity("dice_diamond", "DICE", "ডায়মন্ড স্টার", "Diamond Star", "হীরার দ্যুতিময় ভিআইপি ছক্কা", 50000L, 80, isUnlocked = false, isEquipped = false, accentColorHex = 0xFF00B0FF),

                        // Frames
                        ShopItemEntity("frame_gold", "FRAME", "গোল্ডেন ক্রাউন", "Golden Crown", "রাজার মুকুট সমৃদ্ধ ফ্রেম", 0L, 0, isUnlocked = true, isEquipped = true, accentColorHex = 0xFFFFD700),
                        ShopItemEntity("frame_diamond", "FRAME", "ডায়মন্ড ক্রেস্ট", "Diamond Crest", "অভিজাত হিরের ফ্রেম", 20000L, 30, isUnlocked = false, isEquipped = false, accentColorHex = 0xFF00E5FF),
                        ShopItemEntity("frame_dragon", "FRAME", "ড্রাগন ফ্লেম", "Dragon Flame", "লেলিহান আগুনের ফ্রেম", 35000L, 60, isUnlocked = false, isEquipped = false, accentColorHex = 0xFFFF3D00)
                    )
                )

                // Initial Friends
                friendDao.insertFriends(
                    listOf(
                        FriendEntity("f1", "তানভীর আহমেদ", "avatar_boy1", "frame_gold", 12, "Online", 68),
                        FriendEntity("f2", "সাকিব হাসান", "avatar_boy2", "frame_diamond", 18, "In Match", 74),
                        FriendEntity("f3", "নুসরাত জাহান", "avatar_girl1", "frame_gold", 9, "Online", 55),
                        FriendEntity("f4", "রাকিব চৌধুরী", "avatar_boy3", "frame_dragon", 15, "Offline", 62),
                        FriendEntity("f5", "মেহেদী হাসান", "avatar_boy4", "frame_gold", 8, "Online", 50)
                    )
                )
            }
        }
    }
}
