package com.example.ai

data class AiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

val INITIAL_AI_SUGGESTIONS = listOf(
    "লুডুতে জেতার সেরা স্ট্র্যাটেজি কী?",
    "অফলাইন গিফট বক্স কীভাবে পাব?",
    "ফেয়ার প্লে সিকিউরিটি কীভাবে কাজ করে?",
    "বিজ্ঞাপন দেখে কীভাবে পয়েন্ট রিকভার করব?",
    "বন্ধুদের সাথে প্রাইভেট রুমে কীভাবে খেলব?",
    "লুডু খেলার আন্তর্জাতিক নিয়মগুলো কী কী?"
)

object LudoAIAgent {
    fun generateResponse(userPrompt: String, isBn: Boolean): String {
        val q = userPrompt.trim().lowercase()

        return when {
            q.contains("স্ট্র্যাটেজি") || q.contains("কৌশল") || q.contains("strategy") || q.contains("জিতব") || q.contains("win") -> {
                if (isBn) {
                    "🎯 লুডু কিং প্রো-তে জেতার সেরা ৩টি প্রো-টিপস:\n\n" +
                            "১. **সবগুলো গুটি বের করুন**: শুধুমাত্র ১টি গুটি নিয়ে দৌড়াবেন না। ছক্কা পড়লে নতুন গুটি বের করে বোর্ডে ছড়িয়ে রাখুন। এতে opponent-কে ব্লক করা সহজ হয়।\n" +
                            "২. **নিরাপদ স্টার (Safe Star) সেল ব্যবহার করুন**: লাল, সবুজ, হলুদ ও নীল স্টার ঘরে আপনার গুটি সম্পূর্ণ সুরক্ষিত, কেউ কাটতে পারবে না।\n" +
                            "৩. **গুটি কাটার সুযোগ নিন**: প্রতিপক্ষের গুটি কাটলে আপনি একটি বোনাস ফ্রি চাল পাবেন এবং প্রতিপক্ষকে পুনরায় স্টার্টিং বেসে ফেরত পাঠানো যাবে!\n" +
                            "৪. **হোম স্ট্রেচে ধৈর্য রাখুন**: শেষের ৬ ঘরে ছক্কা বা বড় সংখ্যা সাবধানে হিসাব করে গুটি হোমে প্রবেশ করান।"
                } else {
                    "🎯 Top 3 Winning Strategies in Ludo King Pro:\n\n" +
                            "1. **Spread your tokens**: Don't run with just one token. Open multiple tokens to create tactical blockade options.\n" +
                            "2. **Utilize Safe Stars**: Position your tokens on star colored squares where they cannot be captured.\n" +
                            "3. **Prioritize Captures**: Capturing opponent tokens gives you an immediate extra turn and sends them back to their yard!"
                }
            }

            q.contains("গিফট বক্স") || q.contains("gift box") || q.contains("অফলাইন") || q.contains("offline") || q.contains("বাক্স") -> {
                if (isBn) {
                    "🎁 অফলাইন গিফট বক্স ও রিওয়ার্ড সিস্টেম:\n\n" +
                            "• আপনি যখন ইন্টারনেট ছাড়া 'কম্পিউটার (AI)' অথবা 'পাস অ্যান্ড প্লে' খেলবেন, তখন প্রতিটি জয়ে 'অফলাইন পয়েন্ট' ও গিফট বক্স পাবেন!\n" +
                            "• **কাঠের বক্স**: ৩০০ অফলাইন পয়েন্ট + ৫০০ কয়েন পর্যন্ত।\n" +
                            "• **সিলভার মিস্ট্রি বক্স**: ৮০০ অফলাইন পয়েন্ট + ১,৮০০ কয়েন + ফ্রি জেম।\n" +
                            "• **গোল্ডেন রয়্যাল বক্স**: ২,৫০০ পয়েন্ট + ৬,০০০ কয়েন + এক্সক্লুসিভ অ্যাভাটার ফ্রেম!\n\n" +
                            "💡 অনলাইনে আপনি স্পন্সরড ভিডিও অ্যাড দেখে বিনামূল্যে তাৎক্ষণিক গিফট বক্স আনলক করতে পারবেন!"
                } else {
                    "🎁 Offline Rewards & Gift Boxes:\n\n" +
                            "• Playing offline vs Computer or Pass & Play earns Offline Experience Points and Mystery Chests!\n" +
                            "• **Wooden Chest**: Up to 500 Coins & 300 Offline Points.\n" +
                            "• **Silver Mystery**: Up to 1,800 Coins & Free Gems.\n" +
                            "• **Royal Gold Chest**: 6,000 Coins + Exclusive Avatar Frame!\n" +
                            "💡 You can also watch rewarded ads to open instant Mystery Gift Boxes!"
                }
            }

            q.contains("ফেয়ার প্লে") || q.contains("fair play") || q.contains("চিট") || q.contains("cheat") || q.contains("হ্যাক") || q.contains("ডাইস") || q.contains("dice") -> {
                if (isBn) {
                    "🛡️ ১০০% সিকিউরড ও ক্রিপ্টোগ্রাফিক ফেয়ার প্লে:\n\n" +
                            "• এই অ্যাপে কোনো প্লেয়ার বা এমনকি সার্ভারও ডাইসের ফলাফল পরিবর্তন করতে পারে না।\n" +
                            "• প্রতিটি ডাইস রোলের জন্য **SHA-256 ক্রিপ্টোগ্রাফিক হ্যাশ** ব্যবহার করা হয় (Server Seed + Client Nonce)।\n" +
                            "• প্রোফাইল পেজে গিয়ে আপনি ডাইস ফ্রিকোয়েন্সি ডিস্ট্রিবিউশন গ্রাফ যাচাই করতে পারেন, যা প্রমাণ করে ১ থেকে ৬ পড়ার সম্ভাবনা সম্পূর্ণ নিরপেক্ষ।"
                } else {
                    "🛡️ Cryptographic Fair Play & Anti-Cheat:\n\n" +
                            "• Every roll is governed by a provably fair SHA-256 hash algorithm (Server Seed + Client Nonce).\n" +
                            "• Impossible to manipulate or rig. Inspect the anti-cheat status dialog during any match for live hash proofs!"
                }
            }

            q.contains("কয়েন") || q.contains("coin") || q.contains("টাকা") || q.contains("পয়েন্ট") || q.contains("পেমেন্ট") || q.contains("bkash") || q.contains("বিকাশ") -> {
                if (isBn) {
                    "🪙 কয়েন ও রিচার্জ সংক্রান্ত তথ্য:\n\n" +
                            "১. **ফ্রি কয়েন অ্যাড**: হোম স্ক্রিনের 'ফ্রি কয়েন' বাটনে চাপ দিয়ে ছোট ভিডিও বিজ্ঞাপন দেখে প্রতিবার ১,০০০+ কয়েন ইনকাম করতে পারবেন।\n" +
                            "২. **পয়েন্ট রিকভারি**: ম্যাচে কয়েন হেরে গেলেও বিজ্ঞাপন দেখে কয়েন রিকভার করা যায়।\n" +
                            "৩. **পেমেন্ট গেটওয়ে**: বিকাশ, নগদ, রকেট বা কার্ডের মাধ্যমে সহজে ইনস্ট্যান্ট কয়েন টপ-আপ করতে পারবেন।"
                } else {
                    "🪙 Coins & Payment Methods:\n\n" +
                            "1. Earn free coins by watching rewarded video ads anytime.\n" +
                            "2. Recover lost coins with Bankrupt Recovery ads.\n" +
                            "3. Top up directly via bKash, Nagad, Rocket, and Cards with instant TxID receipts."
                }
            }

            q.contains("ভয়েস") || q.contains("voice") || q.contains("চ্যাট") || q.contains("chat") || q.contains("ইমোজি") || q.contains("emoji") -> {
                if (isBn) {
                    "🎙️ ভয়েস চ্যাট ও ডেডিকেটেড ইমোজি থ্রো:\n\n" +
                            "• খেলার সময় ওপরের মাইক আইকনে ট্যাপ করে ভয়েস চ্যাট অন/অফ করতে পারবেন।\n" +
                            "• ইন-গেম চ্যাট অপশনে গিয়ে প্রতিপক্ষকে বিভিন্ন ইমোজি (হাসি 😂, কান্না 😭, উসকানো 😏, টমেটো 🍅) সরাসরি ছুড়ে মারতে পারবেন!\n" +
                            "• পাশাপাশি যে কোনো কাস্টম বাংলা বা ইংরেজি মেসেজ টাইপ করে রিয়েল-টাইমে চ্যাট করতে পারবেন।"
                } else {
                    "🎙️ Voice Chat & Dedicated Emojis:\n\n" +
                            "• Toggle microphone anytime with active audio waveform equalization.\n" +
                            "• Send/throw interactive dedicated emojis (Laugh 😂, Cry 😭, Provoke 😏, Tomato 🍅) directly at opponents!\n" +
                            "• Type custom in-game chat messages in real time."
                }
            }

            q.contains("কেমন") || q.contains("how are you") || q.contains("হাই") || q.contains("hello") || q.contains("hi") || q.contains("সালাম") -> {
                if (isBn) {
                    "হ্যালো! আমি আপনার 'লুডু কিং প্রো এআই সহকারী' 🤖✨। আমি চমৎকার আছি! আজ আপনাকে লুডু খেলার কোনো নিয়ম, স্ট্র্যাটেজি বা অন্য যেকোনো বিষয়ে কীভাবে সাহায্য করতে পারি?"
                } else {
                    "Hello! I am your Ludo King Pro AI Assistant 🤖✨. I'm doing great! How can I assist you today with game strategies, rules, or anything else?"
                }
            }

            q.contains("বাংলাদেশ") || q.contains("bangladesh") || q.contains("ঢাকা") -> {
                if (isBn) {
                    "🇧🇩 বাংলাদেশ একটি সুন্দর ও সমৃদ্ধ সংস্কৃতির দেশ! বাংলাদেশে লুডু একটি অত্যন্ত জনপ্রিয় ও ঐতিহ্যবাহী খেলা, যা পরিবার ও বন্ধুদের মাঝে উৎসবের আমেজ তৈরি করে। আমাদের এই অ্যাপে বাংলাদেশি খেলোয়াড়দের জন্য বিকাশ ও নগদ পেমেন্ট গেটওয়ে এবং সম্পূর্ণ বাংলা ইন্টারফেস যুক্ত করা হয়েছে।"
                } else {
                    "🇧🇩 Bangladesh is known for its rich culture and love for board games like Ludo! Our app includes localized Bengali support, bKash & Nagad payments for our community."
                }
            }

            else -> {
                // General query handler
                if (isBn) {
                    "ধন্যবাদ আপনার সুন্দর প্রশ্নের জন্য! 🤖\n\n" +
                            "লুডু কিং প্রো সম্পর্কে আরও জানতে পারেন:\n" +
                            "• অফলাইনে খেললে দারুণ 'গিফট বক্স' ও পয়েন্ট পাওয়া যায়।\n" +
                            "• অনলাইনে রিয়েল-টাইমে ভয়েস চ্যাট ও লাইভ ইমোজি থ্রো করা যায়।\n" +
                            "• কোনো বিশেষ কৌশল বা অ্যাপের ফিচার সম্পর্কে জানতে চাইলে আমাকে যেকোনো সময় প্রশ্ন করুন!"
                } else {
                    "Thank you for your question! 🤖\n\n" +
                            "Feel free to ask me anything about game rules, winning strategies, offline mystery gift boxes, provably fair dice hashing, or in-game voice chat!"
                }
            }
        }
    }
}
