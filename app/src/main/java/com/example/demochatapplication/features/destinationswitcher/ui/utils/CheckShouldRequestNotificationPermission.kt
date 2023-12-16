package com.example.demochatapplication.features.destinationswitcher.ui.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.demochatapplication.features.shared.applaunchstatus.AppLaunchStatus

fun checkShouldRequestNotificationPermission(
    appLaunchStatus: AppLaunchStatus,
    context: Context,
): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return false

    val lastTimePermissionCheck = appLaunchStatus.lastTimePermissionCheckedMillis
    val currentTimeMillis = System.currentTimeMillis()
    val millisDifferencePermissionChecked =
        currentTimeMillis.minus(lastTimePermissionCheck).toDouble()
    val daysDifferencePermissionChecked =
        /*                                         seconds       minutes        hours        days  */
        (millisDifferencePermissionChecked.div(1000).div(60).div(60).div(24)).toInt()

    return daysDifferencePermissionChecked >= 3
}