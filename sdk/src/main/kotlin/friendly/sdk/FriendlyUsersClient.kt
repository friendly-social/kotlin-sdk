package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable

public class FriendlyUsersClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint = endpoint / "users"

    public sealed interface Details2Result {
        public fun orThrow(): Success

        public data class IOError(val cause: Exception) : Details2Result {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : Details2Result {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : Details2Result {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(
            val details: UserDetails,
            val commonFriends: List<UserDetails>?,
        ) : Details2Result {
            override fun orThrow(): Success = this
        }
    }

    public suspend fun details2(authorization: Authorization): Details2Result {
        val endpoint = endpoint / "details2"
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Get
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return Details2Result.IOError(request.cause)
            is ServerError -> return Details2Result.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            Unauthorized -> return Details2Result.Unauthorized
            OK -> response.body<Details2Response>()
            else -> error("Unknown status code")
        }
        return responseBody.typed()
    }

    public suspend fun details2(
        authorization: Authorization,
        id: UserId,
        accessHash: UserAccessHash,
    ): Details2Result {
        val endpoint = endpoint / "details2" / "${id.long}" / accessHash.string
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Get
            authorization(authorization)
        }
        val response = when (request) {
            is IOError -> return Details2Result.IOError(request.cause)
            is ServerError -> return Details2Result.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            Unauthorized -> return Details2Result.Unauthorized
            OK -> response.body<Details2Response>()
            else -> error("Unknown status code")
        }
        return responseBody.typed()
    }

    @Serializable
    private data class Details2Response(
        val details: UserDetailsSerializable,
        val commonFriends: List<UserDetailsSerializable>?,
    )

    private fun Details2Response.typed(): Details2Result.Success =
        Details2Result.Success(
            details = details.typed(),
            commonFriends = commonFriends?.map { user -> user.typed() },
        )

    public sealed interface DetailsResult {
        public fun orThrow(): UserDetails

        public data class IOError(val cause: Exception) : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object Unauthorized : DetailsResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val details: UserDetails) : DetailsResult {
            override fun orThrow(): UserDetails = details
        }
    }

    public suspend fun details(authorization: Authorization): DetailsResult {
        val endpoint = endpoint / "details"
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
            OK -> response.body<UserDetailsSerializable>()
            else -> error("Unknown status code")
        }
        val details = responseBody.typed()
        return DetailsResult.Success(details)
    }

    public suspend fun details(
        authorization: Authorization,
        id: UserId,
        accessHash: UserAccessHash,
    ): DetailsResult {
        val endpoint = endpoint / "details" / "${id.long}" / accessHash.string
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
            OK -> response.body<UserDetailsSerializable>()
            else -> error("Unknown status code")
        }
        val details = responseBody.typed()
        return DetailsResult.Success(details)
    }

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
        public data object Success : EditResult {
            override fun orThrow() {}
        }
    }

    public suspend fun edit(
        authorization: Authorization,
        nickname: Nickname,
        description: UserDescription,
        interests: InterestList,
        avatar: FileDescriptor?,
        socialLink: SocialLink?,
    ): EditResult = edit(
        authorization = authorization,
        nickname = Field(nickname),
        description = Field(description),
        interests = Field(interests),
        avatar = Field(avatar),
        socialLink = Field(socialLink),
    )

    @Serializable
    private data class EditRequestBody(
        val nickname: FieldSerializable<NicknameSerializable>? = null,
        val description: FieldSerializable<UserDescriptionSerializable>? = null,
        val interests: FieldSerializable<InterestListSerializable>? = null,
        val avatar: FieldSerializable<FileDescriptorSerializable?>? = null,
        val socialLink: FieldSerializable<SocialLinkSerializable?>? = null,
    )

    public suspend fun edit(
        authorization: Authorization,
        nickname: Field<Nickname>? = null,
        description: Field<UserDescription>? = null,
        interests: Field<InterestList>? = null,
        avatar: Field<FileDescriptor?>? = null,
        socialLink: Field<SocialLink?>? = null,
    ): EditResult {
        val endpoint = endpoint / "edit"
        val requestBody = EditRequestBody(
            nickname = nickname?.serializable { value ->
                value.serializable()
            },
            description = description?.serializable { value ->
                value.serializable()
            },
            interests = interests?.serializable { value ->
                value.serializable()
            },
            avatar = avatar?.serializable { value ->
                value?.serializable()
            },
            socialLink = socialLink?.serializable { value ->
                value?.serializable()
            },
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Patch
            authorization(authorization)
            setBody(requestBody)
        }
        val response = when (request) {
            is IOError -> return EditResult.IOError(request.cause)
            is ServerError -> return ServerError
            is Success -> request.response
        }
        when (response.status) {
            Unauthorized -> return Unauthorized
            OK -> return Success
            else -> error("Unknown status code")
        }
    }
}
