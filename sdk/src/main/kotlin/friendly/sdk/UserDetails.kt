package friendly.sdk

public data class UserDetails(
    val id: UserId,
    val accessHash: UserAccessHash,
    val nickname: Nickname,
    val description: UserDescription,
    val interests: InterestList,
    val avatar: FileDescriptor?,
    val socialLink: SocialLink?,
    val email: Email?,
    val friendship: Friendship,
) {
    public val descriptor: UserDescriptor
        get() = UserDescriptor(id, accessHash)

    public fun serializable(): UserDetailsSerializable =
        UserDetailsSerializable(
            id = id.serializable(),
            accessHash = accessHash.serializable(),
            nickname = nickname.serializable(),
            description = description.serializable(),
            interests = interests.serializable(),
            avatar = avatar?.serializable(),
            socialLink = socialLink?.serializable(),
            email = email?.serializable(),
            friendship = friendship.serializable(),
        )
}
