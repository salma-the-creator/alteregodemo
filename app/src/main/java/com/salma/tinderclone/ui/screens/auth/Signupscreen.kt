package com.salma.tinderclone.ui.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import com.salma.tinderclone.R

// ✅ Google Web Client ID
private const val WEB_CLIENT_ID =
    "591274729680-fm8c8faqp6ddbu8c8klbn24r14slik9g.apps.googleusercontent.com"

// Colors
private val PinkLight = Color(0xFFFAD4ED)
private val PinkMedium = Color(0xFFF5A8D8)
private val PurpleLight = Color(0xFFE495CB)
private val PurpleMedium = Color(0xFFCF87C0)
private val PurpleDark = Color(0xFFB87BB5)
private val ButtonPurple = Color(0xFFB89AD4)
private val ButtonPurpleDisabled = Color(0xFFB89AD4)
private val LinkPink = Color(0xFFD64B8A)
private val LinkBlue = Color(0xFF5B9BD5)
private val TextGray = Color(0xFFAAAAAA)
private val CheckboxBorder = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onGoogleSignUp: () -> Unit,
    onSignUp: (email: String, password: String) -> Unit,
    onForgotPassword: () -> Unit = {},
    onSignInHere: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // -------------------------------------------------------
    // GOOGLE SIGN-IN CONFIG
    // -------------------------------------------------------
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }

    val googleClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken

                if (idToken != null) {
                    isLoading = true
                    val credential = GoogleAuthProvider.getCredential(idToken, null)

                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        isLoading = false
                        if (authTask.isSuccessful) {
                            Log.d("SignUpScreen", "Google sign-in success")
                            onGoogleSignUp()
                        } else {
                            Log.e("SignUpScreen", "Firebase auth failed", authTask.exception)
                        }
                    }
                }
            } catch (e: ApiException) {
                Log.e("SignUpScreen", "Google sign-in failed", e)
            }
        }
    }

    // -------------------------------------------------------
    // UI
    // -------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PinkLight, PinkMedium, PurpleLight, PurpleMedium, PurpleDark)
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
                    .padding(top = 24.dp)
                    .clickable { onBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back", color = Color.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_alterego_main),
                contentDescription = null,
                modifier = Modifier
                    .width(160.dp)
                    .height(110.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // -------------------------------------------------------
            // GOOGLE SIGN UP BUTTON
            // -------------------------------------------------------
            Button(
                onClick = {
                    googleClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleClient.signInIntent)
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Sign up with Google", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // EMAIL INPUT
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email address", color = TextGray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // PASSWORD INPUT
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter your password", color = TextGray) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextGray
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // FORGOT PASSWORD
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Forgot password?",
                    color = LinkPink,
                    modifier = Modifier.clickable { onForgotPassword() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TERMS CHECKBOX
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            1.5.dp,
                            if (agreeToTerms) ButtonPurple else CheckboxBorder,
                            RoundedCornerShape(4.dp)
                        )
                        .background(if (agreeToTerms) ButtonPurple else Color.Transparent)
                        .clickable { agreeToTerms = !agreeToTerms },
                    contentAlignment = Alignment.Center
                ) {
                    if (agreeToTerms) {
                        Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("I agree to the terms & conditions", color = Color.Black.copy(alpha = 0.8f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SIGN UP BUTTON
            Button(
                onClick = { onSignUp(email, password) },
                enabled = email.isNotBlank() && password.isNotBlank() && agreeToTerms,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPurple,
                    disabledContainerColor = ButtonPurpleDisabled.copy(alpha = 0.6f)
                )
            ) {
                Text("Sign up", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SIGN IN LINK
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Already have an\naccount?",
                    color = Color.Black.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Sign in here",
                    color = LinkBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onSignInHere() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
