package friendly.sdk

import kotlin.time.Instant

public sealed interface CommunityPostDetails {
    public val id: CommunityPostId
    public val accessHash: CommunityPostAccessHash
    public val instant: Instant

    public val descriptor: CommunityPostDescriptor get() =
        CommunityPostDescriptor(id, accessHash)

    public fun serializable(): CommunityPostDetailsSerializable

    public data class Plain(
        override val id: CommunityPostId,
        override val accessHash: CommunityPostAccessHash,
        override val instant: Instant,
        val text: CommunityPostText,
        val owner: UserDetails,
        val edited: Boolean,
    ) : CommunityPostDetails {
        override fun serializable(): CommunityPostDetailsSerializable.Plain =
            CommunityPostDetailsSerializable.Plain(
                id = id.serializable(),
                accessHash = accessHash.serializable(),
                instant = instant,
                text = text.serializable(),
                owner = owner.serializable(),
                edited = edited,
            )
    }

    public data class Deleted(
        override val id: CommunityPostId,
        override val accessHash: CommunityPostAccessHash,
        override val instant: Instant,
    ) : CommunityPostDetails {
        override fun serializable(): CommunityPostDetailsSerializable.Deleted =
            CommunityPostDetailsSerializable.Deleted(
                id = id.serializable(),
                accessHash = accessHash.serializable(),
                instant = instant,
            )
    }
}
