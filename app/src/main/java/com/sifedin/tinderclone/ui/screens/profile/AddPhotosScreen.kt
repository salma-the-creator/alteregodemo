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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.sifedin.tinderclone.R
import com.sifedin.tinderclone.viewmodel.AuthViewModel

// Colors from design
private val PinkLight = Color(0xFFFCE4F3)
private val PinkMedium = Color(0xFFF8B4D9)
private val PurpleLight = Color(0xFFE991C5)
private val PurpleMedium = Color(0xFFD4A5E8)
private val PurpleDark = Color(0xFFB57EDC)
private val ButtonPurple = Color(0xFFD98ED9)
private val GreenAccent = Color(0xFF4CD964)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhotosScreen(
    viewModel: AuthViewModel = viewModel(),
    onContinue: () -> Unit
) {
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val maxPhotos = 6

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxPhotos)
    ) { uris ->
        if (uris.isNotEmpty()) {
            photoUris = (photoUris + uris).take(maxPhotos)
            viewModel.photoUris = photoUris
        }
    }

    val singlePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            if (photoUris.size < maxPhotos) {
                photoUris = photoUris + it
                viewModel.photoUris = photoUris
            }
        }
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        .background(GreenAccent)
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
                    text = "Add Photos",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Add your best photos",
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Select up to 6 photos to show on your profile",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Photo Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(maxPhotos) { index ->
                    PhotoSlotStyled(
                        uri = photoUris.getOrNull(index),
                        index = index,
                        onAdd = {
                            singlePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRemove = {
                            photoUris = photoUris.toMutableList().also {
                                if (index < it.size) it.removeAt(index)
                            }
                            viewModel.photoUris = photoUris
                        }
                    )
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

            Spacer(modifier = Modifier.height(16.dp))

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

                // Next/Done Button
                Button(
                    onClick = {
                        if (photoUris.isEmpty()) {
                            viewModel.errorMessage = "Please add at least one photo"
                        } else {
                            viewModel.saveProfile(onContinue)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonPurple,
                        disabledContainerColor = ButtonPurple.copy(alpha = 0.5f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Done",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoSlotStyled(
    uri: Uri?,
    index: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    val hasPhoto = uri != null

    Box(
        modifier = Modifier
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .border(
                width = if (hasPhoto) 2.dp else 1.dp,
                color = if (hasPhoto) ButtonPurple else Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !hasPhoto, onClick = onAdd)
    ) {
        if (hasPhoto) {
            // Photo
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Remove Button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Main Photo Badge (for first photo)
            if (index == 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ButtonPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Main",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        } else {
            // Add Photo Placeholder
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ButtonPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Photo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (index == 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Main",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Keep old PhotoSlot for compatibility
@Composable
fun PhotoSlot(uri: Uri?, onAdd: () -> Unit, onRemove: () -> Unit) {
    PhotoSlotStyled(
        uri = uri,
        index = 0,
        onAdd = onAdd,
        onRemove = onRemove
    )
}