package friendly.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
public sealed interface CommunityPostDetailsSerializable {
    public val id: CommunityPostIdSerializable
    public val accessHash: CommunityPostAccessHashSerializable
    public val instant: Instant

    public fun typed(): CommunityPostDetails

    @Serializable
    @SerialName("plain")
    public data class Plain(
        override val id: CommunityPostIdSerializable,
        override val accessHash: CommunityPostAccessHashSerializable,
        override val instant: Instant,
        val text: CommunityPostTextSerializable,
        val owner: UserDetailsSerializable,
        val edited: Boolean,
    ) : CommunityPostDetailsSerializable {
        override fun typed(): CommunityPostDetails.Plain =
            CommunityPostDetails.Plain(
                id = id.typed(),
                accessHash = accessHash.typed(),
                instant = instant,
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
    ) : CommunityPostDetailsSerializable {
        override fun typed(): CommunityPostDetails.Deleted =
            CommunityPostDetails.Deleted(
                id = id.typed(),
                accessHash = accessHash.typed(),
                instant = instant,
            )
    }
}
