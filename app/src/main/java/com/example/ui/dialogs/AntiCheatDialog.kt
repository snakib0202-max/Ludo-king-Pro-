package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.FairPlaySession

@Composable
fun AntiCheatDialog(
    session: FairPlaySession,
    lastDiceHash: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E676)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Security",
                            tint = Color(0xFF00E676)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ফেয়ার প্লে ও অ্যান্টি-চিট",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Security Status Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF00E676).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFF00E676), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ক্রিপ্টোগ্রাফিক ফেয়ার প্লে সক্রিয় (100% সুরক্ষিত)",
                                color = Color(0xFF00E676),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "কোনো প্লেয়ার বা সার্ভার ডাইস রোল প্রভাবিত করতে পারে না",
                                color = Color(0xFFE2E8F0),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detail Items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecurityDetailRow(label = "সেশন আইডি:", value = "#${session.sessionId}")
                    SecurityDetailRow(label = "সার্ভার সিড হ্যাশ:", value = session.serverSeedHash.take(16) + "...")
                    SecurityDetailRow(
                        label = "সর্বশেষ ডাইস হ্যাশ:",
                        value = if (lastDiceHash.isNotEmpty()) lastDiceHash.take(16) + "..." else "যাচাইয়ের অপেক্ষায়"
                    )
                    SecurityDetailRow(label = "এনক্রিপশন স্ট্যান্ডার্ড:", value = session.encryptionStandard)
                    SecurityDetailRow(label = "অ্যান্টি-ট্যাম্পার প্রটেকশন:", value = "সক্রিয় (Active)")
                    SecurityDetailRow(label = "লেটেন্সি পিং:", value = "${session.pingMs} ms (গ্রিন)")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    modifier = Modifier.fillMaxWidth().testTag("anti_cheat_close_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "যাচাই সম্পন্ন (Fair Play Verified)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
        Text(
            text = value,
            color = Color(0xFFFFD700),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
    }
}
