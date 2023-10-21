package com.example.demochatapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demochatapplication.features.chat.ui.ChatScreen
import com.example.demochatapplication.features.destinationswitcher.ui.DestinationSwitcherScreen
import com.example.demochatapplication.features.login.ui.LoginScreenParent
import com.example.demochatapplication.features.shared.SocketTester
import com.example.demochatapplication.features.shared.navigation.Destinations
import com.example.demochatapplication.ui.theme.DemoChatApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val socketTester = SocketTester
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        socket.on(Socket.EVENT_CONNECT, socketTester.onConnect);

        setContent {
            val navController = rememberNavController()
            DemoChatApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Destinations.DestinationSwitcher.route
                    ) {
                        composable(Destinations.DestinationSwitcher.route) {
                            DestinationSwitcherScreen(navController = navController)
                        }



                        composable(Destinations.LoginScreen.route) {
                            LoginScreenParent(navHostController = navController)
                        }

                        composable(Destinations.ChatScreen.route) {
                            ChatScreen()
                        }
                    }
                }
            }
        }
    }


    companion object {
        const val TAG = "mainactivitytag"
    }
}
