package com.example.screenlock

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.ContextCompat

class ScreenLockAccessibilityService : AccessibilityService() {
    companion object {
        const val ACTION_LOCK_SCREEN = "com.example.screenlock.ACTION_LOCK_SCREEN"
    }

    private val lockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_LOCK_SCREEN) {
                Log.d("ScreenLockService", "Received lock screen broadcast")
                performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val filter = IntentFilter(ACTION_LOCK_SCREEN)
        try {
            ContextCompat.registerReceiver(
                this,
                lockReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } catch (e: Exception) {
            Log.e("ScreenLockService", "Failed to register receiver", e)
        }
        Log.d("ScreenLockService", "Service connected and receiver registered")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No need to handle events
    }

    override fun onInterrupt() {
        // No need to handle interrupts
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(lockReceiver)
        } catch (_: IllegalArgumentException) {
            Log.w("ScreenLockService", "Receiver not registered or already unregistered")
        }
        Log.d("ScreenLockService", "Service destroyed and receiver unregistered")
        super.onDestroy()
    }
}
