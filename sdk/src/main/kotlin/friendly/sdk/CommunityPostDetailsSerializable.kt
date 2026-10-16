package friendly.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
public sealed interface CommunityPostDetailsSerializable {
    public val id: CommunityPostIdSerializable
    public val accessHash: CommunityPostAccessHashSerializable
    public val instant: Instant
    public val replyPreviews: List<UserDetailsSerializable>
    public val newUnreadId: CommunityPostIdSerializable?

    public fun typed(): CommunityPostDetails

    @Serializable
    @SerialName("plain")
    public data class Plain(
        override val id: CommunityPostIdSerializable,
        override val accessHash: CommunityPostAccessHashSerializable,
        override val instant: Instant,
        override val replyPreviews: List<UserDetailsSerializable>,
        override val newUnreadId: CommunityPostIdSerializable?,
        val text: CommunityPostTextSerializable,
        val owner: UserDetailsSerializable,
        val edited: Boolean,
    ) : CommunityPostDetailsSerializable {
        override fun typed(): CommunityPostDetails.Plain =
            CommunityPostDetails.Plain(
                id = id.typed(),
                accessHash = accessHash.typed(),
                instant = instant,
                replyPreviews = replyPreviews.map { preview ->
                    preview.typed()
                },
                newUnreadId = newUnreadId?.typed(),
                text = text.typed(),
                owner = owner.typed(),
                edited = edited,
            )
    }

    @Serializable
    @SerialName("deleted")
    public data class Deleted(
        override val id: CommunityPostIdSerializable,
        override val accessHash: CommunityPostAccessHashSerializable,
        override val instant: Instant,
        override val replyPreviews: List<UserDetailsSerializable>,
        override val newUnreadId: CommunityPostIdSerializable?,
    ) : CommunityPostDetailsSerializable {
        override fun typed(): CommunityPostDetails.Deleted =
            CommunityPostDetails.Deleted(
                id = id.typed(),
                accessHash = accessHash.typed(),
                instant = instant,
                replyPreviews = replyPreviews.map { preview ->
                    preview.typed()
                },
                newUnreadId = newUnreadId?.typed(),
            )
    }
}
