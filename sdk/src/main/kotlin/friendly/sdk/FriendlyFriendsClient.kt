package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

public class FriendlyFriendsClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint: FriendlyEndpoint = endpoint / "friends"

    @Serializable
    private data class GenerateResponseBody(
        val token: FriendTokenSerializable,
        val expiration: Instant,
    )

    public sealed interface GenerateResult {
        public fun orThrow(): FriendToken

        public data class IOError(val cause: Exception) : GenerateResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : GenerateResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : GenerateResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(
            val token: FriendToken,
            val expiration: Instant,
        ) : GenerateResult {
            override fun orThrow(): FriendToken = token
        }
    }

    public suspend fun generate(authorization: Authorization): GenerateResult {
        val endpoint = endpoint / "generate"
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return GenerateResult.IOError(request.cause)
            is ServerError -> return GenerateResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            Unauthorized -> return GenerateResult.Unauthorized
            OK -> response.body<GenerateResponseBody>()
            else -> error("Unknown status code")
        }
        val token = responseBody.token.typed()
        val expiration = responseBody.expiration
        return GenerateResult.Success(token, expiration)
    }

    @Serializable
    private data class AddRequestBody(val token: FriendTokenSerializable)

    @Serializable
    private sealed interface AddResponseBody {
        @Serializable
        @SerialName("FriendTokenExpired")
        data object FriendTokenExpired : AddResponseBody

        @Serializable
        @SerialName("Success")
        data object Success : AddResponseBody
    }

    public sealed interface AddResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : AddResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : AddResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : AddResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object FriendTokenExpired : AddResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : AddResult {
            override fun orThrow() {}
        }
    }

    public suspend fun add(
        authorization: Authorization,
        token: FriendToken,
    ): AddResult {
        val endpoint = endpoint / "add"
        val requestBody = AddRequestBody(token = token.serializable())
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return AddResult.IOError(request.cause)
            is ServerError -> return AddResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            OK -> response.body<AddResponseBody>()
            Unauthorized -> return AddResult.Unauthorized
            else -> error("Unknown status code")
        }
        return responseBody.toResult()
    }

    private fun AddResponseBody.toResult(): AddResult = when (this) {
        FriendTokenExpired -> FriendTokenExpired
        Success -> Success
    }

    @Serializable
    private data class AddDetailsResponseBody(
        val nickname: NicknameSerializable,
        val description: UserDescriptionSerializable,
        val interests: InterestListSerializable,
        val avatar: FileDescriptorSerializable?,
        val expiration: Instant,
    )

    public sealed interface AddDetailsResult {
        public fun orThrow(): Success

        public data class IOError(val cause: Exception) : AddDetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : AddDetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : AddDetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(
            val nickname: Nickname,
            val description: UserDescription,
            val interests: InterestList,
            val avatar: FileDescriptor?,
            val expiration: Instant,
        ) : AddDetailsResult {
            override fun orThrow(): Success = this
        }
    }

    public suspend fun addDetails(token: FriendToken): AddDetailsResult {
        val endpoint = endpoint / "add" / "details" / token.string
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Get
        }
        val response = when (request) {
            is IOError -> return AddDetailsResult.IOError(request.cause)
            is ServerError -> return AddDetailsResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            OK -> response.body<AddDetailsResponseBody>()
            NotFound -> return AddDetailsResult.NotFound
            else -> error("Unknown status code")
        }
        return AddDetailsResult.Success(
            nickname = responseBody.nickname.typed(),
            description = responseBody.description.typed(),
            interests = responseBody.interests.typed(),
            avatar = responseBody.avatar?.typed(),
            expiration = responseBody.expiration,
        )
    }

    @Serializable
    private data class RequestRequestBody(
        val userId: UserIdSerializable,
        val userAccessHash: UserAccessHashSerializable,
    )

    @Serializable
    internal data object RequestResponseBody

    public sealed interface RequestResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : RequestResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : RequestResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : RequestResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : RequestResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : RequestResult {
            override fun orThrow() {}
        }
    }

    public suspend fun request(
        authorization: Authorization,
        userId: UserId,
        userAccessHash: UserAccessHash,
    ): RequestResult {
        val endpoint = endpoint / "request"
        val requestBody = RequestRequestBody(
            userId = userId.serializable(),
            userAccessHash = userAccessHash.serializable(),
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return RequestResult.IOError(request.cause)
            is ServerError -> return RequestResult.ServerError
            is Success -> request.response
        }
        when (response.status) {
            OK -> response.body<RequestResponseBody>()
            Unauthorized -> return RequestResult.Unauthorized
            NotFound -> return RequestResult.NotFound
            else -> error("Unknown status code")
        }
        return RequestResult.Success
    }

    @Serializable
    private data class DeclineRequestBody(
        val userId: UserIdSerializable,
        val userAccessHash: UserAccessHashSerializable,
    )

    @Serializable
    internal data object DeclineResponseBody

    public sealed interface DeclineResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : DeclineResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : DeclineResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : DeclineResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object NotFound : DeclineResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : DeclineResult {
            override fun orThrow() {}
        }
    }

    public suspend fun decline(
        authorization: Authorization,
        userId: UserId,
        userAccessHash: UserAccessHash,
    ): DeclineResult {
        val endpoint = endpoint / "decline"
        val requestBody = DeclineRequestBody(
            userId = userId.serializable(),
            userAccessHash = userAccessHash.serializable(),
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return DeclineResult.IOError(request.cause)
            is ServerError -> return DeclineResult.ServerError
            is Success -> request.response
        }
        when (response.status) {
            OK -> response.body<DeclineResponseBody>()
            Unauthorized -> return DeclineResult.Unauthorized
            NotFound -> return DeclineResult.NotFound
            else -> error("Unknown status code")
        }
        return DeclineResult.Success
    }
}
