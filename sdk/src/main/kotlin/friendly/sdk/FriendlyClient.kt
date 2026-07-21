package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.config
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

public class FriendlyClient(
    private val endpoint: FriendlyEndpoint,
    httpClient: HttpClient = HttpClient(CIO),
) {
    private val httpClient = httpClient.config {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    public val auth: FriendlyAuthClient = FriendlyAuthClient(
        endpoint = endpoint,
        httpClient = this.httpClient,
    )

    public val files: FriendlyFilesClient = FriendlyFilesClient(
        endpoint = endpoint,
        httpClient = this.httpClient,
    )

    public val users: FriendlyUsersClient = FriendlyUsersClient(
        endpoint = endpoint,
        httpClient = this.httpClient,
    )

    public val friends: FriendlyFriendsClient = FriendlyFriendsClient(
        endpoint = endpoint,
        httpClient = this.httpClient,
    )

    public val network: FriendlyNetworkClient = FriendlyNetworkClient(
        endpoint = endpoint,
        httpClient = this.httpClient,
    )

    public val feed: FriendlyFeedClient = FriendlyFeedClient(
        endpoint = endpoint,
        httpClient = this.httpClient,
    )

    public val email: FriendlyEmailClient = FriendlyEmailClient(
        endpoint = endpoint,
        httpClient = this.httpClient,
    )

    public val community: FriendlyCommunityClient = FriendlyCommunityClient(
        endpoint = endpoint,
        httpClient = this.httpClient,
    )

    public companion object {
        public fun localhost(
            port: Int = 8080,
            httpClient: HttpClient = HttpClient(CIO),
        ): FriendlyClient {
            val endpoint = FriendlyEndpoint.localhost(port)
            return FriendlyClient(endpoint, httpClient)
        }

        @Suppress("DEPRECATION", "ktlint:standard:max-line-length")
        @Deprecated(
            message = "This server is localed in Russia and is not used anymore",
            replaceWith = ReplaceWith("production"),
        )
        public fun meetacy(
            httpClient: HttpClient = HttpClient(CIO),
        ): FriendlyClient {
            val endpoint = FriendlyEndpoint.meetacy()
            return FriendlyClient(endpoint, httpClient)
        }

        public fun production(
            httpClient: HttpClient = HttpClient(CIO),
        ): FriendlyClient {
            val endpoint = FriendlyEndpoint.production()
            return FriendlyClient(endpoint, httpClient)
        }
    }
}
