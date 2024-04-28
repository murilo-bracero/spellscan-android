import com.google.protobuf.gradle.id

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.spellscanapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.spellscanapp"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["appAuthRedirectScheme"] = "com.example.spellscanapp"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "BACKEND_HOST", "\"backend.spellscan.com\"")
            buildConfigField("String", "OIDC_HOST", "\"idp.spellscan.com\"")
        }

        debug {
            buildConfigField("String", "BACKEND_HOST", "\"backend.dev.spellscan.com.br\"")
            buildConfigField("String", "OIDC_HOST", "\"idp.dev.spellscan.com.br\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    sourceSets["main"].java.srcDirs("build/generated/source/proto/main/grpc")
    sourceSets["main"].java.srcDirs("build/generated/source/proto/main/grpckt")
    sourceSets["main"].java.srcDirs("build/generated/source/proto/main/java")

    sourceSets["debug"].java.srcDirs("build/generated/source/proto/debug/grpc")
    sourceSets["debug"].java.srcDirs("build/generated/source/proto/debug/grpckt")
    sourceSets["debug"].java.srcDirs("build/generated/source/proto/debug/java")
}

val camerax_version = "1.3.0"
val cronet_version = "18.0.1"
val grpc_version = "1.60.0"
val protobuf_version = "3.25.1"
val grpc_kotlin_stub_version = "1.4.1"
val coroutines_version = "1.7.3"
val coil_version = "2.5.0"
val room_version = "2.6.1"
val appauth_version = "0.11.1"

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.mlkit:text-recognition:16.0.0")

    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")

    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // grpc
    implementation("io.grpc:grpc-protobuf-lite:${grpc_version}")
    implementation("io.grpc:grpc-stub:${grpc_version}")

    implementation("io.grpc:grpc-cronet:${grpc_version}")
    implementation("io.grpc:grpc-okhttp:${grpc_version}")
    implementation("com.google.android.gms:play-services-cronet:${cronet_version}")

    //grpc - transitive dependencies
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("org.apache.tomcat:annotations-api:6.0.53")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines_version}")

    implementation("io.coil-kt:coil:${coil_version}")

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // auth
    implementation("net.openid:appauth:$appauth_version")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${coroutines_version}")
    androidTestImplementation("org.mockito:mockito-android:2.7.15")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

protobuf {
    protoc{
        artifact = "com.google.protobuf:protoc:${protobuf_version}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpc_version}"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                id("java") {
                    option("lite")
                }
            }

            it.plugins {
                id("grpc") {
                    option("lite")
                }
            }
        }
    }
}