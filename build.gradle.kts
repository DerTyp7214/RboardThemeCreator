@file:Suppress("UNUSED_VARIABLE")
plugins {
    alias(libs.plugins.ksp) apply false
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.fabric.io/public")
        maven("https://maven.google.com")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven ("https://storage.googleapis.com/r8-releases/raw")
    }
    dependencies {
        classpath(libs.gradle)
        classpath(libs.r8)
        //noinspection DifferentKotlinGradleVersion
        classpath(kotlin("gradle-plugin", version = "2.1.10"))
        classpath(libs.kotlin.gradle.plugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.fabric.io/public")
        maven("https://maven.google.com")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}