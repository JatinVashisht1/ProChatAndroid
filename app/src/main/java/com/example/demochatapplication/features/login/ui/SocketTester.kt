package com.example.demochatapplication.features.login.ui
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.net.URISyntaxException

class SocketTester {
    companion object {
        const val TAG = "sockettester"
    }
    private lateinit var mSocket: Socket

    @Synchronized
    fun setSocket(url: String) {
        try {
            mSocket = IO.socket(url)
        } catch (e: URISyntaxException) {
            Timber.tag(TAG).d("uri syntax exception while connecting to server $e")
        } catch (e: Exception) {
            Timber.tag(TAG).d("exception while connecting to server $e")
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }

    val onConnect = Emitter.Listener {
        mSocket.emit("chat", JSONArray(JSONObject("success: true")))
    }
}