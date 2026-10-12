package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import kotlinx.serialization.Serializable

public class FriendlyActivityClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint: FriendlyEndpoint = endpoint / "activity"

    public sealed interface ListResult {
        public fun orThrow(): Success

        public data class IOError(val cause: Exception) : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(
            val hasUnread: Boolean,
            val cursor: Cursor<ActivityDetails>,
        ) : ListResult {
            override fun orThrow(): Success = this
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
            compatible()
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return ListResult.IOError(request.cause)
            is ServerError -> return ListResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            Unauthorized -> return ListResult.Unauthorized
            OK -> response.body<ListResponse>()
            else -> error("Unknown status code")
        }
        return responseBody.typed()
    }

    @Serializable
    private data class ListResponse(
        val hasUnread: Boolean,
        val cursor: CursorSerializable<ActivityDetailsSerializable>,
    )

    private fun ListResponse.typed(): ListResult.Success = ListResult.Success(
        hasUnread = hasUnread,
        cursor = cursor.typed { details -> details.typed() },
    )

    public sealed interface ReadResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : ReadResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : ReadResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : ReadResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : ReadResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : ReadResult {
            override fun orThrow() {}
        }
    }

    public suspend fun read(
        authorization: Authorization,
        id: ActivityId,
    ): ReadResult {
        val endpoint = endpoint / "read" / "${id.long}"
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return ReadResult.IOError(request.cause)
            is ServerError -> return ReadResult.ServerError
            is Success -> request.response
        }
        when (response.status) {
            NotFound -> return ReadResult.NotFound
            Unauthorized -> return ReadResult.Unauthorized
            OK -> return ReadResult.Success
            else -> error("Unknown status code")
        }
    }
}
