import org.jetbrains.kotlin.config.JvmTarget

plugins {
    id("com.android.application")
    kotlin("kapt")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    namespace = "de.dertyp7214.rboardthemecreator"
    compileSdk = 35

    buildToolsVersion = "35.0.0"
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthemecreator"
        minSdk = 26
        targetSdk = 35
        versionCode = 131004
        versionName = "1.3.1"

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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_22
        targetCompatibility = JavaVersion.VERSION_22
    }

    kotlinOptions {
        jvmTarget = JvmTarget.JVM_22.toString()
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
            "-Xsuppress-version-warnings"
        )
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
}

dependencies {
    implementation(project(":colorutilsc"))
    implementation(project(":rboardcomponents"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.analytics.ktx)
    
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    implementation(libs.insetter)
    //noinspection DifferentStdlibGradleVersion
    implementation(libs.androidx.core)
    implementation(libs.prdownloader)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.webkit)
    implementation(libs.preferencesplus)
    implementation(libs.flagkit.android)
    implementation(libs.pikolo)
    implementation(libs.vectorchildfinder)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.commons.text)
    implementation(libs.core.ktx)
    //noinspection DifferentStdlibGradleVersion
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.android.shell)
    implementation(libs.gson)
    implementation(libs.simple.item.decoration)
    testImplementation(libs.junit)
    implementation(libs.glide)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    coreLibraryDesugaring(libs.desugar.jdk.libs.nio)

    ksp(libs.glide.ksp)
}
