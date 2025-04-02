plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.datatree"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.datatree"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    viewBinding{
        enable = true
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }


}

dependencies {
    implementation("com.airbnb.android:lottie:6.0.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    // implementacion de Dagger Hilt para inyectar dependencias
    implementation("com.google.dagger:hilt-android:2.50")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

    //Dependencias para camerax
    val camerax_version = "1.1.0-beta01"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")

    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")


    kapt("com.google.dagger:hilt-android-compiler:2.50")

    //"ai.djl.opencv:opencv:0.30.0"
    implementation("org.opencv:opencv:4.9.0") // O la última versión disponible



    //implmenetacion de viewmodel y livedata

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")



    implementation("androidx.viewpager2:viewpager2:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
}