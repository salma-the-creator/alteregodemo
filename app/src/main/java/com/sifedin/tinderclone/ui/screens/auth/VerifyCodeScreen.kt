package com.sifedin.tinderclone.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
fun VerifyCodeScreen(
    viewModel: AuthViewModel,
    onVerified: () -> Unit
) {
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    // 6 chiffres pour le code OTP
    val codeLength = 6
    var code by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(code) {
        viewModel.verificationCode = code
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

            Spacer(modifier = Modifier.weight(0.5f))

            // Instruction Text Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .padding(vertical = 20.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter the 6 digit codes we send to you",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 6 Code Input Boxes
            BasicTextField(
                value = code,
                onValueChange = { newValue ->
                    if (newValue.length <= codeLength && newValue.all { it.isDigit() }) {
                        code = newValue
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.focusRequester(focusRequester),
                decorationBox = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(codeLength) { index ->
                            val char = code.getOrNull(index)?.toString() ?: ""

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(
                                        width = if (code.length == index) 2.dp else 0.dp,
                                        color = if (code.length == index) ButtonPurple else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char,
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D1B4E),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }
                }
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Sign Up Button
            Button(
                onClick = { viewModel.verifyCode(onVerified) },
                enabled = code.length == codeLength && !isLoading,
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
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
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