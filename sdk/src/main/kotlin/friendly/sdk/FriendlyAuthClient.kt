package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable

public class FriendlyAuthClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint: FriendlyEndpoint = endpoint / "auth"

    @Serializable
    private data class GenerateRequestBody(
        val nickname: NicknameSerializable,
        val description: UserDescriptionSerializable,
        val interests: InterestListSerializable,
        val avatar: FileDescriptorSerializable?,
        val socialLink: SocialLinkSerializable?,
    )

    @Serializable
    private data class GenerateResponseBody(
        val token: TokenSerializable,
        val id: UserIdSerializable,
        val accessHash: UserAccessHashSerializable,
    )

    public sealed interface GenerateResult {
        public fun orThrow(): Authorization

        public data class IOError(val cause: Exception) : GenerateResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : GenerateResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val authorization: Authorization) :
            GenerateResult {
            override fun orThrow(): Authorization = authorization
        }
    }

    public suspend fun generate(
        nickname: Nickname,
        description: UserDescription,
        interests: InterestList,
        avatar: FileDescriptor?,
        socialLink: SocialLink?,
    ): GenerateResult {
        val endpoint = endpoint / "generate"
        val requestBody = GenerateRequestBody(
            nickname = nickname.serializable(),
            description = description.serializable(),
            interests = interests.serializable(),
            avatar = avatar?.serializable(),
            socialLink = socialLink?.serializable(),
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return GenerateResult.IOError(request.cause)
            is ServerError -> return GenerateResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            OK -> response.body<GenerateResponseBody>()
            else -> error("Unknown status")
        }
        val authorization = responseBody.toAuthorization()
        return GenerateResult.Success(authorization)
    }

    private fun GenerateResponseBody.toAuthorization(): Authorization =
        Authorization(
            id = id.typed(),
            accessHash = accessHash.typed(),
            token = token.typed(),
        )

    @Serializable
    private data class FirebaseRequestBody(
        val firebaseToken: FirebaseTokenSerializable,
    )

    @Serializable
    private data object FirebaseResponseBody

    public sealed interface FirebaseResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : FirebaseResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : FirebaseResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : FirebaseResult {
            override fun orThrow() {}
        }
    }

    public suspend fun firebase(
        authorization: Authorization,
        firebaseToken: FirebaseToken,
    ): FirebaseResult {
        val endpoint = endpoint / "firebase"
        val requestBody = FirebaseRequestBody(
            firebaseToken = firebaseToken.serializable(),
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return FirebaseResult.IOError(request.cause)
            is ServerError -> return FirebaseResult.ServerError
            is Success -> request.response
        }
        when (response.status) {
            OK -> response.body<FirebaseResponseBody>()
            else -> error("Unknown status")
        }
        return FirebaseResult.Success
    }

    @Serializable
    private data object LogoutResponseBody

    public sealed interface LogoutResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : LogoutResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : LogoutResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : LogoutResult {
            override fun orThrow() {}
        }
    }

    @Serializable
    private data class EmailRequestBody(val email: EmailSerializable)

    public sealed interface EmailResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : EmailResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : EmailResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object EmailAlreadyUsed : EmailResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : EmailResult {
            override fun orThrow() {}
        }
    }

    public suspend fun email(email: Email): EmailResult {
        val endpoint = endpoint / "email"
        val requestBody = EmailRequestBody(email.serializable())
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return EmailResult.IOError(request.cause)
            is ServerError -> return EmailResult.ServerError
            is Success -> request.response
        }
        return when (response.status) {
            Conflict -> EmailAlreadyUsed
            OK -> Success
            else -> error("Unknown status")
        }
    }

    @Serializable
    private class LoginRequestBody(
        val email: EmailSerializable,
        val code: LoginCodeSerializable,
    )

    public sealed interface LoginResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : LoginResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : LoginResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object InvalidCode : LoginResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : LoginResult {
            override fun orThrow() {}
        }
    }

    public suspend fun login(email: Email, code: LoginCode): LoginResult {
        val endpoint = endpoint / "login"
        val requestBody = LoginRequestBody(
            email = email.serializable(),
            code = code.serializable(),
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return LoginResult.IOError(request.cause)
            is ServerError -> return LoginResult.ServerError
            is Success -> request.response
        }
        return when (response.status) {
            Forbidden -> InvalidCode
            OK -> Success
            else -> error("Unknown status")
        }
    }

    public suspend fun logout(authorization: Authorization): LogoutResult {
        val endpoint = endpoint / "logout"
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return LogoutResult.IOError(request.cause)
            is ServerError -> return LogoutResult.ServerError
            is Success -> request.response
        }
        when (response.status) {
            OK -> response.body<LogoutResponseBody>()
            else -> error("Unknown status")
        }
        return LogoutResult.Success
    }
}
