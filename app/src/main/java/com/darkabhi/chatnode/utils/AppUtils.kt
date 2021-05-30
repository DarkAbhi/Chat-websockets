package com.darkabhi.chatnode.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Created by Abhishek AN <abhishek@iku.earth> on 5/30/2021.
 */
object AppUtils {
    fun Context.checkStoragePermission(): Boolean {
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
}