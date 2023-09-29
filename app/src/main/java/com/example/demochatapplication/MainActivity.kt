package com.example.demochatapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.dataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.features.login.ui.LoginScreenParent
import com.example.demochatapplication.features.login.ui.LoginScreenViewModel
import com.example.demochatapplication.features.login.ui.SocketTester
import com.example.demochatapplication.features.shared.cryptomanager.CryptoManager
import com.example.demochatapplication.features.shared.navigation.Destinations
import com.example.demochatapplication.features.shared.usersettings.UserSettingsSerializer
import com.example.demochatapplication.ui.theme.DemoChatApplicationTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var oneTapClient: SignInClient

    private val Context.datastore by dataStore(
        LoginScreenViewModel.USER_SETTINGS_FILE_NAME, UserSettingsSerializer(
            CryptoManager()
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        oneTapClient = Identity.getSignInClient(this)
        val socketTester = SocketTester();
        socketTester.setSocket(Constants.SERVER_URL)
        val socket = socketTester.getSocket()
        socket.connect()

        socket.on(Socket.EVENT_CONNECT, socketTester.onConnect);
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

                    }
                }
            }
        }
    }


    companion object {
        const val TAG = "mainactivitytag"
    }
}
