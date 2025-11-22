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
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

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
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    private fun lockAndExit() {
        // Try both methods - let user decide which one to enable based on their preference
        // Accessibility Service: Allows biometric unlock but may not work on OnePlus
        // Device Admin: Works on OnePlus but requires PIN/Password unlock
        
        val accessibilityEnabled = isAccessibilityServiceEnabled(this, ScreenLockAccessibilityService::class.java)
        val adminEnabled = isDeviceAdminEnabled()
        
        when {
            accessibilityEnabled -> {
                // Prefer Accessibility Service if enabled (allows biometric unlock)
                val intent = Intent(ScreenLockAccessibilityService.ACTION_LOCK_SCREEN)
                intent.setPackage(packageName)
                sendBroadcast(intent)
                finishAndRemoveTask()
            }
            adminEnabled -> {
                // Fallback to Device Admin (works on OnePlus but requires PIN/Password)
                devicePolicyManager.lockNow()
                finishAndRemoveTask()
            }
            else -> {
                // Route to the UI that asks user to enable a method
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, ScreenLockDeviceAdminReceiver::class.java)

        if (isDeviceAdminEnabled() || isAccessibilityServiceEnabled(this, ScreenLockAccessibilityService::class.java)) {
            lockAndExit()
        } else {
            // Route to the UI that asks user to enable a method
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

