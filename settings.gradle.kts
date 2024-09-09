plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
includeBuild("gradle-plugin") {
    dependencySubstitution {
        substitute(module("com.bloder:gradle-plugin:1.0.0")).using(project(":"))
    }
}
includeBuild("compiler-plugin")
include(":lib")
include(":sample")
