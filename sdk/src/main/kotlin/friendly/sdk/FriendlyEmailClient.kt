package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable

public class FriendlyEmailClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint: FriendlyEndpoint = endpoint / "email"

    @Serializable
    private data class LinkRequestBody(val email: EmailSerializable)

    public sealed interface LinkResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : LinkResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : LinkResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object EmailAlreadyUsed : LinkResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : LinkResult {
            override fun orThrow() {}
        }
    }

    public suspend fun link(
        authorization: Authorization,
        email: Email,
    ): LinkResult {
        val endpoint = endpoint / "link"
        val requestBody = LinkRequestBody(email.serializable())
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return LinkResult.IOError(request.cause)
            is ServerError -> return LinkResult.ServerError
            is Success -> request.response
        }
        return when (response.status) {
            Conflict -> EmailAlreadyUsed
            OK -> Success
            else -> error("Unknown status")
        }
    }

    @Serializable
    private class ConfirmRequestBody(val code: EmailCodeSerializable)

    public sealed interface ConfirmResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : ConfirmResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : ConfirmResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object InvalidCode : ConfirmResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : ConfirmResult {
            override fun orThrow() {}
        }
    }

    public suspend fun confirm(
        authorization: Authorization,
        code: EmailCode,
    ): ConfirmResult {
        val endpoint = endpoint / "confirm"
        val requestBody = ConfirmRequestBody(code.serializable())
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return ConfirmResult.IOError(request.cause)
            is ServerError -> return ConfirmResult.ServerError
            is Success -> request.response
        }
        return when (response.status) {
            Forbidden -> InvalidCode
            OK -> Success
            else -> error("Unknown status")
        }
    }

    public sealed interface UnlinkResult {
        public fun orThrow()

        public data class IOError(val cause: Exception) : UnlinkResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : UnlinkResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Success : UnlinkResult {
            override fun orThrow() {}
        }
    }

    public suspend fun unlink(authorization: Authorization): UnlinkResult {
        val endpoint = endpoint / "unlink"
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return UnlinkResult.IOError(request.cause)
            is ServerError -> return UnlinkResult.ServerError
            is Success -> request.response
        }
        return when (response.status) {
            OK -> Success
            else -> error("Unknown status")
        }
    }
}
