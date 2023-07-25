package com.example.demochatapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.features.login.ui.LoginScreenParent
import com.example.demochatapplication.features.login.ui.SocketTester
import com.example.demochatapplication.ui.theme.DemoChatApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
}
