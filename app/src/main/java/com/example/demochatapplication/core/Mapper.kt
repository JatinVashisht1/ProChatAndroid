package com.example.demochatapplication.core

interface Mapper<A, B> {
    fun mapAtoB(objectTypeA: A): B
    fun mapBtoA(objectTypeB: B): A
}