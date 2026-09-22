package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import com.example.data.ShopItemEntity
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination

@Composable
fun ShopScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()
    val shopItems by viewModel.shopItems.collectAsState()
    val isBn = user?.languageBn ?: true

    val categories = listOf("BOARD", "DICE", "FRAME", "AVATAR")
    val categoryTitles = if (isBn) listOf("বোর্ড স্কিন", "ডাইস স্কিন", "ফ্রেম", "অবতার") else listOf("Boards", "Dice", "Frames", "Avatars")

    var selectedTab by remember { mutableIntStateOf(0) }
    val currentCategory = categories[selectedTab]
    val filteredItems = shopItems.filter { it.category == currentCategory }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Surface(
            color = Color(0xFF0F172A),
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(ScreenDestination.HOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "স্কিন ও আইটেম শপ" else "Skins & Item Shop",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Coin & Recharge Shortcut
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                    modifier = Modifier.clickable { viewModel.showPaymentDialog.value = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.coins ?: 0}",
                            color = Color(0xFFFFD700),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                    }
                }
            }
        }

        // Category Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0F172A),
            contentColor = Color(0xFFFFD700),
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .background(Color(0xFFFFD700), RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                )
            }
        ) {
            categoryTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        // Shop Items Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredItems) { item ->
                val isEquipped = when (item.category) {
                    "BOARD" -> user?.boardSkinId == item.id
                    "DICE" -> user?.diceSkinId == item.id
                    "FRAME" -> user?.frameId == item.id
                    "AVATAR" -> user?.avatarId == item.id
                    else -> false
                }

                ShopItemCard(
                    item = item,
                    isBn = isBn,
                    isEquipped = isEquipped,
                    canAfford = (user?.coins ?: 0) >= item.priceCoins,
                    onAction = {
                        viewModel.purchaseOrEquipShopItem(item)
                    },
                    onWatchAdToUnlock = {
                        viewModel.isBankruptRecoveryAd.value = false
                        viewModel.showRewardedAdDialog.value = true
                    }
                )
            }
        }
    }
}

@Composable
fun ShopItemCard(
    item: ShopItemEntity,
    isBn: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onAction: () -> Unit,
    onWatchAdToUnlock: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isEquipped) 2.dp else 1.dp,
            color = if (isEquipped) Color(0xFF00E676) else Color(0xFF334155)
        ),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Visual Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(item.accentColorHex),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (item.category) {
                            "BOARD" -> "🎯"
                            "DICE" -> "🎲"
                            "FRAME" -> "👑"
                            else -> "👤"
                        },
                        fontSize = 32.sp
                    )
                    if (isEquipped) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00E676))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("EQUIPPED", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isBn) item.nameBn else item.nameEn,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (item.isUnlocked) {
                Button(
                    onClick = onAction,
                    enabled = !isEquipped,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEquipped) Color(0xFF334155) else Color(0xFFFFD700)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("equip_item_${item.id}")
                ) {
                    Text(
                        text = if (isEquipped) (if (isBn) "ব্যবহার হচ্ছে" else "In Use") else (if (isBn) "ব্যবহার করুন" else "Equip"),
                        color = if (isEquipped) Color(0xFF94A3B8) else Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = onAction,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("unlock_item_${item.id}")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.priceCoins} 🪙",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Watch Ad to Unlock Shortcut
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00C853).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF00C853), RoundedCornerShape(8.dp))
                            .clickable { onWatchAdToUnlock() }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (isBn) "বিজ্ঞাপনে ফ্রি" else "Ad Unlock",
                                color = Color(0xFF00E676),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
