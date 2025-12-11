package com.sifedin.tinderclone.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.sifedin.tinderclone.R
import com.sifedin.tinderclone.viewmodel.AuthViewModel

// Colors from design
private val PinkLight = Color(0xFFF8B4D9)
private val PinkMedium = Color(0xFFE991C5)
private val PurpleLight = Color(0xFFD4A5E8)
private val PurpleMedium = Color(0xFFB57EDC)
private val PurpleDark = Color(0xFF8B5A9E)
private val ButtonPurple = Color(0xFF8B6FC0)
private val BlueAccent = Color(0xFF1E90FF)
private val TextGray = Color(0xFF8E8E93)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentifyYourselfScreen(
    viewModel: AuthViewModel,
    onContinue: () -> Unit
) {
    val error = viewModel.errorMessage
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedStatus by remember { mutableStateOf("") }
    var showStatusDropdown by remember { mutableStateOf(false) }

    val statusOptions = listOf(
        "Looking for relationship",
        "Just chatting",
        "Making friends",
        "Not sure yet"
    )

    // Photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { profileImageUri = it }
    }

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
            // Header with Back Button and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Handle back */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Back",
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer to balance the back button
                Spacer(modifier = Modifier.width(60.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ALTEREGO Logo Image
            Image(
                painter = painterResource(id = R.drawable.logo_alterego_main),
                contentDescription = "ALTEREGO",
                modifier = Modifier
                    .width(180.dp)
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Avatar with Camera Button
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(profileImageUri),
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Add Photo",
                            tint = Color.LightGray,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // Camera Button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BlueAccent)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Name Input Field
            TextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                placeholder = {
                    Text(
                        text = "Enter your name or nickname",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = ButtonPurple
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description Input Field
            var description by remember { mutableStateOf("") }
            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text(
                        text = "Enter a description",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = ButtonPurple
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Status Dropdown
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedStatus,
                    onValueChange = { },
                    placeholder = {
                        Text(
                            text = "select a statut",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                    },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = TextGray,
                            modifier = Modifier.clickable { showStatusDropdown = true }
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = ButtonPurple
                    ),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { showStatusDropdown = true }
                )

                DropdownMenu(
                    expanded = showStatusDropdown,
                    onDismissRequest = { showStatusDropdown = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White)
                ) {
                    statusOptions.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                selectedStatus = status
                                showStatusDropdown = false
                            }
                        )
                    }
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Complete Button
            Button(
                onClick = {
                    when {
                        viewModel.name.isBlank() -> viewModel.errorMessage = "Enter your name"
                        else -> {
                            viewModel.errorMessage = null
                            onContinue()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPurple.copy(alpha = 0.7f),
                    disabledContainerColor = ButtonPurple.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = "Complete",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// Keep the old GenderButton for compatibility if needed elsewhere
@Composable
fun GenderButton(
    label: String,
    selected: Boolean,
    primaryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) primaryColor else Color.White,
            contentColor = if (selected) Color.White else primaryColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(label)
    }
}