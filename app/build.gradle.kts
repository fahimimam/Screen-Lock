plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.screenlock"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.screenlock"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Only package English resources to cut size (adjust if you need more)
        resourceConfigurations += listOf("en")

        // With minSdk 30 we don't need vector compat PNGs
        // vectorDrawables.useSupportLibrary = false // default
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Enable R8 full mode for better optimization
            isDebuggable = false
        }
    }
    
    // Split APKs by ABI to reduce size for each device
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false  // Don't build universal APK with all ABIs
        }
    }

    // Exclude common license and metadata files
    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1,LICENSE*,NOTICE*,DEPENDENCIES}",
                "/META-INF/*.kotlin_module",
                "/kotlin/**",
                "DebugProbesKt.bin",
                "/META-INF/*.version"
            )
        }
        // Use legacy packaging for dex to optimize size
        dex {
            useLegacyPackaging = false
        }
    }
    
    // Optimize build configuration
    bundle {
        language {
            // Only include English resources in bundles
            enableSplit = true
        }
        density {
            // Enable density splits for smaller downloads
            enableSplit = true
        }
        abi {
            // Enable ABI splits for smaller downloads
            enableSplit = true
        }
    }



    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = false
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    // Preview annotation needed at compile time only
    compileOnly(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}