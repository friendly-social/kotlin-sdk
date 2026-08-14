package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body

public class FriendlyActivityClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint: FriendlyEndpoint = endpoint / "activity"

    public sealed interface ListResult {
        public fun orThrow(): Cursor<ActivityDetails>

        public data class IOError(val cause: Exception) : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val cursor: Cursor<ActivityDetails>) :
            ListResult {
            override fun orThrow(): Cursor<ActivityDetails> = cursor
        }
    }

    public suspend fun list(
        authorization: Authorization,
        cursorId: CursorId?,
    ): ListResult {
        var endpoint = endpoint / "list"
        if (cursorId != null) {
            endpoint = endpoint / cursorId.string
        }
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Get
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return ListResult.IOError(request.cause)
            is ServerError -> return ListResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            Unauthorized -> return ListResult.Unauthorized
            OK -> response.body<
                CursorSerializable<ActivityDetailsSerializable>,
                >()
            else -> error("Unknown status code")
        }
        val cursor = responseBody.typed { post -> post.typed() }
        return ListResult.Success(cursor)
    }
}
