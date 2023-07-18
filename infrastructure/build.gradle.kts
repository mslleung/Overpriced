plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf") version "0.9.3"
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 34
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
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
    namespace = "com.igrocery.overpriced.infrastructure"
    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.22.3"
        }
        generateProtoTasks {
            all().forEach { task ->
                task.builtins {
                    create("java") {
                        option("lite")
                    }
//                    create("kotlin") {
//                        option("lite")
//                    }
                }
            }
        }
    }
    sourceSets.getByName("test") {
        kotlin.srcDir("src/test/kotlin")
    }
    sourceSets.getByName("androidTest") {
        kotlin.srcDir("src/androidTest/kotlin")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":shared"))

    // kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")

    // hilt dependency injection
    implementation("com.google.dagger:hilt-android:2.47")
    implementation("androidx.core:core-ktx:1.10.1")
    kapt("com.google.dagger:hilt-android-compiler:2.47")

    // room
    val roomVersion = "2.5.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:2.5.2")

    // paging
    val pagingVersion = "3.1.1"
    implementation("androidx.paging:paging-runtime:$pagingVersion")

    // datastore
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("com.google.protobuf:protobuf-javalite:4.0.0-rc-2")
//    implementation("com.google.protobuf:protobuf-kotlin-lite:3.22.3")

    // date
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

    implementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("junit:junit:4.13.2")

    val kotestVersion = "5.6.2"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    androidTestImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
}
