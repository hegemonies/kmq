import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.21"

    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"

    id("org.graalvm.buildtools.native") version "0.9.20"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
    id("com.google.cloud.tools.jib") version "3.3.1"
}

group = "site.hegemonies"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

val kotlinVersion = "1.8.21"
val coroutineVersion = "1.7.0-RC"
val grpcVersion = "1.54.1"
val grpcKotlinVersion = "1.3.0"
val protobufUtilsVersion = "3.22.3"

repositories {
    google()
    mavenCentral()
}

dependencies {
    // proto contracts
    implementation(project(":proto-compiler"))

    // spring
//    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
//    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // kotlin
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutineVersion")

    // json
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // grpc
    implementation("io.grpc:grpc-netty:$grpcVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("net.devh:grpc-spring-boot-starter:2.14.0.RELEASE")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("com.google.protobuf:protobuf-java-util:$protobufUtilsVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufUtilsVersion")

    // logger
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // databases
//    runtimeOnly("com.h2database:h2")
//    runtimeOnly("io.r2dbc:r2dbc-h2")

    // tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")

    // utils
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.90.Final:osx-aarch_64")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
        useK2 = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "azul/zulu-openjdk:17-latest"
    }

    to {
        image = "hegemonies/kmq:$version"
    }
}

graalvmNative {
    binaries {
        all {
            resources.autodetect()
            imageName.set("app")
            buildArgs.add("--verbose")
            buildArgs.add("--add-opens=java.base/java.nio=ALL-UNNAMED")
            buildArgs.add("--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED")
            buildArgs.add("--add-opens=java.base/jdk.internal.ref=ALL-UNNAMED")
            buildArgs.add("--trace-class-initialization=ch.qos.logback.classic.Logger")
            buildArgs.add("--initialize-at-build-time=org.slf4j.LoggerFactory,ch.qos.logback")
            buildArgs.add("--initialize-at-run-time=io.netty'")
        }
    }
}

nativeBuild {
    buildArgs("-H:ReflectionConfigurationFiles=../../../src/main/resources/reflection-config.json")
}
