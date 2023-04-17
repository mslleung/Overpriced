// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val hiltVersion by extra { "2.45" }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.9.0")
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
