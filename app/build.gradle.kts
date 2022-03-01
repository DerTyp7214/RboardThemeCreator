import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

val kotlinVersion: String = KotlinCompilerVersion.VERSION

android {
    compileSdkPreview = "Tiramisu"
    buildToolsVersion = "33.0.0 rc1"

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthemecreator"
        minSdk = 23
        targetSdk = 32
        versionCode = 101003
        versionName = "1.0.1"

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
    }
}

dependencies {

    implementation("com.github.madrapps:pikolo:2.0.2")
    implementation("com.github.devsideal:VectorChildFinder:1.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4-alpha04")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha04")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0-alpha02")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0-alpha02")
    implementation("org.apache.commons:commons-text:1.9")

    implementation("androidx.core:core-ktx:1.7.0")
    //noinspection DifferentStdlibGradleVersion
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.6.0-alpha02")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.5.0-alpha02")
    implementation("com.jaredrummler:android-shell:1.0.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.bignerdranch.android:simple-item-decoration:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4-alpha04")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha04")
    implementation("com.github.bumptech.glide:glide:4.13.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10")

    debugImplementation("androidx.compose.ui:ui-tooling:1.2.0-alpha03")
}