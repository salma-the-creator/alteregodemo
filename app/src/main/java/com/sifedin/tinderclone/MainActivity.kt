package com.sifedin.tinderclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sifedin.tinderclone.navigation.AppNavGraph
import com.sifedin.tinderclone.navigation.Routes
import com.sifedin.tinderclone.ui.theme.HolaDatingAppTheme
import com.sifedin.tinderclone.viewmodel.AuthViewModel
import com.sifedin.tinderclone.ui.components.BottomNavBar

class MainActivity : ComponentActivity() {
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HolaDatingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot(vm)
                }
            }
        }
    }
}

@Composable
fun AppRoot(vm: AuthViewModel) {
    val nav = rememberNavController()
    val navBackStackEntry = nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Routes.HOME,
        Routes.LIKES,
        Routes.CHATS,
        Routes.SETTINGS
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = nav)
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            nav = nav,
            vm = vm,
            modifier = Modifier.padding(paddingValues)
        )
    }
}