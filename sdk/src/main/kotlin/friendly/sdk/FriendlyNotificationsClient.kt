package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

public class FriendlyNotificationsClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint = endpoint / "notifications"

    public sealed interface DetailsResult {
        public fun orThrow(): NotificationDetails

        public data class IOError(val cause: Exception) : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val notification: NotificationDetails) :
            DetailsResult {
            override fun orThrow(): NotificationDetails = notification
        }
    }

    public suspend fun details(
        authorization: Authorization,
        id: NotificationId,
    ): DetailsResult {
        val endpoint = endpoint / "details" / id.long.toString()
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Get
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return DetailsResult.IOError(request.cause)
            is ServerError -> return DetailsResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            Unauthorized -> return DetailsResult.Unauthorized
            OK -> response.body<NotificationDetailsSerializable>()
            else -> error("Unknown status code")
        }
        return DetailsResult.Success(responseBody.typed())
    }
}
