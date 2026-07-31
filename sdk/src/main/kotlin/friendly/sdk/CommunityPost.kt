package friendly.sdk

import kotlin.time.Instant

public data class CommunityPost(
    val id: CommunityPostId,
    val text: CommunityPostText,
    val owner: UserDetails,
    val instant: Instant,
    val edited: Boolean,
) {
    public fun serializable(): CommunityPostSerializable =
        CommunityPostSerializable(
            id = id.serializable(),
            text = text.serializable(),
            owner = owner.serializable(),
            instant = instant,
            edited = edited,
        )
}
