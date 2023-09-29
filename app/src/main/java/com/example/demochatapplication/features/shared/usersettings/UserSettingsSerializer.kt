package com.example.demochatapplication.features.shared.usersettings

import androidx.datastore.core.Serializer
import com.example.demochatapplication.features.shared.cryptomanager.CryptoManager
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


class UserSettingsSerializer @Inject constructor(
    private val cryptoManager: CryptoManager
) : Serializer<UserSettings> {
    override val defaultValue: UserSettings
        get() = UserSettings()

    override suspend fun readFrom(input: InputStream): UserSettings {
        val decryptedBytes = cryptoManager.decrypt(inputStream = input)
        return try {
            Json.decodeFromString(
                deserializer = UserSettings.serializer(),
                string = decryptedBytes.decodeToString()
            )
        } catch (serializationException: SerializationException) {
            serializationException.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        val inputBytes = Json.encodeToString(
            serializer = UserSettings.serializer(),
            value = t
        )

        cryptoManager.encrypt(
            bytes = inputBytes.encodeToByteArray(),
            outputStream = output,
        )
    }
}