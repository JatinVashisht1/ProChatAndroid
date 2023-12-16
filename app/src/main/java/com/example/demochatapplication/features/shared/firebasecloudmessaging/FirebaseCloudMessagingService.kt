package com.example.demochatapplication.features.shared.firebasecloudmessaging

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.demochatapplication.MainActivity
import com.example.demochatapplication.R
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.UpdateFirebaseRegistrationTokenBody
import com.example.demochatapplication.features.chat.domain.model.ChatEventMessage
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseCloudMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var userSettingsRepository: UserSettingsRepository
    @Inject
    lateinit var chatApi: ChatApi
    @Inject
    lateinit var chatRepository: ChatRepository
    @Inject
    lateinit var messageDeliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(IO).launch {
            userSettingsRepository.writeFirebaseRegistrationToken(token)
            val userSettings = userSettingsRepository.getFirstEntry()
            chatApi.updateUserFirebaseRegistrationToken(
                userSettings.firebaseRegistrationToken,
                UpdateFirebaseRegistrationTokenBody(updatedFirebaseToken = token)
            )
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        try {
            val notificationMessage = message.notification
            val fullMessageJson = message.data["fullMessage"]
            CoroutineScope(IO).launch {
                fullMessageJson?.let {
                    val fullMessage = Json.decodeFromString<ChatEventMessage>(fullMessageJson)
                    Timber.tag(TAG).d("full message is $fullMessage and delivery state is ${fullMessage.deliveryStatus}")
                    val chatMessage = ChatScreenUiModel.ChatModel(
                        from = fullMessage.from,
                        message = fullMessage.message,
                        to = fullMessage.to,
                        timeInMillis = fullMessage.createdAt,
                        id = fullMessage.messageId,
                        deliveryState = messageDeliveryStateAndStringMapper.mapBtoA(fullMessage.deliveryStatus),
                        )
                    chatRepository.insertChatMessage(chatMessage)
                }
            }

            val notificationBuilder = NotificationCompat.Builder(
                this@FirebaseCloudMessagingService,
                MainActivity.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(notificationMessage?.title ?: "message")
                .setContentText(notificationMessage?.body ?: "empty message")
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this@FirebaseCloudMessagingService)) {
                if (ActivityCompat.checkSelfPermission(
                        this@FirebaseCloudMessagingService,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notify(NOTIFICATION_ID, notificationBuilder.build())
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).d("exception in on message received: $e")
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val TAG = "firebasecloudmessagingservice"
    }
}