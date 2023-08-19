import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2" apply false
    id("io.spring.dependency-management") version "1.1.2"
    id("java")
    `maven-publish`

    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    id("com.google.protobuf") version "0.9.4"

    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
}

val grpcVersion = "1.57.2"
val protobufUtilsVersion = "3.24.0"
val grpcKotlinVersion = "1.3.0"

group = "site.hegemonies"
version = "0.0.2"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    google()
    mavenCentral()
}

extra["coroutinesVersion"] = "1.7.3"

dependencyManagement {
    imports {
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${property("coroutinesVersion")}")
    }
}

dependencies {
    // proto contracts
    implementation(project(":proto-compiler"))

    // Base
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    // utils
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.90.Final:osx-aarch_64")
    implementation(kotlin("stdlib-jdk8"))

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // grpc
    implementation("io.grpc:grpc-netty:$grpcVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("net.devh:grpc-spring-boot-starter:2.14.0.RELEASE")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("com.google.protobuf:protobuf-java-util:$protobufUtilsVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufUtilsVersion")

    // Java
    api("javax.annotation:javax.annotation-api:1.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

artifacts {
    archives(sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact("build/libs/${project.name}-$version.jar") {
                artifactId = project.name
                extension = "jar"
            }
            artifact(tasks["sourcesJar"])
        }
    }
    repositories {
//        maven(url = nexusUrl) {
//            name = "nexus"
//        }
    }
}
