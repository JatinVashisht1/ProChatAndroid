package com.example.demochatapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.features.login.ui.LoginScreenParent
import com.example.demochatapplication.features.login.ui.REQ_ONE_TAP
import com.example.demochatapplication.features.login.ui.SocketTester
import com.example.demochatapplication.ui.theme.DemoChatApplicationTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var oneTapClient: SignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oneTapClient = Identity.getSignInClient(this)
        val socketTester = SocketTester();
        socketTester.setSocket(Constants.SERVER_URL)
        val socket = socketTester.getSocket()
        socket.connect()

        socket.on(Socket.EVENT_CONNECT, socketTester.onConnect);

        setContent {
            DemoChatApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreenParent()
                }
            }
        }
    }



    companion object {
        const val TAG = "mainactivitytag"
    }
}
