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
    private data class PostRequestBody(
        val text: CommunityPostTextSerializable,
        val replyTo: CommunityPostDescriptorSerializable?,
    )

    public sealed interface PostResult {
        public fun orThrow(): CommunityPostDescriptor

        public data class IOError(val cause: Exception) : PostResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : PostResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : PostResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : PostResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val descriptor: CommunityPostDescriptor) :
            PostResult {
            override fun orThrow(): CommunityPostDescriptor = descriptor
        }
    }

    public suspend fun post(
        authorization: Authorization,
        text: CommunityPostText,
        replyTo: CommunityPostDescriptor? = null,
    ): PostResult {
        val requestBody = PostRequestBody(
            text = text.serializable(),
            replyTo = replyTo?.serializable(),
        )
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
        val responseBody = when (response.status) {
            NotFound -> return PostResult.NotFound
            Unauthorized -> return PostResult.Unauthorized
            OK -> response.body<CommunityPostDescriptorSerializable>()
            else -> error("Unknown status code")
        }
        return PostResult.Success(responseBody.typed())
    }

    public sealed interface DetailsResult {
        public fun orThrow(): Success

        public data class IOError(val cause: Exception) : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(
            val post: CommunityPostDetails,
            val replies: Cursor<CommunityPostDetails>,
            val upstream: List<CommunityPostDetails>,
        ) : DetailsResult {
            override fun orThrow(): Success = this
        }
    }

    public suspend fun details(
        authorization: Authorization,
        descriptor: CommunityPostDescriptor,
    ): DetailsResult {
        val endpoint = endpoint /
            descriptor.id.long.toString() /
            descriptor.accessHash.string
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
            OK -> response.body<DetailsResponseBody>()
            else -> error("Unknown status code")
        }
        return responseBody.typed()
    }

    @Serializable
    private data class DetailsResponseBody(
        val post: CommunityPostDetailsSerializable,
        val replies: CursorSerializable<CommunityPostDetailsSerializable>,
        val upstream: List<CommunityPostDetailsSerializable>,
    )

    private fun DetailsResponseBody.typed(): DetailsResult.Success =
        DetailsResult.Success(
            post = post.typed(),
            replies = replies.typed { post -> post.typed() },
            upstream = upstream.map { post -> post.typed() },
        )

    public sealed interface ListResult {
        public fun orThrow(): Cursor<CommunityPostDetails.Plain>

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
            val cursor: Cursor<CommunityPostDetails.Plain>,
        ) : ListResult {
            override fun orThrow(): Cursor<CommunityPostDetails.Plain> = cursor
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
                CursorSerializable<CommunityPostDetailsSerializable.Plain>,
                >()
            else -> error("Unknown status code")
        }
        val cursor = responseBody.typed { post -> post.typed() }
        return ListResult.Success(cursor)
    }

    public sealed interface FromResult {
        public fun orThrow(): Cursor<CommunityPostDetails>

        public data class IOError(val cause: Exception) : FromResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : FromResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : FromResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : FromResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val cursor: Cursor<CommunityPostDetails>) :
            FromResult {
            override fun orThrow(): Cursor<CommunityPostDetails> = cursor
        }
    }

    public suspend fun from(
        authorization: Authorization,
        userDescriptor: UserDescriptor,
        cursorId: CursorId?,
    ): FromResult {
        var endpoint = endpoint / "from" /
            userDescriptor.id.long.toString() /
            userDescriptor.accessHash.string
        if (cursorId != null) {
            endpoint = endpoint / cursorId.string
        }
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Get
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return FromResult.IOError(request.cause)
            is ServerError -> return FromResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            Unauthorized -> return FromResult.Unauthorized
            NotFound -> return FromResult.NotFound
            OK -> response.body<
                CursorSerializable<CommunityPostDetailsSerializable>,
                >()
            else -> error("Unknown status code")
        }
        val cursor = responseBody.typed { post -> post.typed() }
        return FromResult.Success(cursor)
    }

    public sealed interface RepliesResult {
        public fun orThrow(): Cursor<CommunityPostDetails>

        public data class IOError(val cause: Exception) : RepliesResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : RepliesResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : RepliesResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : RepliesResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val cursor: Cursor<CommunityPostDetails>) :
            RepliesResult {
            override fun orThrow(): Cursor<CommunityPostDetails> = cursor
        }
    }

    public suspend fun replies(
        authorization: Authorization,
        replyTo: CommunityPostDescriptor,
        cursorId: CursorId?,
    ): RepliesResult {
        var endpoint = endpoint /
            replyTo.id.long.toString() /
            replyTo.accessHash.string /
            "replies"
        if (cursorId != null) {
            endpoint = endpoint / cursorId.string
        }
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Get
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return RepliesResult.IOError(request.cause)
            is ServerError -> return RepliesResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            Unauthorized -> return RepliesResult.Unauthorized
            NotFound -> return RepliesResult.NotFound
            OK -> response.body<
                CursorSerializable<CommunityPostDetailsSerializable>,
                >()
            else -> error("Unknown status code")
        }
        val cursor = responseBody.typed { post -> post.typed() }
        return RepliesResult.Success(cursor)
    }

    @Serializable
    private data class EditRequestBody(
        val text: FieldSerializable<CommunityPostTextSerializable>?,
    )

    public sealed interface EditResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : EditResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : EditResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : EditResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : EditResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : EditResult {
            override fun orThrow() {}
        }
    }

    public suspend fun edit(
        authorization: Authorization,
        id: CommunityPostId,
        text: Field<CommunityPostText>?,
    ): EditResult {
        val endpoint = endpoint / id.long.toString() / "edit"
        val requestBody = EditRequestBody(
            text = text?.serializable { value -> value.serializable() },
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return EditResult.IOError(request.cause)
            is ServerError -> return EditResult.ServerError
            is Success -> request.response
        }
        return when (response.status) {
            OK -> EditResult.Success
            Unauthorized -> EditResult.Unauthorized
            NotFound -> EditResult.NotFound
            else -> error("Unknown status code")
        }
    }

    public sealed interface DeleteResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : DeleteResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : DeleteResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : DeleteResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : DeleteResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : DeleteResult {
            override fun orThrow() {}
        }
    }

    public suspend fun delete(
        authorization: Authorization,
        id: CommunityPostId,
    ): DeleteResult {
        val endpoint = endpoint / id.long.toString() / "delete"
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return DeleteResult.IOError(request.cause)
            is ServerError -> return DeleteResult.ServerError
            is Success -> request.response
        }
        return when (response.status) {
            OK -> DeleteResult.Success
            Unauthorized -> DeleteResult.Unauthorized
            NotFound -> DeleteResult.NotFound
            else -> error("Unknown status code")
        }
    }
}
