package com.example.game

enum class GiftBoxRarity(val labelBn: String, val labelEn: String, val colorHex: Long) {
    WOODEN("কাঠের বক্স", "Wooden Box", 0xFF8D6E63),
    SILVER("সিলভার বক্স", "Silver Mystery", 0xFFCFD8DC),
    GOLDEN("গোল্ডেন রয়্যাল বক্স", "Royal Gold Chest", 0xFFFFD700)
}

data class GiftBoxItem(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val rarity: GiftBoxRarity,
    val offlinePoints: Long,
    val minCoins: Long,
    val maxCoins: Long,
    val minGems: Int,
    val maxGems: Int,
    val descriptionBn: String
)

data class GiftBoxReward(
    val boxName: String,
    val coinsWon: Long,
    val gemsWon: Int,
    val offlinePointsWon: Long,
    val specialBonus: String? = null
)

val DEFAULT_GIFT_BOXES = listOf(
    GiftBoxItem(
        id = "box_wooden",
        nameBn = "কাঠের উপহার বক্স",
        nameEn = "Wooden Gift Box",
        rarity = GiftBoxRarity.WOODEN,
        offlinePoints = 300L,
        minCoins = 200L,
        maxCoins = 500L,
        minGems = 0,
        maxGems = 2,
        descriptionBn = "অফলাইন ম্যাচ খেলে অর্জিত পুরস্কার"
    ),
    GiftBoxItem(
        id = "box_silver",
        nameBn = "সিলভার মিস্ট্রি বক্স",
        nameEn = "Silver Mystery Box",
        rarity = GiftBoxRarity.SILVER,
        offlinePoints = 800L,
        minCoins = 800L,
        maxCoins = 1800L,
        minGems = 2,
        maxGems = 6,
        descriptionBn = "অফলাইন উইন স্ট্রিক ও রিওয়ার্ডেড অ্যাড বোনাস"
    ),
    GiftBoxItem(
        id = "box_golden",
        nameBn = "গোল্ডেন রয়্যাল বক্স",
        nameEn = "Royal Gold Chest",
        rarity = GiftBoxRarity.GOLDEN,
        offlinePoints = 2500L,
        minCoins = 3000L,
        maxCoins = 6000L,
        minGems = 8,
        maxGems = 20,
        descriptionBn = "গ্র্যান্ডমাস্টার অফলাইন চ্যাম্পিয়ন পুরস্কার"
    )
)

object GiftBoxManager {
    fun generateReward(box: GiftBoxItem): GiftBoxReward {
        val coins = (box.minCoins..box.maxCoins).random()
        val gems = (box.minGems..box.maxGems).random()
        val bonus = when (box.rarity) {
            GiftBoxRarity.GOLDEN -> "👑 এক্সক্লুসিভ গোল্ডেন অ্যাভাটার ফ্রেম শার্ড!"
            GiftBoxRarity.SILVER -> "🎲 ডাইস স্কিন শার্ড"
            GiftBoxRarity.WOODEN -> null
        }
        return GiftBoxReward(
            boxName = box.nameBn,
            coinsWon = coins,
            gemsWon = gems,
            offlinePointsWon = box.offlinePoints,
            specialBonus = bonus
        )
    }
}
