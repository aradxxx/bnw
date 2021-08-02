buildscript {
    repositories {
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath(kotlin("gradle-plugin:1.5.21"))
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint").version("10.1.0")
    id("io.gitlab.arturbosch.detekt").version("1.17.1")
}

allprojects {
    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("io.gitlab.arturbosch.detekt")
    }

    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

detekt {
    parallel = true
    input = files(
        "src/main/java",
        "src/test/kotlin",
        "src/androidTest/java",
        "src/main/kotlin",
        "src/test/java",
        "src/androidTest/kotlin",
    )
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    android.set(true)
}

task("quality") {
    group = "verification"
    dependsOn("ktlintCheck", "detekt", ":app:lint")
}
