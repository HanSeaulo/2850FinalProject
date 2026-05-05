plugins {
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

val exposedVersion = "1.1.0"
val datetimeVersion = "1.0.0"

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.guava)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation("io.ktor:ktor-server-pebble-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-sessions-jvm:${libs.versions.ktor.get()}")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$datetimeVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")

    implementation("org.xerial:sqlite-jdbc:3.50.2.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.flightbooking.AppKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}