import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.smartlibrary"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.smartlibrary"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: "YOUR_GOOGLE_MAPS_API_KEY_HERE"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      viewBinding = true
    }
}

dependencies {
  // Classic XML & Material UI Dependencies
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.recyclerview)
  implementation(libs.androidx.cardview)
  // Firebase & Maps & Glide
  implementation(libs.firebase.auth)
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.database)
  implementation(libs.firebase.messaging)
  implementation(libs.play.services.maps)
  implementation(libs.play.services.location)
  implementation(libs.play.services.auth)
  implementation(libs.glide)
  implementation(libs.retrofit)
  implementation(libs.retrofit.gson)

  // QR Code Generation & Scanning (ZXing)
  implementation(libs.zxing.core)
  implementation(libs.zxing.android.embedded)


  // Core Android dependencies
  implementation(libs.androidx.core)

  // Local tests
  testImplementation(libs.junit)

  // Instrumented tests
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)
}

