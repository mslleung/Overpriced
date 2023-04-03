import com.google.protobuf.gradle.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 33
//    compileSdkPreview = "UpsideDownCake"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    namespace = "com.igrocery.overpriced.infrastructure"
    protobuf {
        generatedFilesBaseDir = "$projectDir/build/generated/source/proto"
        protoc {
            artifact = "com.google.protobuf:protoc:4.0.0-rc-2"
        }
        generateProtoTasks {
            all().forEach { task ->
                task.plugins{
                    create("java") {
                        option("lite")
                    }
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // hilt dependency injection
    val hiltVersion: String by rootProject.extra
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("androidx.core:core-ktx:1.9.0")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // room
    val roomVersion = "2.5.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:2.5.1")

    // paging
    val pagingVersion = "3.1.1"
    implementation("androidx.paging:paging-runtime:$pagingVersion")

    // datastore
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("com.google.protobuf:protobuf-javalite:3.20.1")

    // date
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")

    implementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("junit:junit:4.13.2")

    val kotestVersion = "5.5.5"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    androidTestImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
}
