plugins {
    // Basic Plugins
    `java-library`
    groovy
    jacoco
    `maven-publish`
    signing

    // Versioning Plugins
    id("org.ajoberstar.grgit") version "4.1.1"

    id("com.adarshr.test-logger") version "3.2.0"
    id("com.github.ksoichiro.console.reporter") version "0.6.3"
    id("com.github.nbaztec.coveralls-jacoco") version "1.2.15"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

group = "org.slog4j"
description = "Structured Event Logging for Java"
version = grgit.describe(mapOf(
    "tags" to true,
    "always" to true,
)).removePrefix("v")
println("version: $version")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    api("org.slf4j:slf4j-api:1.7.36")
    api("org.joda:joda-convert:2.2.3")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.yaml:snakeyaml:2.1")

    testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")

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
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}

tasks.reportCoverage {
    dependsOn(tasks.jacocoTestReport)
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

val groupId = group.toString()
val artifactId = name

val ghOrg = "slog4j"
val ghRepo = name
val ghHostAndPath = "github.com/${ghOrg}/${ghRepo}.git"

publishing {
    publications {
        create<MavenPublication>(artifactId) {
            from(components["java"])
            version = version.toString()
            pom {
                name.set(artifactId)
                description.set(project.description)
                url.set("https://$ghHostAndPath")
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
                    connection.set("scm:git:$ghHostAndPath")
                    developerConnection.set("scm:git:ssh://$ghHostAndPath")
                    url.set("https://$ghHostAndPath")
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications[artifactId])
}

nexusPublishing {
    repositories {
        val sonatypeStagingProfileId: String? by project
        val sonatypeUsername: String? by project
        val sonatypePassword: String? by project

        if ((sonatypeStagingProfileId != null) && (sonatypeUsername != null) && (sonatypePassword != null)) {
            sonatype {
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

                stagingProfileId.set(sonatypeStagingProfileId)
                username.set(sonatypeUsername)
                password.set(sonatypePassword)
            }
        }
    }
}
