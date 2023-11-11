package com.example.demochatapplication.features.chat.domain.exceptions

/**
 * Created by Jatin Vashisht on 03-11-2023.
 */
data object InvalidMessageDeliveryStateStringException : Exception("this string cannot be converted into MessageDeliveryState")