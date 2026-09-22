package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSubmitFeedback: (rating: Int, category: String, comments: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var selectedCategory by remember { mutableStateOf("স্মুথ গেমপ্লে") }
    var comments by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    val categories = listOf("স্মুথ গেমপ্লে", "ভয়েস চ্যাট", "গ্রাফিক্স ডিজাইন", "ফেয়ার প্লে", "অন্যান্য")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8)),
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
                        Icon(Icons.Default.RateReview, contentDescription = null, tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ইউজার ফিডব্যাক ও মতামত",
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

                if (isSubmitted) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉 মতামত দেওয়ার জন্য ধন্যবাদ!", color = Color(0xFF4ADE80), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "আপনার মূল্যবান মতামত গেমটিকে আরও উন্নত করতে সাহায্য করবে।", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                        ) {
                            Text("ঠিক আছে", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Star Rating
                    Text(text = "আপনার গেমিং অভিজ্ঞতা রেট করুন:", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i }) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "$i Stars",
                                    tint = if (i <= rating) Color(0xFFFFD700) else Color(0xFF64748B),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Chips
                    Text(text = "কোন বিষয়টি ভালো লেগেছে?", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Comment box
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        label = { Text("আপনার পরামর্শ বা মন্তব্য লিখুন...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().height(90.dp).testTag("feedback_comments_input"),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isSubmitted = true
                            onSubmitFeedback(rating, selectedCategory, comments)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("submit_feedback_button")
                    ) {
                        Text(text = "ফিডব্যাক জমা দিন", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
