plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
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

    val room_version = "2.6.1" // Versión estable actual

    // Core de Room (obligatorio)
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Para corrutinas

    // Procesador de anotaciones (obligatorio)
    ksp("androidx.room:room-compiler:$room_version")

    // Para testing (opcional pero recomendado)
    testImplementation("androidx.room:room-testing:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    //ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")


    implementation("com.airbnb.android:lottie:6.0.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.11.0")

    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    //Dependencia que necesita el VisorActiitykt
    implementation ("com.google.guava:guava:30.1-android")
    // implementacion de Dagger Hilt para inyectar dependencias
    implementation("com.google.dagger:hilt-android:2.50")


    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")
    implementation("androidx.annotation:annotation:1.9.1")

    //Esta dependencia no incluye un labeler funcional. E sólo el nucleo comun, no incluye
    //ningun modelo de etiquetado real ni on device ni en la nube
    /*
    Por eso, cuando llamas a ImageLabeling.getClient(...), el sistema no encuentra un detector válido y lanza el NullPointerException.


    */

    //implementation("com.google.mlkit:image-labeling-default-common:17.0.0")
    implementation ("com.google.mlkit:image-labeling:17.0.7")

    //Dependencias para camerax
    val cameraxversion = "1.1.0-beta01"
    implementation("androidx.camera:camera-core:${cameraxversion}")
    implementation ("androidx.camera:camera-camera2:${cameraxversion}")
    implementation ("androidx.camera:camera-lifecycle:${cameraxversion}")
    implementation ("androidx.camera:camera-video:${cameraxversion}")

    implementation ("androidx.camera:camera-view:${cameraxversion}")
    implementation ("androidx.camera:camera-extensions:${cameraxversion}")


    kapt("com.google.dagger:hilt-android-compiler:2.50")

    //"ai.djl.opencv:opencv:0.30.0"
    implementation("org.opencv:opencv:4.9.0") // O la última versión disponible



    //implmenetacion de viewmodel y livedata

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")


    //implementacion para ViewPager, con este se hace el corusel

    //tambien nos sirve para mostrar las imagenes en el bottomsheet.
    implementation("androidx.viewpager2:viewpager2:1.1.0")



    //Dependencias para firebase
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.0")

    //Analitycs
    //implementation(platform("com.google.firebase:firebase-bom:33.12.0")
    //implementation("com.google.firebase:firebase-analytics")

    //Dependencia para manipulacion de la respuesta JSON que devuelve la API OpenAI
    implementation("com.google.code.gson:gson:2.11.0")

    //Dependencia para webrtc
    implementation("com.mesibo.api:webrtc:1.0.5")

    //Para el modulo Extensions.kt
    implementation("com.guolindev.permissionx:permissionx:1.8.1")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //photo view para manipulacion e interaccion de imagenes
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    //imagenes redondeadas
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //cargar imagenes
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    //para aplicar zoom a las imagenes
    val photo_view_version="2.3.0"
    implementation("com.github.chrisbanes:PhotoView:${photo_view_version}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
}