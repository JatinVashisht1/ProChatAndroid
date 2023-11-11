package com.example.demochatapplication.core

sealed class Resource<T> {
    data class Success<T>(val result: T?): Resource<T>()

    data class Error<T>(val error: String): Resource<T>()

    class Loading<T>(): Resource<T>()

}
