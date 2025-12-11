package com.sifedin.tinderclone.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifedin.tinderclone.R

// Colors from design
private val PinkLight = Color(0xFFFCE4F3)
private val PinkMedium = Color(0xFFF8B4D9)
private val PurpleLight = Color(0xFFE991C5)
private val PurpleMedium = Color(0xFFD4A5E8)
private val PurpleDark = Color(0xFFB57EDC)
private val LinkBlue = Color(0xFF4A90D9)

@Composable
fun SignInScreen(
    onGoogleSignIn: () -> Unit,      // -> Opens SignUpScreen
    onFacebookSignIn: () -> Unit,    // -> Opens SignUpScreen
    onAppleSignIn: () -> Unit,       // -> Opens SignUpScreen
    onPhoneSignIn: () -> Unit,       // -> Opens PhoneNumberScreen
    onSignInHere: () -> Unit         // -> Opens SignUpScreen
) {
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
            Spacer(modifier = Modifier.height(80.dp))

            // ALTEREGO Logo Image
            Image(
                painter = painterResource(id = R.drawable.logo_alterego_main),
                contentDescription = "ALTEREGO",
                modifier = Modifier
                    .width(180.dp)
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Sign in with Google Button -> Opens SignUpScreen
            SocialSignInButton(
                text = "Sign in with Google",
                iconResId = R.drawable.ic_google,
                onClick = onGoogleSignIn
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sign in with Facebook Button -> Opens SignUpScreen
            SocialSignInButton(
                text = "Sign in with Facebook",
                iconResId = R.drawable.ic_facebook,
                onClick = onFacebookSignIn
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sign in with Apple Button -> Opens SignUpScreen
            SocialSignInButton(
                text = "Sign in with Apple",
                iconResId = R.drawable.ic_apple,
                onClick = onAppleSignIn
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Divider with "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.4f),
                    thickness = 1.dp
                )
                Text(
                    text = "or",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.4f),
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue with phone number Button -> Opens PhoneNumberScreen
            Button(
                onClick = onPhoneSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Text(
                    text = "Continue with phone number",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Already have an account? Sign in here -> Opens SignUpScreen
            Row(
                modifier = Modifier.padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an\naccount?",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in here",
                    color = LinkBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onSignInHere() }
                )
            }
        }
    }
}

@Composable
fun SocialSignInButton(
    text: String,
    iconResId: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 24.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}