package com.salma.tinderclone.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salma.tinderclone.viewmodel.AuthViewModel

// Colors from design
private val PinkLight = Color(0xFFFCE4F3)
private val PinkMedium = Color(0xFFF8B4D9)
private val PurpleLight = Color(0xFFE991C5)
private val PurpleMedium = Color(0xFFD4A5E8)
private val PurpleDark = Color(0xFFB57EDC)
private val ButtonPurple = Color(0xFFD98ED9)
private val ButtonPurpleDark = Color(0xFFC77DC7)
private val ChipBackground = Color(0xFFFDF5FC)
private val ChipBorder = Color(0xFFE8E8E8)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF666666)

// Interest data class with emoji
data class Interest(
    val emoji: String,
    val name: String
)

// List of interests matching the design
private val allInterests = listOf(
    Interest("⚽", "Sport"),
    Interest("🎵", "Music"),
    Interest("✈️", "Travelling"),
    Interest("👗", "Fashion"),
    Interest("🎮", "Games"),
    Interest("💻", "Technology"),
    Interest("💄", "Beauty"),
    Interest("🍔", "Food"),
    Interest("😄", "Comedy"),
    Interest("💅", "Skincare"),
    Interest("🧘", "Wellness"),
    Interest("👜", "Bag"),
    Interest("🧢", "Accessories"),
    Interest("🏛️", "Architecture"),
    Interest("🖌️", "Art"),
    Interest("🎬", "Film"),
    Interest("📅", "Calendar"),
    Interest("🖨️", "Printer")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InterestedScreen(
    viewModel: AuthViewModel,
    onContinue: () -> Unit
) {
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }

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
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (green circle)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CD964))
                        .clickable { /* Handle back */ },
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
                    text = "Your Interests",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer to balance
                Spacer(modifier = Modifier.width(36.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Choose your interests",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Get better recommendations.",
                fontSize = 15.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Interest Chips - Flow Layout
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                allInterests.forEach { interest ->
                    InterestChip(
                        interest = interest,
                        isSelected = selectedInterests.contains(interest.name),
                        onClick = {
                            selectedInterests = if (selectedInterests.contains(interest.name)) {
                                selectedInterests - interest.name
                            } else {
                                selectedInterests + interest.name
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Skip Button
                Button(
                    onClick = { onContinue() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Skip",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                // Next Button with gradient-like effect
                Button(
                    onClick = {
                        // Save selected interests if needed
                        viewModel.errorMessage = null
                        onContinue()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonPurple
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun InterestChip(
    interest: Interest,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) ButtonPurple.copy(alpha = 0.2f) else ChipBackground,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) ButtonPurple else ChipBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = interest.emoji,
                fontSize = 16.sp
            )
            Text(
                text = interest.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) ButtonPurple else TextDark
            )
        }
    }
}

// Keep OptionButton for backward compatibility
@Composable
fun OptionButton(label: String, selected: Boolean, onClick: () -> Unit) {
    val primaryColor = ButtonPurple

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) primaryColor else Color.White,
            contentColor = if (selected) Color.White else primaryColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(label)
    }
}