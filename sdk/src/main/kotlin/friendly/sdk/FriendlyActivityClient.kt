package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
            OK -> response.body<
                CursorSerializable<ActivityDetailsSerializable>,
                >()
            else -> error("Unknown status code")
        }
        val cursor = responseBody.typed { details -> details.typed() }
        return ListResult.Success(cursor)
    }

    public sealed interface NewUnreadsResult {
        public fun orThrow(): Ok

        public data class IOError(val cause: Exception) : NewUnreadsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : NewUnreadsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : NewUnreadsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public sealed interface Ok : NewUnreadsResult {
            override fun orThrow(): Ok
        }
        public data object None : Ok {
            override fun orThrow(): None = this
        }
        public data class Some(val activityId: ActivityId) : Ok {
            override fun orThrow(): Some = this
        }
    }

    public suspend fun newUnreads(
        authorization: Authorization,
    ): NewUnreadsResult {
        val endpoint = endpoint / "unreads" / "new"
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return NewUnreadsResult.IOError(request.cause)
            is ServerError -> return NewUnreadsResult.ServerError
            is Success -> request.response
        }
        val result = when (response.status) {
            Unauthorized -> return NewUnreadsResult.Unauthorized
            OK -> response.body<NewUnreadsResponse>()
            else -> error("Unknown status code")
        }
        return result.typed()
    }

    @Serializable
    private sealed interface NewUnreadsResponse {
        @Serializable
        @SerialName("none")
        data object None : NewUnreadsResponse

        @Serializable
        @SerialName("some")
        data class Some(val activityId: ActivityIdSerializable) :
            NewUnreadsResponse
    }

    private fun NewUnreadsResponse.typed(): NewUnreadsResult.Ok = when (this) {
        is None -> None
        is Some -> NewUnreadsResult.Some(activityId.typed())
    }

    public sealed interface AcknowledgeUnreadsResult {
        public fun orThrow(): Ok

        public data class IOError(val cause: Exception) :
            AcknowledgeUnreadsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : AcknowledgeUnreadsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : AcknowledgeUnreadsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public sealed interface Ok : AcknowledgeUnreadsResult {
            override fun orThrow(): Ok
        }
        public data object None : Ok {
            override fun orThrow(): None = this
        }
        public data class Some(val activityId: ActivityId) : Ok {
            override fun orThrow(): Some = this
        }
    }

    public suspend fun acknowledgeUnreads(
        authorization: Authorization,
    ): AcknowledgeUnreadsResult {
        val endpoint = endpoint / "unreads" / "acknowledge"
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return AcknowledgeUnreadsResult.IOError(request.cause)
            is ServerError -> return AcknowledgeUnreadsResult.ServerError
            is Success -> request.response
        }
        val result = when (response.status) {
            Unauthorized -> return AcknowledgeUnreadsResult.Unauthorized
            OK -> response.body<AcknowledgeUnreadsResponse>()
            else -> error("Unknown status code")
        }
        return result.typed()
    }

    @Serializable
    private sealed interface AcknowledgeUnreadsResponse {
        @Serializable
        @SerialName("none")
        data object None : AcknowledgeUnreadsResponse

        @Serializable
        @SerialName("some")
        data class Some(val activityId: ActivityIdSerializable) :
            AcknowledgeUnreadsResponse
    }

    @Suppress("ktlint:standard:max-line-length")
    private fun AcknowledgeUnreadsResponse.typed(): AcknowledgeUnreadsResult.Ok =
        when (this) {
            is None -> None
            is Some -> AcknowledgeUnreadsResult.Some(activityId.typed())
        }

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
