package com.example.screenlock

import android.accessibilityservice.AccessibilityService
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.screenlock.ui.theme.ScreenLockTheme

class MainActivity : ComponentActivity() {
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

    private fun requestAccessibilityService(context: Context) {
        // Open Accessibility Settings in the same task so back returns to this activity
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }

    private fun openAppInfo() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun devicePolicyManager(): DevicePolicyManager =
        getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private fun adminComponent(): ComponentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

    private fun isDeviceAdminActive(): Boolean = devicePolicyManager().isAdminActive(adminComponent())

    private fun requestDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent())
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.app_name))
        }
        startActivity(intent)
    }

    private fun lockPreferAdminAndFinish() {
        if (isDeviceAdminActive()) {
            devicePolicyManager().lockNow()
            finishAndRemoveTask()
        } else {
            // Fallback to accessibility broadcast if available
            val intent = Intent(ScreenLockAccessibilityService.ACTION_LOCK_SCREEN)
            intent.setPackage(packageName)
            sendBroadcast(intent)
            finishAndRemoveTask()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScreenLockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val lifecycleOwner = LocalLifecycleOwner.current
                    var isServiceEnabled by remember {
                        mutableStateOf(
                            isAccessibilityServiceEnabled(this@MainActivity, ScreenLockAccessibilityService::class.java)
                        )
                    }
                    var isAdminActive by remember { mutableStateOf(isDeviceAdminActive()) }

                    // Update the toggles when we return from settings (or app resumes)
                    DisposableEffect(lifecycleOwner) {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_RESUME) {
                                isServiceEnabled = isAccessibilityServiceEnabled(
                                    this@MainActivity,
                                    ScreenLockAccessibilityService::class.java
                                )
                                isAdminActive = isDeviceAdminActive()
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                    }

                    MainScreen(
                        isDeviceAdminActive = isAdminActive,
                        isAccessibilityEnabled = isServiceEnabled,
                        onEnableDeviceAdmin = { requestDeviceAdmin() },
                        onEnableAccessibility = { requestAccessibilityService(this@MainActivity) },
                        onOpenAppInfo = { openAppInfo() },
                        onLock = { lockPreferAdminAndFinish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    isDeviceAdminActive: Boolean,
    isAccessibilityEnabled: Boolean,
    onEnableDeviceAdmin: () -> Unit,
    onEnableAccessibility: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isDeviceAdminActive || isAccessibilityEnabled -> {
                Button(onClick = onLock) { Text("Lock Screen") }
                if (!isDeviceAdminActive) {
                    Text("Tip: Enable Device Admin for best compatibility")
                    Button(onClick = onEnableDeviceAdmin) { Text("Enable Device Admin") }
                }
            }
            else -> {
                Text("To lock the screen, enable one of the following:")
                Button(onClick = onEnableDeviceAdmin) { Text("Enable Device Admin") }
                Button(onClick = onEnableAccessibility) { Text("Enable Accessibility Service") }
                Text("If Accessibility shows 'Restricted setting', open App Info and allow restricted settings.")
                Button(onClick = onOpenAppInfo) { Text("Open App Info") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ScreenLockTheme {
        MainScreen(
            isDeviceAdminActive = false,
            isAccessibilityEnabled = false,
            onEnableDeviceAdmin = {},
            onEnableAccessibility = {},
            onOpenAppInfo = {},
            onLock = {}
        )
    }
}