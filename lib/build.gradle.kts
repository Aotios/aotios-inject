plugins {
    id("org.jetbrains.kotlin.multiplatform") version libs.versions.kotlin
}

apply(plugin = "aotios-inject")

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvm()
    linuxX64("linux")
    //js()
    sourceSets {
        val commonMain by getting {

            dependencies {

            }
        }

//        val jsMain by getting {
//
//            dependencies {
//
//            }
//        }

        val jvmMain by getting {

        }
        val linuxMain by getting {

        }

    }
}

