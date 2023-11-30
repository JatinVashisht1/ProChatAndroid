package com.example.demochatapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.features.accounts.ui.AccountsScreenParent
import com.example.demochatapplication.features.chat.ui.ChatScreen
import com.example.demochatapplication.features.destinationswitcher.ui.DestinationSwitcherScreen
import com.example.demochatapplication.features.login.ui.LoginScreenParent
import com.example.demochatapplication.features.shared.socket.SocketManager
import com.example.demochatapplication.core.navigation.Destinations
import com.example.demochatapplication.core.navigation.NavArgsKeys
import com.example.demochatapplication.features.searchuseraccounts.ui.SearchUserScreenParent
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import com.example.demochatapplication.theme.DemoChatApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userSettingsRepository: UserSettingsRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            SocketManager.setSocket(url = Constants.SERVER_URL, userSettingsRepository.getFirstEntry().token)
            SocketManager.establishConnection()
//            SocketManager.mSocket?.on("chat") {
//                val data = it[0]
//                Timber.tag(TAG).d("data returned is $data")
//            }
        }
//        startService(Intent(this, ChatSyncService::class.java))

//        mSocket.on(Socket.EVENT_CONNECT, socketTester.onConnect);

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

                        composable(
                            route = Destinations.ChatScreen.route + "/{${NavArgsKeys.ANOTHER_USERNAME}}",
                            arguments = listOf(navArgument(NavArgsKeys.ANOTHER_USERNAME) {
                                type = NavType.StringType
                            })
                        ) {
                            ChatScreen()
                        }

                        composable(Destinations.AccountsScreen.route) {
                            AccountsScreenParent(
                                navHostController = navController
                            )
                        }

                        composable(Destinations.SearchUserScreen.route) {
                            SearchUserScreenParent(navHostController = navController)
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
