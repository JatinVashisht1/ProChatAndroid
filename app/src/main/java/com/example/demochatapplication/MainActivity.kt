package com.example.demochatapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
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
import com.example.demochatapplication.features.authentication.ui.login.LoginScreenParent
import com.example.demochatapplication.features.shared.socket.SocketManager
import com.example.demochatapplication.core.navigation.Destinations
import com.example.demochatapplication.core.navigation.NavArgsKeys
import com.example.demochatapplication.features.authentication.ui.signup.SignUpScreenParent
import com.example.demochatapplication.features.searchuseraccounts.ui.SearchUserScreenParent
import com.example.demochatapplication.features.shared.chatsyncservice.ChatSyncService
import com.example.demochatapplication.features.shared.internetconnectivity.NetworkConnectionManager
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import com.example.demochatapplication.theme.DemoChatApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userSettingsRepository: UserSettingsRepository


    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatSyncServiceIntent = Intent(this@MainActivity, ChatSyncService::class.java)
        startService(chatSyncServiceIntent)

        createNotificationChannel()

        lifecycleScope.launch {
            SocketManager.setSocket(url = Constants.SERVER_URL, userSettingsRepository.getFirstEntry().token)
            SocketManager.establishConnection()
        }

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
                            DestinationSwitcherScreen(navController = navController, context = this@MainActivity)
                        }

                        composable(Destinations.LoginScreen.route) {
                            LoginScreenParent(navHostController = navController)
                        }
                        
                        composable(Destinations.SignUpScreen.route) {
                            SignUpScreenParent(navHostController = navController)
                        }

                        composable(
                            route = Destinations.ChatScreen.route + "/{${NavArgsKeys.ANOTHER_USERNAME}}",
                            arguments = listOf(navArgument(NavArgsKeys.ANOTHER_USERNAME) {
                                type = NavType.StringType
                            }),

                        ) {
                            ChatScreen(navController = navController)
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

    override fun onStart() {
        super.onStart()
        networkConnectionManager.startMonitoring()
    }

    override fun onStop() {
        super.onStop()
        networkConnectionManager.stopMonitoring()
    }

    private fun createNotificationChannel () {
        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            description = CHANNEL_DESCRIPTION
        }

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val TAG = "mainactivitytag"
        const val CHANNEL_NAME = "firebasenotificationchannel"
        const val CHANNEL_DESCRIPTION = "show new message notification"
        const val CHANNEL_ID = "firebasenotificationchannel"
    }
}
