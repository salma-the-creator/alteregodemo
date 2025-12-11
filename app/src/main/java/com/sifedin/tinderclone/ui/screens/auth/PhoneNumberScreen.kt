package com.sifedin.tinderclone.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifedin.tinderclone.R
import com.sifedin.tinderclone.viewmodel.AuthViewModel

// Colors from design
private val PinkLight = Color(0xFFF8B4D9)
private val PinkMedium = Color(0xFFE991C5)
private val PurpleLight = Color(0xFFD4A5E8)
private val PurpleMedium = Color(0xFFB57EDC)
private val ButtonPurple = Color(0xFF8B6FC0)
private val LinkPurple = Color(0xFF7C5CBF)
private val TextGray = Color(0xFF8E8E93)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberScreen(
    viewModel: AuthViewModel,
    onCodeSent: () -> Unit
) {
    val context = LocalContext.current
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PinkLight,
                        PinkMedium,
                        PurpleLight,
                        PurpleMedium
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
            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* Handle back */ }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ALTEREGO Logo
            Image(
                painter = painterResource(id = R.drawable.logo_alterego_main),
                contentDescription = "ALTEREGO",
                modifier = Modifier
                    .width(180.dp)
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Phone Number Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Country Code Box
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+212",
                        fontSize = 16.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }

                // Phone Number TextField
                TextField(
                    value = viewModel.phoneNumber,
                    onValueChange = { viewModel.phoneNumber = it },
                    placeholder = {
                        Text(
                            text = "Enter your phone number",
                            color = TextGray
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = ButtonPurple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms & Conditions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "I agree to the ",
                    color = Color.Black.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = "terms & conditions",
                    color = LinkPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* Handle click */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            Button(
                onClick = {
                    val activity = context as Activity
                    viewModel.sendVerificationCode(
                        activity = activity,
                        onCodeSent = onCodeSent
                    )
                },
                enabled = !isLoading && viewModel.phoneNumber.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPurple,
                    disabledContainerColor = ButtonPurple.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Sign up",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Already have an account
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an\naccount?",
                    color = Color.Black.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in here",
                    color = LinkPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { /* Handle sign in */ }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}