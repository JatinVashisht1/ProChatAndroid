package com.example.demochatapplication.features.shared.chatsyncservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.shared.socket.SocketEvents
import com.example.demochatapplication.features.shared.socket.SocketManager
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 01-11-2023.
 */

@AndroidEntryPoint()
class ChatSyncService: Service() {

    @Inject()
    lateinit var userSettingsRepository: UserSettingsRepository
    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("inside on create")
        CoroutineScope(IO).launch {
            SocketManager.setSocket(url = Constants.SERVER_URL, userSettingsRepository.getFirstEntry().token)
            SocketManager.establishConnection()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SocketManager.mSocket?.let {
            Timber.tag(TAG).d("inside onstart command")
        }

        val emitter = Emitter.Listener {dataArgs->
            val data = dataArgs[0]
            Timber.tag(TAG).d("data is $data")
        }

        SocketManager.mSocket?.on(SocketEvents.Chat.eventName, emitter)

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val TAG = "chatsyncservicetag"
    }
}