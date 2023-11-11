package com.example.demochatapplication.features.shared.socket
import com.example.demochatapplication.core.Constants
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.net.URISyntaxException

object SocketManager {
    const val TAG = "sockettester"

    var mSocket: Socket? = null

    @Synchronized
    fun setSocket(url: String, token: String) {
        try {
            mSocket = IO.socket(url, IO.Options.builder().setAuth(mapOf(Pair<String, String>("token", token))).build())
        } catch (e: URISyntaxException) {
            Timber.tag(TAG).d("uri syntax exception while connecting to server $e")
//            throw (e)
        } catch (e: Exception) {
            Timber.tag(TAG).d("exception while connecting to server $e")
//            throw(e)
        }
    }



    @Synchronized
    fun getSocket(): Socket? {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket?.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket?.disconnect()
    }

    val onConnect = Emitter.Listener {
        mSocket?.emit(SocketEvents.Chat.eventName, JSONArray(JSONObject("success: true")))
    }

    val onChat = Emitter.Listener {
        val data = it[0]
        Timber.tag(TAG).d("data is ${data}")
    }

//    val TAG = "socketmanager"
}