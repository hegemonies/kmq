import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf

val coroutineVersion = "1.7.2"
val grpcVersion = "1.57.2"
val protobufUtilsVersion = "3.24.0"
val grpcKotlinVersion = "1.3.0"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("com.google.protobuf") version "0.9.4"
}

group = "site.hegemonies"
version = "0.0.2"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    google()
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")

    // gRPC
    implementation("com.google.protobuf:protobuf-java-util:$protobufUtilsVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufUtilsVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

    // Protobuf
    protobuf(files("../proto-contracts"))

    // Java
    api("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    val classifier = protocClassifier()
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufUtilsVersion:$classifier"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion:$classifier"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.generateDescriptorSet = true
            it.descriptorSetOptions.includeImports = true

            it.builtins {
                create("kotlin")
            }

            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

// Make protoc work on macOS
fun protocClassifier(): String {
    return if (System.getProperty("os.name", "").startsWith("Mac")) "osx-x86_64" else ""
}
