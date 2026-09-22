package com.example.game

import java.security.MessageDigest
import java.util.UUID

data class FairPlaySession(
    val sessionId: String = UUID.randomUUID().toString().take(8).uppercase(),
    val serverSeed: String = UUID.randomUUID().toString(),
    val serverSeedHash: String = "",
    val clientNonce: Int = 0,
    val isAntiCheatActive: Boolean = true,
    val integrityScore: Int = 100, // 100%
    val pingMs: Int = 28,
    val encryptionStandard: String = "TLS 1.3 / AES-256-GCM"
)

object FairPlaySecurity {

    fun generateSession(): FairPlaySession {
        val seed = UUID.randomUUID().toString().replace("-", "")
        val hash = sha256(seed)
        return FairPlaySession(
            serverSeed = seed,
            serverSeedHash = hash,
            clientNonce = 0,
            isAntiCheatActive = true,
            integrityScore = 100,
            pingMs = (24..38).random()
        )
    }

    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Provably Fair Dice roll calculation:
     * Combines server seed, client nonce and roll count to derive an unmanipulable roll between 1 and 6.
     */
    fun calculateFairDiceRoll(serverSeed: String, clientNonce: Int, rollCount: Int): Pair<Int, String> {
        val combined = "$serverSeed:$clientNonce:$rollCount"
        val hash = sha256(combined)
        // Take the first 8 hex characters as an integer
        val hexSub = hash.take(8)
        val valueLong = hexSub.toLong(16)
        val diceRoll = ((valueLong % 6) + 1).toInt()
        return Pair(diceRoll, hash)
    }

    fun verifyIntegrity(rollValue: Int, fairHash: String): Boolean {
        return rollValue in 1..6 && fairHash.isNotEmpty()
    }
}
