package friendly.sdk

import kotlin.time.Instant

public sealed interface ActivityDetails {
    public val id: ActivityId
    public val instant: Instant

    public fun serializable(): ActivityDetailsSerializable

    public data class Reply(
        override val id: ActivityId,
        override val instant: Instant,
        val post: CommunityPostDetails,
    ) : ActivityDetails {
        override fun serializable(): ActivityDetailsSerializable =
            ActivityDetailsSerializable.Reply(
                id = id.serializable(),
                instant = instant,
                post = post.serializable(),
            )
    }
}
