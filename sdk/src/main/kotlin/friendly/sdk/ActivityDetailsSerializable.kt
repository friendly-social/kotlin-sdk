package friendly.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
public sealed interface ActivityDetailsSerializable {
    public val id: ActivityIdSerializable
    public val instant: Instant
    public val isRead: Boolean

    public fun typed(): ActivityDetails

    @Serializable
    @SerialName("new_reply")
    public data class NewReply(
        override val id: ActivityIdSerializable,
        override val instant: Instant,
        override val isRead: Boolean,
        val post: CommunityPostDetailsSerializable.Plain,
    ) : ActivityDetailsSerializable {
        override fun typed(): ActivityDetails = ActivityDetails.NewReply(
            id = id.typed(),
            instant = instant,
            isRead = isRead,
            post = post.typed(),
        )
    }

    @Serializable
    @SerialName("indirect_replies")
    public data class IndirectReplies(
        override val id: ActivityIdSerializable,
        override val instant: Instant,
        override val isRead: Boolean,
        val post: CommunityPostDetailsSerializable.Plain,
    ) : ActivityDetailsSerializable {
        override fun typed(): ActivityDetails = ActivityDetails.IndirectReplies(
            id = id.typed(),
            instant = instant,
            isRead = isRead,
            post = post.typed(),
        )
    }

    @Serializable
    @SerialName("new_request")
    public data class NewRequest(
        override val id: ActivityIdSerializable,
        override val instant: Instant,
        override val isRead: Boolean,
        public val from: UserDetailsSerializable,
        public val isMutual: Boolean,
    ) : ActivityDetailsSerializable {
        override fun typed(): ActivityDetails = ActivityDetails.NewRequest(
            id = id.typed(),
            instant = instant,
            isRead = isRead,
            from = from.typed(),
            isMutual = isMutual,
        )
    }
}
