plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")  // Firebase plugin
}
//android {
//    defaultConfig {
//        minSdkVersion 26
//    }
//}
android {
    namespace = "com.example.rvnow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rvnow"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    }
}

dependencies {

    // Firebase SDK dependencies (with Firebase BOM)
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))  // Firebase BOM to manage versions
    implementation("com.google.firebase:firebase-auth:22.1.2")  // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore:24.9.1")  // Firestore
    implementation("com.google.firebase:firebase-analytics")  // Firebase Analytics
    implementation("com.google.android.gms:play-services-auth:20.7.0")  // Google Auth
    implementation("com.google.firebase:firebase-auth:22.1.2")

    // Jetpack Compose and Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("androidx.compose.material:material-icons-extended:1.0.0")

    // Compose UI & Material3
    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.material3:material3:1.0.0")

    // AndroidX Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)

    // Testing Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

dependencies {
    implementation ("androidx.compose.ui:ui:1.1.0")
    implementation ("androidx.compose.material3:material3:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    implementation ("com.google.firebase:firebase-auth-ktx:21.0.3")
    implementation ("com.google.firebase:firebase-bom:30.0.1")
    implementation ("androidx.compose.ui:ui-text:1.4.0")
    implementation ("androidx.compose.ui:ui:1.4.0")
    implementation ("androidx.compose.material3:material3:1.1.0")
    implementation ("androidx.compose.ui:ui:1.4.0")
    implementation ("androidx.compose.material3:material3:1.1.0")
//    implementation ("io.coil-kt:coil-compose:2.2.2")
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")


}
