package com.example.game

data class DedicatedEmoji(
    val id: String,
    val emoji: String,
    val nameBn: String,
    val nameEn: String,
    val soundEffectDesc: String
)

val DEDICATED_EMOJIS = listOf(
    DedicatedEmoji("e_laugh", "😂", "হাসি", "Laugh", "হা হা হা"),
    DedicatedEmoji("e_cry", "😭", "কান্না", "Cry", "উহু উহু কান্না"),
    DedicatedEmoji("e_tease", "😏", "উসকানো", "Tease / Provoke", "কি বুঝলেন!"),
    DedicatedEmoji("e_angry", "😡", "রাগ", "Angry", "রেগে আগুন!"),
    DedicatedEmoji("e_tomato", "🍅", "টমেটো ছোড়া", "Tomato Throw", "টমেটো স্প্ল্যাশ!"),
    DedicatedEmoji("e_kiss", "😘", "ভালোবাসা", "Love Kiss", "ভালোবাসা"),
    DedicatedEmoji("e_fire", "🔥", "আগুনে চাল", "Fire", "দারুণ চাল!"),
    DedicatedEmoji("e_crown", "👑", "রাজা", "Crown", "লুডুর রাজা"),
    DedicatedEmoji("e_clap", "👏", "হাততালি", "Clap", "সাবাশ!"),
    DedicatedEmoji("e_mindblown", "🤯", "অবাক", "Mind Blown", "অবিশ্বাস্য!")
)

data class ActiveEmojiThrow(
    val id: Long = System.currentTimeMillis(),
    val senderColor: PlayerColor,
    val senderName: String,
    val targetColor: PlayerColor,
    val targetName: String,
    val emoji: String,
    val actionTextBn: String
)
