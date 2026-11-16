plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktlint)
    application
}

application {
    mainClass = "friendly.sdk.examples.MainKt"
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        extraWarnings = true
        allWarningsAsErrors = true
        progressiveMode = true
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
    }
}

dependencies {
    implementation(projects.sdk)
    implementation(libs.ktor.client.logging)
    implementation(libs.slf4j.simple)
}
