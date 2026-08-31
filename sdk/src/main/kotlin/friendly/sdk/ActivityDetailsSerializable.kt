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
    @SerialName("reply")
    public data class Reply(
        override val id: ActivityIdSerializable,
        override val instant: Instant,
        override val isRead: Boolean,
        val post: CommunityPostDetailsSerializable.Plain,
    ) : ActivityDetailsSerializable {
        override fun typed(): ActivityDetails = ActivityDetails.Reply(
            id = id.typed(),
            instant = instant,
            isRead = isRead,
            post = post.typed(),
        )
    }
}
