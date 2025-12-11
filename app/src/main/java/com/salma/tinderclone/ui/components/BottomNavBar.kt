package com.salma.tinderclone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.salma.tinderclone.navigation.Routes

// Colors
private val PinkAccent = Color(0xFFE88ED4)
private val PinkLight = Color(0xFFF5B8E8)
private val GrayIcon = Color(0xFF9E9E9E)
private val BlackIcon = Color(0xFF1A1A1A)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide on auth screens
    val hideOnRoutes = listOf(
        Routes.PHONE, Routes.VERIFY, Routes.IDENTIFY,
        Routes.INTEREST, Routes.PHOTOS
    )

    if (currentRoute in hideOnRoutes) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        // Bottom Navigation Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                NavItem(
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    label = "Home",
                    isSelected = currentRoute == Routes.HOME,
                    onClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )

                // Likes
                NavItem(
                    icon = Icons.Outlined.FavoriteBorder,
                    selectedIcon = Icons.Filled.Favorite,
                    label = "Likes",
                    isSelected = currentRoute == Routes.LIKES,
                    onClick = {
                        navController.navigate(Routes.LIKES) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )

                // Center Button Spacer
                Spacer(modifier = Modifier.width(70.dp))

                // Chats
                NavItem(
                    icon = Icons.Outlined.Chat,
                    selectedIcon = Icons.Filled.Chat,
                    label = "Chats",
                    isSelected = currentRoute == Routes.CHATS,
                    onClick = {
                        navController.navigate(Routes.CHATS) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )

                // Settings
                NavItem(
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    label = "Settings",
                    isSelected = currentRoute == Routes.SETTINGS,
                    onClick = {
                        navController.navigate(Routes.SETTINGS) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
        }

        // Center Pink Button
        CenterButton(
            modifier = Modifier.align(Alignment.TopCenter),
            onClick = {
                // Navigate to home/swipe
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            }
        )
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else icon,
            contentDescription = label,
            tint = if (isSelected) BlackIcon else GrayIcon,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) BlackIcon else GrayIcon
        )
    }
}

@Composable
private fun CenterButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .offset(y = (-10).dp)
            .size(60.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = PinkAccent.copy(alpha = 0.3f),
                spotColor = PinkAccent.copy(alpha = 0.3f)
            )
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PinkLight,
                        PinkAccent
                    )
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Diamond/Star icon in center
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "Home",
                tint = PinkAccent,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
