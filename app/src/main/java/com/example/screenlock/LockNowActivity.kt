package com.example.screenlock

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity

class LockNowActivity : ComponentActivity() {

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
        val expectedComponentName = ComponentName(context, service)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        for (serviceName in colonSplitter) {
            if (serviceName.equals(expectedComponentName.flattenToString(), ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun lockAndExit() {
        val intent = Intent(ScreenLockAccessibilityService.ACTION_LOCK_SCREEN)
        intent.setPackage(packageName)
        sendBroadcast(intent)
        finishAndRemoveTask()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isAccessibilityServiceEnabled(this, ScreenLockAccessibilityService::class.java)) {
            lockAndExit()
        } else {
            // Route to the UI that asks user to enable the service
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

