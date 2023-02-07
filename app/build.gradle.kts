@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

val kotlinVersion: String = KotlinCompilerVersion.VERSION

android {
    namespace = "de.dertyp7214.rboardthemecreator"

    compileSdk = 33

    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthemecreator"
        minSdk = 23
        targetSdk = 33
        versionCode = 108000
        versionName = "1.0.8"

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.toString()
    }
}

dependencies {
    implementation(project(":colorutilsc"))
    implementation(project(":rboardcomponents"))

    implementation(platform("com.google.firebase:firebase-bom:30.3.2"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.1")
    implementation("com.google.firebase:firebase-analytics-ktx:21.2.0")
    
    implementation("com.google.firebase:firebase-analytics:21.2.0")
    implementation("com.google.firebase:firebase-messaging:23.1.1")

    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    //noinspection DifferentStdlibGradleVersion
    implementation("androidx.core:core:1.9.0")
    implementation("de.dertyp7214:PRDownloader:v0.6.0")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.webkit:webkit:1.6.0")
    kapt("com.github.bumptech.glide:compiler:4.14.2")
    implementation("de.dertyp7214:PreferencesPlus:1.1")
    implementation("com.github.murgupluoglu:flagkit-android:1.0.2")
    implementation("com.github.madrapps:pikolo:2.0.2")
    implementation("com.github.devsideal:VectorChildFinder:1.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("androidx.core:core-ktx:1.9.0")
    //noinspection DifferentStdlibGradleVersion
    implementation("androidx.appcompat:appcompat:1.7.0-alpha01")
    implementation("com.google.android.material:material:1.9.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.activity:activity-ktx:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.0-alpha04")
    implementation("com.jaredrummler:android-shell:1.0.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.bignerdranch.android:simple-item-decoration:1.0.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
}