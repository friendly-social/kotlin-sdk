package friendly.sdk

import kotlin.time.Instant

public sealed interface ActivityDetails {
    public val id: ActivityId
    public val instant: Instant
    public val isRead: Boolean

    public fun serializable(): ActivityDetailsSerializable

    public data class NewReply(
        override val id: ActivityId,
        override val instant: Instant,
        override val isRead: Boolean,
        val post: CommunityPostDetails.Plain,
    ) : ActivityDetails {
        override fun serializable(): ActivityDetailsSerializable =
            ActivityDetailsSerializable.NewReply(
                id = id.serializable(),
                instant = instant,
                isRead = isRead,
                post = post.serializable(),
            )
    }

    public data class IndirectReplies(
        override val id: ActivityId,
        override val instant: Instant,
        override val isRead: Boolean,
        val post: CommunityPostDetails.Plain,
    ) : ActivityDetails {
        override fun serializable(): ActivityDetailsSerializable =
            ActivityDetailsSerializable.IndirectReplies(
                id = id.serializable(),
                instant = instant,
                isRead = isRead,
                post = post.serializable(),
            )
    }

    public data class NewRequest(
        override val id: ActivityId,
        override val instant: Instant,
        override val isRead: Boolean,
        public val from: UserDetails,
        public val isMutual: Boolean,
    ) : ActivityDetails {
        override fun serializable(): ActivityDetailsSerializable =
            ActivityDetailsSerializable.NewRequest(
                id = id.serializable(),
                instant = instant,
                isRead = isRead,
                from = from.serializable(),
                isMutual = isMutual,
            )
    }
}
