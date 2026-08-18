package friendly.sdk.examples

import friendly.sdk.FriendlyClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout

val httpClient = HttpClient(CIO) {
    // install(Logging) {
    //     level = LogLevel.ALL
    // }
    install(HttpTimeout) {
        requestTimeoutMillis = 1_000_000
    }
}

// val client = FriendlyClient.production(httpClient = httpClient)
val client = FriendlyClient.localhost(httpClient = httpClient)

suspend fun main() {
    // authExample()
    // filesExample()
    // emailExample()
    // usersExample()
    // friendsExample()
    // networkExample()
    // feedExample()
    // communityExample()
    // activityExample()
}
