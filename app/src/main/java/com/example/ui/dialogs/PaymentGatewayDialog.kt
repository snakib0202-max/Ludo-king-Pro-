package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

data class CoinPackage(
    val id: String,
    val coins: Long,
    val bonus: String,
    val priceBdt: String
)

val COIN_PACKAGES = listOf(
    CoinPackage("p1", 5000L, "+0%", "৳৫০"),
    CoinPackage("p2", 25000L, "+10% বোনাস", "৳২০০"),
    CoinPackage("p3", 100000L, "+25% বোনাস", "৳৭০০"),
    CoinPackage("p4", 500000L, "+50% ভিআইপি", "৳৩,০০০")
)

@Composable
fun PaymentGatewayDialog(
    onDismiss: () -> Unit,
    onPaymentSuccess: (coinsEarned: Long) -> Unit
) {
    var selectedPackage by remember { mutableStateOf(COIN_PACKAGES[1]) }
    var selectedGateway by remember { mutableStateOf("bKash") }
    var phoneNumber by remember { mutableStateOf("01712345678") }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentSuccessReceipt by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD700)),
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
                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "পেমেন্ট গেটওয়ে / Wallet",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (paymentSuccessReceipt != null) {
                    // Success View
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "পেমেন্ট সফল হয়েছে!",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+${selectedPackage.coins} কয়েন আপনার অ্যাকাউন্টে যোগ হয়েছে",
                            color = Color(0xFFFFD700),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "TxID: $paymentSuccessReceipt",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                onPaymentSuccess(selectedPackage.coins)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            modifier = Modifier.fillMaxWidth().testTag("payment_confirm_button")
                        ) {
                            Text("ঠিক আছে", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Coin Package selector
                    Text(
                        text = "কয়েন প্যাকেজ নির্বাচন করুন:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (pkg in COIN_PACKAGES) {
                            val isSelected = pkg.id == selectedPackage.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.5f))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFFFD700) else Color(0xFF334155),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedPackage = pkg }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${pkg.coins / 1000}K",
                                        color = Color(0xFFFFD700),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = pkg.priceBdt,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = pkg.bonus,
                                        color = Color(0xFF4ADE80),
                                        fontSize = 8.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gateway selector
                    Text(
                        text = "পেমেন্ট মাধ্যম বেছে নিন:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val gateways = listOf(
                        Triple("bKash", "বিকাশ", Color(0xFFD81B60)),
                        Triple("Nagad", "নগদ", Color(0xFFF57C00)),
                        Triple("Rocket", "রকেট", Color(0xFF8E24AA)),
                        Triple("Card", "কার্ড", Color(0xFF1E88E5))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        gateways.forEach { (id, nameBn, color) ->
                            val isSelected = selectedGateway == id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) color.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) color else Color(0xFF334155),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedGateway = id }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = nameBn,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("$selectedGateway একাউন্ট নম্বর", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("payment_phone_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pay Button
                    Button(
                        onClick = {
                            isProcessing = true
                            scope.launch {
                                delay(1200)
                                isProcessing = false
                                paymentSuccessReceipt = "LUDO-" + UUID.randomUUID().toString().take(8).uppercase()
                            }
                        },
                        enabled = !isProcessing && phoneNumber.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier.fillMaxWidth().testTag("proceed_payment_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("প্রসেসিং হচ্ছে...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = "${selectedPackage.priceBdt} পরিশোধ করুন",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
