// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false


}

//para la implementacion de Dagger Hilt, nos permite la practica de inyectar dependencias automaticamente.

buildscript{
    dependencies{
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}