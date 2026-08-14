package friendly.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public sealed interface ActivityDetailsSerializable {
    public val id: ActivityIdSerializable

    public fun typed(): ActivityDetails

    @Serializable
    @SerialName("reply")
    public data class Reply(
        override val id: ActivityIdSerializable,
        val post: CommunityPostDetailsSerializable,
    ) : ActivityDetailsSerializable {
        override fun typed(): ActivityDetails = ActivityDetails.Reply(
            id = id.typed(),
            post = post.typed(),
        )
    }
}
