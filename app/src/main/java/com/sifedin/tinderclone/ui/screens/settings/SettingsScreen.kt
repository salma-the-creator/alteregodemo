package com.sifedin.tinderclone.ui.screens.settings

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.sifedin.tinderclone.R
import com.sifedin.tinderclone.viewmodel.SettingsViewModel

// Colors from design
private val PinkLight = Color(0xFFFCE4F3)
private val PinkMedium = Color(0xFFF8B4D9)
private val PurpleLight = Color(0xFFE991C5)
private val PurpleMedium = Color(0xFFD4A5E8)
private val PurpleDark = Color(0xFFB57EDC)
private val ButtonPurple = Color(0xFFD98ED9)
private val GreenAccent = Color(0xFF4CD964)
private val RedDelete = Color(0xFFFF3B5C)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8E8E93)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToDeleteAccount: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PinkLight,
                        PinkMedium,
                        PurpleLight,
                        PurpleMedium,
                        PurpleDark
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (green circle) - optional, remove if not needed
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GreenAccent)
                        .clickable { /* Handle back if needed */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer to balance
                Spacer(modifier = Modifier.width(36.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ALTEREGO Logo Image
            Image(
                painter = painterResource(id = R.drawable.logo_alterego_main),
                contentDescription = "ALTEREGO",
                modifier = Modifier
                    .width(180.dp)
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.LightGray,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = "Your Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Settings Items
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Edit Profile
                SettingsItemStyled(
                    icon = Icons.Outlined.Edit,
                    title = "Edit Profile",
                    subtitle = "Update your information",
                    onClick = onNavigateToEditProfile
                )

                // Notifications
                SettingsItemStyled(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifications",
                    subtitle = "Manage your notifications",
                    onClick = { /* Handle */ }
                )

                // Privacy
                SettingsItemStyled(
                    icon = Icons.Outlined.Lock,
                    title = "Privacy",
                    subtitle = "Control your privacy settings",
                    onClick = { /* Handle */ }
                )

                // Help & Support
                SettingsItemStyled(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Help & Support",
                    subtitle = "Get help or contact us",
                    onClick = { /* Handle */ }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Delete Account
                SettingsItemStyled(
                    icon = Icons.Outlined.DeleteOutline,
                    title = "Delete Account",
                    subtitle = "Permanently delete your account",
                    onClick = onNavigateToDeleteAccount,
                    isDestructive = true
                )

                // Logout
                SettingsItemStyled(
                    icon = Icons.Outlined.Logout,
                    title = "Logout",
                    subtitle = "Sign out of your account",
                    onClick = { showLogoutDialog = true },
                    isDestructive = true
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // App Version
            Text(
                text = "Version 1.0.0",
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = "Logout",
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to logout?",
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.logout()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedDelete
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Logout", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLogoutDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = TextDark)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsItemStyled(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDestructive) RedDelete.copy(alpha = 0.1f)
                        else ButtonPurple.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isDestructive) RedDelete else ButtonPurple,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) RedDelete else TextDark
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextGray
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Keep old SettingsItem for compatibility
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    color: Color = TextDark
) {
    SettingsItemStyled(
        icon = icon,
        title = title,
        subtitle = "",
        onClick = onClick,
        isDestructive = color == RedDelete
    )
}