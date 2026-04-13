import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val azureAdClient = "0.0.7"
val jacksonVersion = "2.21.2"
val logbackEncoderVersion = "9.0"
val springCloudContractVersion = "4.0.4"
val mockkVersion = "1.14.6"
val assertJVersion = "3.27.6"
val jsonUnitVersion = "5.0.0"
val wiremockVersion = "3.13.1"
val mockitoVersion = "6.1.0"
val navTokenSupportVersion = "6.0.5"
val hibernateValidatorVersion = "9.1.0.Final"
val junit5Version = "6.0.3"


val snakeYamlVersion = "2.6"
val snappyJavaVersion = "1.1.10.8"
val httpClient5Version = "5.5.2"
val httpClientVersion = "4.5.14" // deprecated, men brukes av
val jettyVersion = "12.1.6" // trengs pga wiremock

plugins {
    val kotlinVersion = "2.3.20"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "4.0.5"
    id("com.github.ben-manes.versions") version "0.53.0"
}

apply(plugin = "io.spring.dependency-management")
apply(plugin = "kotlin-jpa")


group = "no.nav.pensjon.opptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
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
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework:spring-aspects")
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("no.nav.security:token-client-spring:$navTokenSupportVersion")

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
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("org.wiremock:wiremock-jetty12:$wiremockVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")

    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")

    // trengs fordi wiremock henger etter på jetty-versjon i forhold til spring 4
    testImplementation("org.eclipse.jetty:jetty-bom:$jettyVersion")
    testImplementation("org.eclipse.jetty.ee10:jetty-ee10-bom:${jettyVersion}")
    testImplementation("org.eclipse.jetty.ee10:jetty-ee10-servlet:${jettyVersion}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_25)
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

tasks.withType<DependencyUpdatesTask>().configureEach {
    rejectVersionIf {
        isNonStableVersion(candidate.version)
    }
}

fun isNonStableVersion(version: String): Boolean {
    return listOf("BETA","RC","-M",".CR","ALPHA").any { version.uppercase().contains(it) }
}

