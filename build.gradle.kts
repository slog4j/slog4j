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
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = "org.slog4j"
val gh_org = "slog4j"
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

fun findProperty(s: String) = project.findProperty(s) as String?

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
            groupId = group.toString()
            artifactId = name
            version = version.toString()
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/${gh_org}/${name}.git")
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
                    connection.set("scm:git:github.com/${gh_org}/${name}.git")
                    developerConnection.set("scm:git:ssh://github.com/${gh_org}/${name}.git")
                    url.set("https://github.com/${gh_org}/${name}.git")
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications[project.name])
}

nexusPublishing {
    repositories {
        sonatype {
            // 'sonatype' is pre-configured for Sonatype Nexus (OSSRH) which is used for The Central Repository
            stagingProfileId.set(System.getenv("SONATYPE_STAGING_PROFILE_ID") ?: findProperty("sonatype.staging.profile.id")) //can reduce execution time by even 10 seconds
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("SONATYPE_USERNAME") ?: findProperty("sonatype.username"))
            password.set(System.getenv("SONATYPE_PASSWORD") ?: findProperty("sonatype.password"))
        }
    }
}
