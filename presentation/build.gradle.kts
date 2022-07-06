plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(projectDir.path + "/release-keystore.jks")
            storePassword = "B}@T*?U?b-8q~^3}"
            keyPassword = "B}@T*?U?b-8q~^3}"
            keyAlias = "release"
        }
    }
    compileSdk = 33

    defaultConfig {
        applicationId = "com.igrocery.overpriced"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0.0"   // {major.feature.patches}, preferably major version should never change

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("release")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        // https://developer.android.com/jetpack/androidx/releases/compose-compiler
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "com.ireceipt.receiptscanner.presentation"
    bundle {
        language {
            // include all language files to allow the user to switch languages
            enableSplit = false
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    implementation(project(":application"))
    implementation(project(":domain"))
    implementation(project(":shared"))

    // android core UI
    val composeVersion = "1.3.0-alpha01"
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.7.0-alpha02")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material3:material3:1.0.0-alpha14")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0")

    // data store
    implementation("androidx.datastore:datastore:1.0.0")

    // navigation component
    val navigationVersion = "2.5.0"
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navigationVersion")
    implementation("androidx.navigation:navigation-compose:$navigationVersion")

    // paging
    val pagingVersion = "3.1.1"
    implementation("androidx.paging:paging-runtime:$pagingVersion")
    implementation("androidx.paging:paging-compose:1.0.0-alpha15")

    // app startup
    implementation("androidx.startup:startup-runtime:1.1.1")

    // hilt dependency injection
    val hiltVersion: String by rootProject.extra
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // accompanist
    val accompanistVersion = "0.24.13-rc"
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")

    // glide
    implementation("com.github.bumptech.glide:glide:4.13.2")
    kapt("com.github.bumptech.glide:compiler:4.13.2")
    implementation("com.github.skydoves:landscapist-glide:1.5.3")

    // google play services
    implementation("com.google.android.gms:play-services-location:20.0.0")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.maps.android:maps-compose:2.5.0")

    // MP Android Chart
//    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.0.2")

    // CameraX
    val cameraxVersion = "1.2.0-alpha03"
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")

    // junit
    testImplementation("junit:junit:4.13.2")

    // instrumented tests
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")

    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}

kapt {
    correctErrorTypes = true
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}
