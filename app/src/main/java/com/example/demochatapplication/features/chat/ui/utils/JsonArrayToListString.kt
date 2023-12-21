package com.example.demochatapplication.features.chat.ui.utils

import org.json.JSONArray


fun jsonArrayToList(jsonArray: JSONArray): List<String> {
    val stringList = mutableListOf<String>()

    for (i in 0 until jsonArray.length()) {
        val element = jsonArray.getString(i)
        stringList.add(element)
    }

    return stringList
}