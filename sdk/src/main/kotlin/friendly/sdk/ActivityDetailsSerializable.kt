package friendly.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
public sealed interface ActivityDetailsSerializable {
    public val id: ActivityIdSerializable
    public val instant: Instant

    public fun typed(): ActivityDetails

    @Serializable
    @SerialName("reply")
    public data class Reply(
        override val id: ActivityIdSerializable,
        override val instant: Instant,
        val post: CommunityPostDetailsSerializable.Plain,
    ) : ActivityDetailsSerializable {
        override fun typed(): ActivityDetails = ActivityDetails.Reply(
            id = id.typed(),
            instant = instant,
            post = post.typed(),
        )
    }
}
