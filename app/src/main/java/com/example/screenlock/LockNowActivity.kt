package com.example.screenlock

import android.accessibilityservice.AccessibilityService
import android.app.admin.DevicePolicyManager
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

    private fun isDeviceAdminEnabled(): Boolean {
        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(this, ScreenLockDeviceAdminReceiver::class.java)
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    private fun lockAndExit() {
        // Prefer Device Admin method (works on all devices including OnePlus)
        if (isDeviceAdminEnabled()) {
            val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            devicePolicyManager.lockNow()
            finishAndRemoveTask()
        } else if (isAccessibilityServiceEnabled(this, ScreenLockAccessibilityService::class.java)) {
            // Fallback to Accessibility Service method
            val intent = Intent(ScreenLockAccessibilityService.ACTION_LOCK_SCREEN)
            intent.setPackage(packageName)
            sendBroadcast(intent)
            finishAndRemoveTask()
        } else {
            // Route to the UI that asks user to enable a method
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isDeviceAdminEnabled() || isAccessibilityServiceEnabled(this, ScreenLockAccessibilityService::class.java)) {
            lockAndExit()
        } else {
            // Route to the UI that asks user to enable a method
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

