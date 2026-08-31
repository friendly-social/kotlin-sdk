package friendly.sdk

import kotlin.time.Instant

public sealed interface ActivityDetails {
    public val id: ActivityId
    public val instant: Instant
    public val isRead: Boolean

    public fun serializable(): ActivityDetailsSerializable

    public data class Reply(
        override val id: ActivityId,
        override val instant: Instant,
        override val isRead: Boolean,
        val post: CommunityPostDetails.Plain,
    ) : ActivityDetails {
        override fun serializable(): ActivityDetailsSerializable =
            ActivityDetailsSerializable.Reply(
                id = id.serializable(),
                instant = instant,
                isRead = isRead,
                post = post.serializable(),
            )
    }
}
