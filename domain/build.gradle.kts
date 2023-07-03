plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.parcelize")
}

android {
    compileSdk = 34
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
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
    kotlinOptions {
        jvmTarget = "17"
    }
    namespace = "com.igrocery.overpriced.domain"
}

dependencies {
    implementation(project(":shared"))

    implementation("androidx.core:core-ktx:1.10.1")

    testImplementation("junit:junit:4.13.2")
}
