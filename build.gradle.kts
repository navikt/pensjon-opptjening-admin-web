import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val azureAdClient = "0.0.7"
val jacksonVersion = "2.17.1"
val logbackEncoderVersion = "8.0"
val springCloudContractVersion = "4.0.4"
val mockkVersion = "1.13.16"
val assertJVersion = "3.27.3"
val jsonUnitVersion = "4.1.0"
val wiremockVersion = "3.11.0"
val mockitoVersion = "5.4.0"
val navTokenSupportVersion = "5.0.16"
val hibernateValidatorVersion = "8.0.1.Final"
val junit5Version = "5.11.3"
val okHttpVersion = "1111"


val snakeYamlVersion = "2.3"
val snappyJavaVersion = "1.1.10.7"
// val httpClient5Version = "5.3.1" // TODO: 5.4 feiler med NoClassDefFoundError
val httpClient5Version = "5.4.3" // TODO: 5.4 feiler med NoClassDefFoundError
val httpClientVersion = "4.5.14" // deprecated, men brukes av

plugins {
    val kotlinVersion = "2.0.20"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "3.4.2"
    id("com.github.ben-manes.versions") version "0.51.0"
}

apply(plugin = "io.spring.dependency-management")
apply(plugin = "kotlin-jpa")


group = "no.nav.pensjon.opptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.github.com/navikt/maven-release") {
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework:spring-aspects")
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")

    // Internal libraries
    implementation("no.nav.pensjonopptjening:pensjon-opptjening-azure-ad-client:$azureAdClient")
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    // Log and metric
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    // transitive dependency overrides
    implementation("org.yaml:snakeyaml:$snakeYamlVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("org.xerial.snappy:snappy-java:$snappyJavaVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClient5Version")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("org.wiremock:wiremock-jetty12:$wiremockVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    maxParallelForks = 1 //Disable parallell execution due to shared resources (db/wiremock)
    useJUnitPlatform()
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
        )
    }
}
