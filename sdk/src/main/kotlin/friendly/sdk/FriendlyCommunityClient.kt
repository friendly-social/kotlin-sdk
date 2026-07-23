package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable

public class FriendlyCommunityClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint: FriendlyEndpoint = endpoint / "community"

    @Serializable
    private data class PostRequestBody(val text: CommunityPostTextSerializable)

    public sealed interface PostResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : PostResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : PostResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : PostResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : PostResult {
            override fun orThrow() {}
        }
    }

    public suspend fun post(
        authorization: Authorization,
        text: CommunityPostText,
    ): PostResult {
        val requestBody = PostRequestBody(text.serializable())
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return PostResult.IOError(request.cause)
            is ServerError -> return PostResult.ServerError
            is Success -> request.response
        }
        return when (response.status) {
            OK -> PostResult.Success
            Unauthorized -> PostResult.Unauthorized
            else -> error("Unknown status code")
        }
    }

    public sealed interface ListResult {
        public fun orThrow(): Cursor<CommunityPost>

        public data class IOError(val cause: Exception) : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : ListResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val cursor: Cursor<CommunityPost>) :
            ListResult {
            override fun orThrow(): Cursor<CommunityPost> = cursor
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
            OK -> response.body<CursorSerializable<CommunityPostSerializable>>()
            else -> error("Unknown status code")
        }
        val cursor = responseBody.typed { post -> post.typed() }
        return ListResult.Success(cursor)
    }
}
