package friendly.sdk

import kotlin.time.Instant

public data class CommunityPostDetails(
    val id: CommunityPostId,
    val accessHash: CommunityPostAccessHash,
    val text: CommunityPostText,
    val owner: UserDetails,
    val instant: Instant,
    val edited: Boolean,
) {
    public val descriptor: CommunityPostDescriptor =
        CommunityPostDescriptor(id, accessHash)

    public fun serializable(): CommunityPostDetailsSerializable =
        CommunityPostDetailsSerializable(
            id = id.serializable(),
            accessHash = accessHash.serializable(),
            text = text.serializable(),
            owner = owner.serializable(),
            instant = instant,
            edited = edited,
        )
}
