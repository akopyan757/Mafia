import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol
import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems.jar
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

ktor {
    docker {
        localImageName.set("mafia-docker-image")
        imageTag.set("0.0.1")
        jreVersion.set(JavaVersion.VERSION_17)
        portMappings.set(
            listOf(
                DockerPortMapping(outsideDocker = 80, insideDocker = 8080, DockerPortMappingProtocol.TCP)
            )
        )
    }

    fatJar {
        archiveFileName.set("fat.jar")
    }
}

group = "com.cheesecake.mafia"
version = "1.0.1"
application {
    mainClass.set("com.cheesecake.mafia.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content)
    implementation(libs.ktor.server.kotlinx)
    implementation(libs.ktor.server.netty)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

    implementation(libs.postgress)
    implementation(libs.hikari)

    implementation(libs.logback)
    testImplementation(libs.kotlin.test.junit)
}

tasks.create("stage") {
    dependsOn("installDist")
}