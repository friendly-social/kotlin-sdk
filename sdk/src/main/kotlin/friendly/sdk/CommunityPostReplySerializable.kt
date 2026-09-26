package friendly.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public sealed interface CommunityPostReplySerializable {
    public fun typed(): CommunityPostReply

    @SerialName("single")
    @Serializable
    public data class Single(val post: CommunityPostDetailsSerializable) :
        CommunityPostReplySerializable {
        override fun typed(): CommunityPostReply.Single =
            CommunityPostReply.Single(
                post = post.typed(),
            )
    }

    @SerialName("thread")
    @Serializable
    public data class Thread(
        val thread: List<CommunityPostDetailsSerializable>,
    ) : CommunityPostReplySerializable {
        override fun typed(): CommunityPostReply.Thread =
            CommunityPostReply.Thread(
                thread = thread.map { post -> post.typed() },
            )
    }
}
