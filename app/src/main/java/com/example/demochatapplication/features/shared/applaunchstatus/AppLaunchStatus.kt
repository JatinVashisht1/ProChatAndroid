package com.example.demochatapplication.features.shared.applaunchstatus

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class AppLaunchStatus(
    val lastTimePermissionCheckedMillis: Long = System.currentTimeMillis()
)
