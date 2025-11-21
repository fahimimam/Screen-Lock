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
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.screenlock.ui.theme.ScreenLockTheme

class MainActivity : ComponentActivity() {
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

    private fun requestAccessibilityService(context: Context) {
        // Open Accessibility Settings in the same task so back returns to this activity
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }

    private fun requestDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Enable Device Admin to lock your screen. This is an alternative method that works on all devices including OnePlus."
        )
        startActivity(intent)
    }

    private fun sendLockRequestAndFinish() {
        if (isDeviceAdminEnabled()) {
            // Use Device Admin method (works on all devices including OnePlus)
            devicePolicyManager.lockNow()
            finishAndRemoveTask()
        } else if (isAccessibilityServiceEnabled(this, ScreenLockAccessibilityService::class.java)) {
            // Fallback to Accessibility Service method
            val intent = Intent(ScreenLockAccessibilityService.ACTION_LOCK_SCREEN)
            intent.setPackage(packageName)
            sendBroadcast(intent)
            finishAndRemoveTask()
        } else {
            // No method available
            finishAndRemoveTask()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, ScreenLockDeviceAdminReceiver::class.java)

        setContent {
            ScreenLockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val lifecycleOwner = LocalLifecycleOwner.current
                    var isServiceEnabled by remember {
                        mutableStateOf(
                            isAccessibilityServiceEnabled(this@MainActivity, ScreenLockAccessibilityService::class.java)
                        )
                    }
                    var isAdminEnabled by remember {
                        mutableStateOf(isDeviceAdminEnabled())
                    }

                    // Update the toggle when we return from settings (or app resumes)
                    DisposableEffect(lifecycleOwner) {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_RESUME) {
                                isServiceEnabled = isAccessibilityServiceEnabled(
                                    this@MainActivity,
                                    ScreenLockAccessibilityService::class.java
                                )
                                isAdminEnabled = isDeviceAdminEnabled()
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                    }

                    MainScreen(
                        isServiceEnabled = isServiceEnabled,
                        isAdminEnabled = isAdminEnabled,
                        onEnableService = { requestAccessibilityService(this@MainActivity) },
                        onEnableAdmin = { requestDeviceAdmin() },
                        onLockScreen = { sendLockRequestAndFinish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    isServiceEnabled: Boolean,
    isAdminEnabled: Boolean,
    onEnableService: () -> Unit,
    onEnableAdmin: () -> Unit,
    onLockScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isAdminEnabled || isServiceEnabled) {
            Button(onClick = onLockScreen) {
                Text("Lock Screen")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isAdminEnabled) {
                    "✓ Device Admin enabled (Recommended for OnePlus devices)"
                } else {
                    "✓ Accessibility Service enabled"
                },
                textAlign = TextAlign.Center
            )
            
            if (!isAdminEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "OnePlus users: Enable Device Admin for better compatibility",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onEnableAdmin) {
                    Text("Enable Device Admin")
                }
            }
        } else {
            Text(
                text = "Enable at least one method to lock your screen:",
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Method 1 (Recommended for OnePlus):",
                textAlign = TextAlign.Center
            )
            Button(onClick = onEnableAdmin) {
                Text("Enable Device Admin")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Method 2 (Alternative):",
                textAlign = TextAlign.Center
            )
            Button(onClick = onEnableService) {
                Text("Enable Accessibility Service")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Note: OnePlus devices work better with Device Admin method.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ScreenLockTheme {
        MainScreen(
            isServiceEnabled = false,
            isAdminEnabled = false,
            onEnableService = {},
            onEnableAdmin = {},
            onLockScreen = {}
        )
    }
}