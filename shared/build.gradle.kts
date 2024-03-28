plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    kotlin("plugin.serialization").version("1.9.23")
}

repositories {
    google()
    mavenCentral()
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.cheesecake.mafia.database")
            generateAsync.set(true)
        }
    }
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm()

    targets.configureEach {
        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive.adapters)
            implementation(libs.ktor.client.core)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android)
            implementation(libs.ktor.client.android)
        }
        jvmMain.dependencies {
            implementation(libs.sqldelight.desktop)
            implementation(libs.ktor.client.apache5)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.ios)
            //implementation(libs.ktor.client.winhttp)
        }
    }
}

android {
    namespace = "com.cheesecake.mafia.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
dependencies {
    implementation(libs.androidx.media3.decoder)
}

task("testClasses")
