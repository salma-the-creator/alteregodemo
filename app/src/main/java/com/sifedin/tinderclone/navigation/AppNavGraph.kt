package com.sifedin.tinderclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel

import com.sifedin.tinderclone.viewmodel.AuthViewModel
import com.sifedin.tinderclone.viewmodel.SwipeViewModel
import com.sifedin.tinderclone.viewmodel.ChatViewModel
import com.sifedin.tinderclone.viewmodel.LikesViewModel
import com.sifedin.tinderclone.viewmodel.SettingsViewModel

import com.sifedin.tinderclone.ui.screens.auth.PhoneNumberScreen
import com.sifedin.tinderclone.ui.screens.auth.VerifyCodeScreen
import com.sifedin.tinderclone.ui.screens.auth.SignInScreen
import com.sifedin.tinderclone.ui.screens.auth.SignUpScreen

import com.sifedin.tinderclone.ui.screens.profile.IdentifyYourselfScreen
import com.sifedin.tinderclone.ui.screens.profile.InterestedScreen
import com.sifedin.tinderclone.ui.screens.profile.AddPhotosScreen

import com.sifedin.tinderclone.ui.screens.swipe.SwipeScreen
import com.sifedin.tinderclone.ui.screens.match.MatchScreen

import com.sifedin.tinderclone.ui.screens.chat.ChatsScreen
import com.sifedin.tinderclone.ui.screens.chat.ChatScreen

import com.sifedin.tinderclone.ui.screens.likes.LikesScreen

import com.sifedin.tinderclone.ui.screens.settings.SettingsScreen
import com.sifedin.tinderclone.ui.screens.settings.EditProfileScreen
import com.sifedin.tinderclone.ui.screens.settings.DeleteAccountScreen

object Routes {
    const val SIGNIN = "signin"
    const val SIGNUP = "signup"     // ← NEW
    const val PHONE = "phone"
    const val VERIFY = "verify"
    const val IDENTIFY = "identify"
    const val INTEREST = "interest"
    const val PHOTOS = "photos"
    const val HOME = "swipe"
    const val MATCH = "match_found"
    const val CHATS = "chats_list"
    const val CHAT_DETAIL = "chat_detail"
    const val LIKES = "likes_placeholder"
    const val SETTINGS = "settings_menu"
    const val EDIT_PROFILE = "edit_profile"
    const val DELETE_ACCOUNT = "delete_account"
}

@Composable
fun AppNavGraph(
    nav: NavHostController,
    vm: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val chatVM: ChatViewModel = viewModel()
    val likesVM: LikesViewModel = viewModel()
    val settingsVM: SettingsViewModel = viewModel()

    NavHost(
        navController = nav,
        startDestination = Routes.SIGNIN,      // ← FIRST SCREEN
        modifier = modifier
    ) {

        // ----------------------------------------------------
        // SIGN IN SCREEN
        // ----------------------------------------------------
        composable(Routes.SIGNIN) {
            SignInScreen(
                onGoogleSignIn = {
                    nav.navigate(Routes.SIGNUP)      // ← Google → SignUpScreen
                },
                onFacebookSignIn = { nav.navigate(Routes.SIGNUP) },
                onAppleSignIn = { nav.navigate(Routes.SIGNUP) },
                onPhoneSignIn = { nav.navigate(Routes.PHONE) },   // ← Continue with phone
                onSignInHere = { /* already here */ }
            )
        }

        // ----------------------------------------------------
        // SIGN UP SCREEN
        // ----------------------------------------------------
        composable(Routes.SIGNUP) {
            SignUpScreen(
                onGoogleSignUp = { /* TODO */ },
                onSignUp = { email, pass -> /* TODO */ },
                onForgotPassword = { /* TODO */ },
                onSignInHere = { nav.navigate(Routes.SIGNIN) },
                onBack = { nav.popBackStack() }
            )
        }

        // ----------------------------------------------------
        // PHONE AUTH FLOW
        // ----------------------------------------------------
        composable(Routes.PHONE) {
            PhoneNumberScreen(vm) {
                nav.navigate(Routes.VERIFY)
            }
        }

        composable(Routes.VERIFY) {
            VerifyCodeScreen(vm) {
                nav.navigate(Routes.IDENTIFY) {
                    popUpTo(Routes.SIGNIN) { inclusive = false }
                }
            }
        }

        // ----------------------------------------------------
        // PROFILE SETUP
        // ----------------------------------------------------
        composable(Routes.IDENTIFY) {
            IdentifyYourselfScreen(vm) { nav.navigate(Routes.INTEREST) }
        }

        composable(Routes.INTEREST) {
            InterestedScreen(vm) { nav.navigate(Routes.PHOTOS) }
        }

        composable(Routes.PHOTOS) {
            AddPhotosScreen(vm) {
                nav.navigate(Routes.HOME) {
                    popUpTo(Routes.SIGNIN) { inclusive = true }
                }
            }
        }

        // ----------------------------------------------------
        // SWIPE HOME
        // ----------------------------------------------------
        composable(Routes.HOME) {
            val swipeVM: SwipeViewModel = viewModel()
            swipeVM.onMatchFound = { user, matchId ->
                nav.navigate("${Routes.MATCH}/${user.name}/$matchId")
            }
            SwipeScreen(viewModel = swipeVM)
        }

        // ----------------------------------------------------
        // MATCH SCREEN
        // ----------------------------------------------------
        composable(
            route = "${Routes.MATCH}/{matchName}/{matchId}",
            arguments = listOf(
                navArgument("matchName") { type = NavType.StringType },
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { entry ->
            val name = entry.arguments?.getString("matchName") ?: "Someone"
            val id = entry.arguments?.getString("matchId") ?: ""

            MatchScreen(
                matchName = name,
                matchId = id,
                onSendMessage = { mid ->
                    nav.popBackStack(Routes.HOME, false)
                    nav.navigate("${Routes.CHAT_DETAIL}/$mid")
                }
            ) {
                nav.popBackStack(Routes.HOME, false)
            }
        }

        // ----------------------------------------------------
        // CHATS
        // ----------------------------------------------------
        composable(Routes.CHATS) {
            ChatsScreen(viewModel = chatVM) { matchId, _ ->
                nav.navigate("${Routes.CHAT_DETAIL}/$matchId")
            }
        }

        composable(
            route = "${Routes.CHAT_DETAIL}/{matchId}",
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { entry ->
            val matchId = entry.arguments?.getString("matchId") ?: ""
            ChatScreen(
                matchId = matchId,
                viewModel = chatVM,
                onBack = { nav.popBackStack() }
            )
        }

        // ----------------------------------------------------
        // SETTINGS
        // ----------------------------------------------------
        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = settingsVM,
                onNavigateToEditProfile = { nav.navigate(Routes.EDIT_PROFILE) },
                onNavigateToDeleteAccount = { nav.navigate(Routes.DELETE_ACCOUNT) },
                onLogout = {
                    nav.navigate(Routes.SIGNIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                viewModel = settingsVM,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.DELETE_ACCOUNT) {
            DeleteAccountScreen(
                viewModel = settingsVM,
                onBack = { nav.popBackStack() },
                onAccountDeleted = {
                    nav.navigate(Routes.SIGNIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
