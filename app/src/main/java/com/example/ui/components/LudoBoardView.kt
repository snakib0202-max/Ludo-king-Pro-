package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.Coordinate
import com.example.game.GameState
import com.example.game.LudoTrackData
import com.example.game.PlayerColor
import com.example.game.Token
import com.example.game.TokenState

@Composable
fun LudoBoardView(
    gameState: GameState,
    boardSkinId: String,
    onTokenClick: (tokenId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9F9))
            .border(3.dp, Color(0xFF2C3E50), RoundedCornerShape(16.dp))
            .testTag("ludo_board")
    ) {
        val boardWidth = maxWidth
        val cellSize = boardWidth / 15f
        val density = LocalDensity.current

        // 1. Draw Canvas background, yards, tracks, and center home triangles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val cSize = w / 15f

            // Background fill
            drawRect(Color(0xFFFAFAFA), size = size)

            // Yard base rectangles (Authentic Ludo King Board)
            // Red (Top-Left 0..5, 0..5)
            drawRect(
                color = Color(0xFFE53935),
                topLeft = Offset(0f, 0f),
                size = Size(cSize * 6, cSize * 6)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(cSize * 1, cSize * 1),
                size = Size(cSize * 4, cSize * 4)
            )

            // Green (Top-Right 9..14, 0..5)
            drawRect(
                color = Color(0xFF43A047),
                topLeft = Offset(cSize * 9, 0f),
                size = Size(cSize * 6, cSize * 6)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(cSize * 10, cSize * 1),
                size = Size(cSize * 4, cSize * 4)
            )

            // Yellow (Bottom-Right 9..14, 9..14)
            drawRect(
                color = Color(0xFFFDD835),
                topLeft = Offset(cSize * 9, cSize * 9),
                size = Size(cSize * 6, cSize * 6)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(cSize * 10, cSize * 10),
                size = Size(cSize * 4, cSize * 4)
            )

            // Blue (Bottom-Left 0..5, 9..14)
            drawRect(
                color = Color(0xFF1E88E5),
                topLeft = Offset(0f, cSize * 9),
                size = Size(cSize * 6, cSize * 6)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(cSize * 1, cSize * 10),
                size = Size(cSize * 4, cSize * 4)
            )

            // Home Stretch columns
            // Red home stretch (cols 1..5, row 7)
            for (col in 1..5) {
                drawRect(
                    color = Color(0xFFE53935),
                    topLeft = Offset(col * cSize, 7 * cSize),
                    size = Size(cSize, cSize)
                )
            }
            // Green home stretch (col 7, rows 1..5)
            for (row in 1..5) {
                drawRect(
                    color = Color(0xFF43A047),
                    topLeft = Offset(7 * cSize, row * cSize),
                    size = Size(cSize, cSize)
                )
            }
            // Yellow home stretch (cols 9..13, row 7)
            for (col in 9..13) {
                drawRect(
                    color = Color(0xFFFDD835),
                    topLeft = Offset(col * cSize, 7 * cSize),
                    size = Size(cSize, cSize)
                )
            }
            // Blue home stretch (col 7, rows 9..13)
            for (row in 9..13) {
                drawRect(
                    color = Color(0xFF1E88E5),
                    topLeft = Offset(7 * cSize, row * cSize),
                    size = Size(cSize, cSize)
                )
            }

            // Start positions colored
            // Red start: (1, 6)
            drawRect(Color(0xFFFFCDD2), Offset(1 * cSize, 6 * cSize), Size(cSize, cSize))
            // Green start: (8, 1)
            drawRect(Color(0xFFC8E6C9), Offset(8 * cSize, 1 * cSize), Size(cSize, cSize))
            // Yellow start: (13, 8)
            drawRect(Color(0xFFFFF9C4), Offset(13 * cSize, 8 * cSize), Size(cSize, cSize))
            // Blue start: (6, 13)
            drawRect(Color(0xFFBBDEFB), Offset(6 * cSize, 13 * cSize), Size(cSize, cSize))

            // Center Home Triangles (6..8, 6..8)
            val center = Offset(7.5f * cSize, 7.5f * cSize)

            // Red triangle (Left)
            val redPath = Path().apply {
                moveTo(6 * cSize, 6 * cSize)
                lineTo(center.x, center.y)
                lineTo(6 * cSize, 9 * cSize)
                close()
            }
            drawPath(redPath, Color(0xFFE53935))

            // Green triangle (Top)
            val greenPath = Path().apply {
                moveTo(6 * cSize, 6 * cSize)
                lineTo(9 * cSize, 6 * cSize)
                lineTo(center.x, center.y)
                close()
            }
            drawPath(greenPath, Color(0xFF43A047))

            // Yellow triangle (Right)
            val yellowPath = Path().apply {
                moveTo(9 * cSize, 6 * cSize)
                lineTo(9 * cSize, 9 * cSize)
                lineTo(center.x, center.y)
                close()
            }
            drawPath(yellowPath, Color(0xFFFDD835))

            // Blue triangle (Bottom)
            val bluePath = Path().apply {
                moveTo(6 * cSize, 9 * cSize)
                lineTo(center.x, center.y)
                lineTo(9 * cSize, 9 * cSize)
                close()
            }
            drawPath(bluePath, Color(0xFF1E88E5))

            // Grid lines
            val lineStroke = Stroke(width = 1f)
            val gridColor = Color(0xFFB0BEC5)

            // Vertical lines in middle cross
            for (i in 6..9) {
                drawLine(gridColor, Offset(i * cSize, 0f), Offset(i * cSize, w), strokeWidth = 1.2f)
            }
            // Horizontal lines in middle cross
            for (i in 6..9) {
                drawLine(gridColor, Offset(0f, i * cSize), Offset(w, i * cSize), strokeWidth = 1.2f)
            }

            // Cell track grid lines
            for (i in 0..15) {
                // Top & Bottom vertical track segments
                drawLine(gridColor, Offset(i * cSize, 0f), Offset(i * cSize, 6 * cSize), strokeWidth = 0.8f)
                drawLine(gridColor, Offset(i * cSize, 9 * cSize), Offset(i * cSize, 15 * cSize), strokeWidth = 0.8f)

                // Left & Right horizontal track segments
                drawLine(gridColor, Offset(0f, i * cSize), Offset(6 * cSize, i * cSize), strokeWidth = 0.8f)
                drawLine(gridColor, Offset(9 * cSize, i * cSize), Offset(15 * cSize, i * cSize), strokeWidth = 0.8f)
            }

            // Yard base circles (token spots)
            for ((color, spots) in LudoTrackData.YARD_COORDINATES) {
                val circleColor = Color(color.baseColorHex)
                for (spot in spots) {
                    drawCircle(
                        color = circleColor,
                        radius = cSize * 0.42f,
                        center = Offset((spot.col + 0.5f) * cSize, (spot.row + 0.5f) * cSize)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = cSize * 0.36f,
                        center = Offset((spot.col + 0.5f) * cSize, (spot.row + 0.5f) * cSize)
                    )
                }
            }
        }

        // 2. Safe Stars overlay
        val starCoords = listOf(
            Coordinate(1, 6),   // Red Start
            Coordinate(8, 1),   // Green Start
            Coordinate(13, 8),  // Yellow Start
            Coordinate(6, 13),  // Blue Start
            Coordinate(6, 2),   // Safe Star
            Coordinate(12, 6),  // Safe Star
            Coordinate(8, 12),  // Safe Star
            Coordinate(2, 8)    // Safe Star
        )

        for (star in starCoords) {
            Box(
                modifier = Modifier
                    .offset(x = cellSize * star.col, y = cellSize * star.row)
                    .size(cellSize),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Safe Star",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(cellSize * 0.65f)
                )
            }
        }

        // 3. Tokens overlay
        val allTokens = gameState.tokens.values.flatten()

        // Group tokens by their coordinate to handle multi-token stack display
        val tokensByCoord = allTokens.groupBy { LudoTrackData.getTokenCoordinate(it) }

        for ((coord, tokensAtCoord) in tokensByCoord) {
            tokensAtCoord.forEachIndexed { index, token ->
                val isSelectable = gameState.canSelectToken &&
                        gameState.currentTurnColor == token.color &&
                        gameState.selectableTokenIds.contains(token.id)

                // Stacking offset if multiple tokens share the exact cell
                val stackOffsetX = if (tokensAtCoord.size > 1) {
                    when (index) {
                        0 -> (-3).dp
                        1 -> 3.dp
                        2 -> (-3).dp
                        else -> 3.dp
                    }
                } else 0.dp

                val stackOffsetY = if (tokensAtCoord.size > 1) {
                    when (index) {
                        0 -> (-3).dp
                        1 -> (-3).dp
                        2 -> 3.dp
                        else -> 3.dp
                    }
                } else 0.dp

                TokenView(
                    token = token,
                    isSelectable = isSelectable,
                    cellSize = cellSize,
                    coord = coord,
                    stackOffsetX = stackOffsetX,
                    stackOffsetY = stackOffsetY,
                    onTokenClick = {
                        if (isSelectable) {
                            onTokenClick(token.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TokenView(
    token: Token,
    isSelectable: Boolean,
    cellSize: androidx.compose.ui.unit.Dp,
    coord: Coordinate,
    stackOffsetX: androidx.compose.ui.unit.Dp,
    stackOffsetY: androidx.compose.ui.unit.Dp,
    onTokenClick: () -> Unit
) {
    val bounceAnim = remember { Animatable(1f) }

    LaunchedEffect(isSelectable) {
        if (isSelectable) {
            bounceAnim.animateTo(
                targetValue = 1.25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            bounceAnim.snapTo(1f)
        }
    }

    val tokenBaseColor = Color(token.color.baseColorHex)
    val tokenDarkColor = Color(token.color.darkColorHex)
    val tokenLightColor = Color(token.color.lightColorHex)

    val tokenSize = cellSize * 0.72f

    Box(
        modifier = Modifier
            .offset(
                x = (cellSize * coord.col) + (cellSize - tokenSize) / 2 + stackOffsetX,
                y = (cellSize * coord.row) + (cellSize - tokenSize) / 2 + stackOffsetY
            )
            .size(tokenSize)
            .scale(if (isSelectable) bounceAnim.value else 1f)
            .clickable(enabled = isSelectable) { onTokenClick() }
            .testTag("token_${token.color}_${token.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring if selectable
        if (isSelectable) {
            Box(
                modifier = Modifier
                    .size(tokenSize * 1.35f)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD700).copy(alpha = 0.45f))
                    .border(2.dp, Color(0xFFFFD700), CircleShape)
            )
        }

        // Token Body: Royal pawn shape with shadow
        Box(
            modifier = Modifier
                .size(tokenSize)
                .shadow(5.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            tokenLightColor,
                            tokenBaseColor,
                            tokenDarkColor
                        )
                    )
                )
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner core ring
            Box(
                modifier = Modifier
                    .size(tokenSize * 0.45f)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
                    .border(1.dp, tokenDarkColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Center crown dot
                Box(
                    modifier = Modifier
                        .size(tokenSize * 0.2f)
                        .clip(CircleShape)
                        .background(tokenBaseColor)
                )
            }
        }
    }
}
