package com.example.demochatapplication.features.shared.socket

/**
 * Created by Jatin Vashisht on 01-11-2023.
 */
sealed class SocketEvents (val eventName: String) {
    data object Connect: SocketEvents("connection")
    data object Chat: SocketEvents("chat")
    data object Disconnect: SocketEvents("disconnect")
    data object UpdateMessageDeliveryStatus: SocketEvents("updateMessageDeliveryStatus")
    data object UpdateAllMessagesDeliveryStatusBetween2Users: SocketEvents("updateAllMessageDeliveryStatus")
}
