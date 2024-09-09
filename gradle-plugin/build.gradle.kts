plugins {
    kotlin("jvm") version("2.0.0")
    kotlin("kapt") version("2.0.0")
    id("java-gradle-plugin")
    `maven-publish`
}

group = "com.bloder"
version = "1.0.0"


allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.google.com")
        maven("https://plugins.gradle.org/m2/")
        google()
    }
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.0.0")
}

gradlePlugin {
    plugins {
        create("aotiosInject") {
            id = "aotios-inject"
            implementationClass = "com.bloder.aotiosinject.AotiosInjectGradleSubPlugin"
        }
    }
}

tasks.register("sourcesJar", Jar::class) {
    group = "build"
    description = "Assembles Kotlin sources"

    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    dependsOn(tasks.classes)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            //artifact(tasks["dokkaJar"])

            pom {
                name.set("aotios-inject")
                description.set("KotlinCompilerPluginExample")
                url.set("https://github.com/Foso/KotlinCompilerPluginExample")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/Foso/Ktorfit/blob/master/LICENSE.txt")
                    }
                }
                scm {
                    url.set("https://github.com/Foso/KotlinCompilerPluginExample")
                    connection.set("scm:git:git://github.com/Foso/KotlinCompilerPluginExample.git")
                }
                developers {
                    developer {
                        name.set("Jens Klingenberg")
                        url.set("https://github.com/Foso")
                    }
                }
            }
        }
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url = when {
                    "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                    else -> property("sonatypeReleaseUrl")
                } as String
                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }
    }
}


tasks.build {
    dependsOn(":kotlin-plugin:publishToMavenLocal")

}