import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ktlint)
    application
}

application {
    mainClass = "friendly.sdk.MainKt"
}

group = "me.y9san9.friendly"
version = libs.versions.friendly.get()

kotlin {
    jvmToolchain(8)

    explicitApi()

    compilerOptions {
        extraWarnings = true
        allWarningsAsErrors = true
        progressiveMode = true
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
    }
}

dependencies {
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.client.serialization.json)
    implementation(libs.kotlinx.serialization.json)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    pom {
        name = "friendly"
        description = "SDK for accessing Friendly APIs. Make new friends!"
        url = "https://github.com/friendly/sdk"

        licenses {
            license {
                name = "MIT"
                distribution = "repo"
                url = "https://github.com/friendly/sdk/blob/main/LICENSE.md"
            }
        }

        developers {
            developer {
                id = "y9san9"
                name = "Alex Sokol"
                email = "y9san9@gmail.com"
            }
        }

        scm {
            connection = "scm:git:ssh://github.com/friendly/sdk.git"
            developerConnection = "scm:git:ssh://github.com/friendly/sdk.git"
            url = "https://github.com/friendly/sdk"
        }
    }

    signAllPublications()
}
