package friendly.sdk

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
public data class CommunityPostSerializable(
    val id: CommunityPostIdSerializable,
    val accessHash: CommunityPostAccessHashSerializable,
    val text: CommunityPostTextSerializable,
    val owner: UserDetailsSerializable,
    val instant: Instant,
    val edited: Boolean,
) {
    public fun typed(): CommunityPost = CommunityPost(
        id = id.typed(),
        accessHash = accessHash.typed(),
        text = text.typed(),
        owner = owner.typed(),
        instant = instant,
        edited = edited,
    )
}
