plugins {
    // Basic Plugins
    `java-library`
    groovy
    jacoco
    `maven-publish`

    // Versioning Plugins
    id("org.ajoberstar.grgit") version "5.0.0"

    id("com.adarshr.test-logger") version "3.2.0"
    id("com.github.kt3k.coveralls") version "2.12.0"
}

group = "org.slog4j"
description = "Structured Event Logging for Java"
version = grgit.describe(mapOf(
    "tags" to true,
    "always" to true,
)).removePrefix("v")
println("version: $version")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    api("org.slf4j:slf4j-api:1.7.36")
    api("org.joda:joda-convert:2.2.2")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.yaml:snakeyaml:1.30")

    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.11")
}

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf(
        "-Xlint:deprecation"
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        html.required.set(true)
    }
}

publishing {
    publications {
        create<MavenPublication>("slog4j") {
            from(components["java"])
            groupId = group.toString()
            artifactId = name
            version = version.toString()
            pom {
                name.set(project.name)
                description.set("Structured Event Logging for Java")
                url.set("https://github.com/slog4j/slog4j.git")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("ebo")
                        name.set("Eliezio Oliveira")
                        email.set("eliezio@pm.me")
                    }
                }
                scm {
                    url.set("https://github.com/slog4j/slog4j.git")
                }
            }
        }
    }
}
