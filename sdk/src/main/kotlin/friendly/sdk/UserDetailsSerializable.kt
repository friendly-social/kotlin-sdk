package friendly.sdk

import kotlinx.serialization.Serializable

@Serializable
public data class UserDetailsSerializable(
    val id: UserIdSerializable,
    val accessHash: UserAccessHashSerializable,
    val nickname: NicknameSerializable,
    val description: UserDescriptionSerializable,
    val interests: InterestListSerializable,
    val avatar: FileDescriptorSerializable?,
    val socialLink: SocialLinkSerializable?,
    val email: EmailSerializable?,
    val friendship: FriendshipSerializable,
) {
    public fun typed(): UserDetails = UserDetails(
        id = id.typed(),
        accessHash = accessHash.typed(),
        nickname = nickname.typed(),
        description = description.typed(),
        interests = interests.typed(),
        avatar = avatar?.typed(),
        socialLink = socialLink?.typed(),
        email = email?.typed(),
        friendship = friendship.typed(),
    )
}
