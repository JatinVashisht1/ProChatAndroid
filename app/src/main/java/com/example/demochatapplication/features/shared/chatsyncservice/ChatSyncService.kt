package com.example.demochatapplication.features.shared.chatsyncservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.shared.socket.SocketEvents
import com.example.demochatapplication.features.shared.socket.SocketManager
import com.example.demochatapplication.features.shared.socket.SocketManager.mSocket
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 01-11-2023.
 */

@AndroidEntryPoint()
class ChatSyncService: Service() {

    @Inject()
    lateinit var chatRepository: ChatRepository
    override fun onCreate() {
        super.onCreate()
        SocketManager.establishConnection()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mSocket?.on(SocketEvents.Chat.eventName) { dataArgs->
            val data = dataArgs[0]
            Timber.tag(TAG).d("data is $data")
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val TAG = "chatsyncservicetag"
    }
}