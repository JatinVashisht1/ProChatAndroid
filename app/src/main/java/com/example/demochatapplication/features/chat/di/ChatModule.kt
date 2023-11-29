package com.example.demochatapplication.features.chat.di

import android.app.Application
import androidx.room.Room
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.features.chat.data.database.ChatDatabase
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.data.mapper.ChatDbEntityAndModelMapper
import com.example.demochatapplication.features.chat.data.mapper.MessageDeliveryStateAndStringMapper
import com.example.demochatapplication.features.chat.data.repository.ChatRepositoryImpl
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatsModule {
    @Provides
    @Singleton
    fun providesChatDatabase(app: Application): ChatDatabase =
        Room
            .databaseBuilder(app, ChatDatabase::class.java, ChatDatabase.CHAT_DB_NAME)
            .build()

    @Provides
    @Singleton
    fun providesChatDbEntityAndModelMapper(): Mapper<ChatDbEntity, ChatScreenUiModel.ChatModel> =
        ChatDbEntityAndModelMapper()

    @Provides
    @Singleton
    fun providesMessageDeliveryStateAndStringMapper(): Mapper<MessageDeliveryState, String> =
        MessageDeliveryStateAndStringMapper()

    @Provides
    @Singleton
    fun providesChatRepository(
        chatApi: ChatApi,
        chatDatabase: ChatDatabase,
        userSettingsRepository: UserSettingsRepository,
        chatDbEntityAndModelMapper: Mapper<ChatDbEntity, ChatScreenUiModel.ChatModel>,
        messageDeliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>,
        chatMessageDtoAndDbEntityMapper: Mapper<GetChatMessagesBetween2UsersDto, List<ChatDbEntity>>,
    ): ChatRepository = ChatRepositoryImpl(
        chatApi = chatApi,
        chatDatabase = chatDatabase,
        userSettingsRepository = userSettingsRepository,
        chatDbEntityAndModelMapper = chatDbEntityAndModelMapper,
        messageDeliveryStateAndStringMapper = messageDeliveryStateAndStringMapper,
        chatMessageDtoAndDbEntityMapper = chatMessageDtoAndDbEntityMapper,
    )
}
