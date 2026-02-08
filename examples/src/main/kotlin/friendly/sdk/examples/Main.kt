package friendly.sdk.examples

import friendly.sdk.FriendlyClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging

val httpClient = HttpClient(CIO) {
    install(Logging) {
        level = LogLevel.ALL
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 1_000_000
    }
}
val client = FriendlyClient.production(httpClient = httpClient)

suspend fun main() {
    // authExample()
    // filesExample()
    // usersExample()
    // friendsExample()
    // networkExample()
    // feedExample()
    // emailExample()
}
