import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    kotlin("native.cocoapods") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}

kotlin {

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvmToolchain(17)
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    applyDefaultHierarchyTemplate()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        //podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "MafiaApp"
            isStatic = true
        }
        //pod("youtube-ios-player-helper")
    }

    sourceSets {
        val desktopMain by getting

        iosMain.dependencies {}
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation("io.insert-koin:koin-android:3.5.3")
         }
        commonMain.dependencies {
            //implementation(project(":composeApp"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.decompose)
            implementation("com.arkivanov.decompose:extensions-compose-jetbrains:2.2.2-compose-experimental")
            implementation("dev.icerock.moko:mvvm-core:0.16.1")
            implementation("dev.icerock.moko:mvvm-flow:0.16.1")
            implementation("dev.icerock.moko:mvvm-compose:0.16.1")
            implementation("dev.icerock.moko:mvvm-flow-compose:0.16.1")
            implementation("dev.icerock.moko:mvvm-state:0.16.1")
            implementation("org.jetbrains.compose.material3:material3:1.6.1")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
            implementation(compose.components.resources)
            implementation(projects.shared)
            implementation(libs.kotlinx.serialization.json)
        }
        desktopMain.dependencies {
            implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
            implementation("io.insert-koin:koin-core-jvm:3.5.3")
            implementation("io.insert-koin:koin-compose-jvm:1.1.2")
            implementation(libs.koin.core.jvm)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.cheesecake.mafia"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/composeResources")

    defaultConfig {
        applicationId = "com.cheesecake.mafia.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.cheesecake.mafia"
            packageVersion = "1.0.1"
        }
    }
}

compose.experimental {
    web.application {}
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.core.i18n)
}
task("testClasses")