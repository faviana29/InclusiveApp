// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

}
// build.gradle (Nivel de Proyecto)
// build.gradle.kts (Nivel de Proyecto)

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1") // Clase Gradle para Android
        classpath("com.google.gms:google-services:4.3.14") // Clase Gradle para Firebase
    }
}

