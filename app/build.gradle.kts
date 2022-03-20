import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

val kotlinVersion: String = KotlinCompilerVersion.VERSION

android {
    compileSdkPreview = "Tiramisu"
    buildToolsVersion = "33.0.0 rc2"

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthemecreator"
        minSdk = 23
        targetSdk = 32
        versionCode = 102001
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:29.2.0"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.0.2")
    implementation("com.google.firebase:firebase-analytics-ktx:20.1.1")
    
    implementation("com.google.firebase:firebase-analytics:20.1.1")
    implementation("com.google.firebase:firebase-messaging:23.0.2")

    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    //noinspection DifferentStdlibGradleVersion
    implementation("androidx.core:core:1.9.0-alpha01")
    implementation("de.dertyp7214:PRDownloader:v0.6.0")
    implementation("androidx.browser:browser:1.4.0")
    kapt("com.github.bumptech.glide:compiler:4.13.1")
    implementation("de.dertyp7214:PreferencesPlus:1.1")
    implementation("com.github.murgupluoglu:flagkit-android:1.0.2")
    implementation("com.github.madrapps:pikolo:2.0.2")
    implementation("com.github.devsideal:VectorChildFinder:1.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.4-alpha04")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha04")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0-alpha03")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0-alpha03")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("androidx.core:core-ktx:1.9.0-alpha01")
    //noinspection DifferentStdlibGradleVersion
    implementation("androidx.appcompat:appcompat:1.6.0-alpha01")
    implementation("com.google.android.material:material:1.6.0-alpha03")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.activity:activity-ktx:1.5.0-alpha03")
    implementation("androidx.fragment:fragment-ktx:1.5.0-alpha03")
    implementation("com.jaredrummler:android-shell:1.0.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.bignerdranch.android:simple-item-decoration:1.0.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.github.bumptech.glide:glide:4.13.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20-RC")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.20-RC")

    debugImplementation("androidx.compose.ui:ui-tooling:1.2.0-alpha05")
}