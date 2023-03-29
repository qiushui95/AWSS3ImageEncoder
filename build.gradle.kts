plugins {
    kotlin("jvm") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.0")

    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.1")

    implementation("software.amazon.awssdk:s3:2.19.31")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}