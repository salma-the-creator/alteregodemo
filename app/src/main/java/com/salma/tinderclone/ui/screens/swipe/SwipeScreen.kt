package com.salma.tinderclone.ui.screens.swipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.salma.tinderclone.data.model.User
import com.salma.tinderclone.viewmodel.SwipeViewModel

// Colors
private val GreenHeart = Color(0xFF4CD964)
private val RedCross = Color(0xFFFF3B5C)
private val YellowStar = Color(0xFFFFD700)
private val PurpleBolt = Color(0xFFAA66FF)
private val GrayUndo = Color(0xFFBDBDBD)
private val BlueBadge = Color(0xFF1DA1F2)
private val GreenActive = Color(0xFF4CD964)
private val TagBackground = Color(0xFFF5F5F5)
private val TagBorder = Color(0xFFE0E0E0)

@Composable
fun SwipeScreen(viewModel: SwipeViewModel = viewModel()) {
    val matches = viewModel.potentialMatches
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PurpleBolt
            )
        } else if (error != null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Error: $error",
                    color = RedCross,
                    fontSize = 16.sp
                )
            }
        } else if (matches.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.SearchOff,
                    contentDescription = null,
                    tint = GrayUndo,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No more profiles nearby",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        } else {
            SwipeCardStack(
                matches = matches,
                onUndo = { /* Handle undo */ },
                onSwipeLeft = { user -> viewModel.recordSwipe(user.uid, isLiked = false) },
                onSuperLike = { user -> viewModel.recordSwipe(user.uid, isLiked = true) },
                onSwipeRight = { user -> viewModel.recordSwipe(user.uid, isLiked = true) },
                onBoost = { /* Handle boost */ }
            )
        }
    }
}

@Composable
fun SwipeCardStack(
    matches: List<User>,
    onUndo: () -> Unit,
    onSwipeLeft: (User) -> Unit,
    onSuperLike: (User) -> Unit,
    onSwipeRight: (User) -> Unit,
    onBoost: () -> Unit
) {
    val topUser = matches.firstOrNull() ?: return
    var currentPhotoIndex by remember { mutableStateOf(0) }
    val photoCount = topUser.photos.size.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            ProfileCardStyled(
                user = topUser,
                currentPhotoIndex = currentPhotoIndex,
                onPreviousPhoto = {
                    if (currentPhotoIndex > 0) currentPhotoIndex--
                },
                onNextPhoto = {
                    if (currentPhotoIndex < photoCount - 1) currentPhotoIndex++
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        ActionButtonsRow(
            onUndo = onUndo,
            onPass = { onSwipeLeft(topUser) },
            onSuperLike = { onSuperLike(topUser) },
            onLike = { onSwipeRight(topUser) },
            onBoost = onBoost
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileCardStyled(
    user: User,
    currentPhotoIndex: Int,
    onPreviousPhoto: () -> Unit,
    onNextPhoto: () -> Unit
) {
    val photoCount = user.photos.size.coerceAtLeast(1)
    val imageUrl = user.photos.getOrNull(currentPhotoIndex)
        ?: user.photos.firstOrNull()
        ?: ""

    Card(
        modifier = Modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Photo
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "${user.name}'s photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Photo Indicators at top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(photoCount) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (index == currentPhotoIndex) Color.White
                                else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            // Touch zones for photo navigation
            Row(modifier = Modifier.fillMaxSize()) {
                // Left tap zone
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onPreviousPhoto)
                )
                // Right tap zone
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onNextPhoto)
                )
            }

            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Info Button (top right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 180.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.KeyboardArrowUp,
                    contentDescription = "More info",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            // User Info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Recently Active Badge
                Surface(
                    color = GreenActive,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Recently Active",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Name, Age, Verified Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${user.getAge()}",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Verified Badge
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = "Verified",
                        tint = BlueBadge,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Matched Preferences
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Tune,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Matched 5+ Preferences",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tags
                PreferenceTagsSection()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreferenceTagsSection() {
    val tags = listOf(
        TagData("📷", "2 Photos"),
        TagData("👁️", "Long-term partner"),
        TagData("🌐", "English"),
        TagData("✨", "Better in person"),
        TagData("📍", "Time together"),
        TagData("🚭", "Non-smoker")
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            PreferenceTag(emoji = tag.emoji, text = tag.text)
        }
    }
}

data class TagData(val emoji: String, val text: String)

@Composable
fun PreferenceTag(emoji: String, text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun ActionButtonsRow(
    onUndo: () -> Unit,
    onPass: () -> Unit,
    onSuperLike: () -> Unit,
    onLike: () -> Unit,
    onBoost: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Undo Button (small)
        ActionButton(
            icon = Icons.Filled.Refresh,
            backgroundColor = Color.White,
            iconColor = GrayUndo,
            size = 48.dp,
            onClick = onUndo
        )

        // Pass Button (medium)
        ActionButton(
            icon = Icons.Filled.Close,
            backgroundColor = Color.White,
            iconColor = RedCross,
            size = 56.dp,
            onClick = onPass
        )

        // Super Like Button (medium)
        ActionButton(
            icon = Icons.Filled.Star,
            backgroundColor = Color.White,
            iconColor = YellowStar,
            size = 48.dp,
            onClick = onSuperLike
        )

        // Like Button (medium)
        ActionButton(
            icon = Icons.Filled.Favorite,
            backgroundColor = Color.White,
            iconColor = GreenHeart,
            size = 56.dp,
            onClick = onLike
        )

        // Boost Button (small)
        ActionButton(
            icon = Icons.Filled.Bolt,
            backgroundColor = Color.White,
            iconColor = PurpleBolt,
            size = 48.dp,
            onClick = onBoost
        )
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

// Keep old ProfileCard for compatibility
@Composable
fun ProfileCard(user: User) {
    ProfileCardStyled(
        user = user,
        currentPhotoIndex = 0,
        onPreviousPhoto = {},
        onNextPhoto = {}
    )
}