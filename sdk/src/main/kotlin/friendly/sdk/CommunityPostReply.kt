package friendly.sdk

public sealed interface CommunityPostReply {
    public fun serializable(): CommunityPostReplySerializable

    public data class Single(val post: CommunityPostDetails) :
        CommunityPostReply {
        override fun serializable(): CommunityPostReplySerializable.Single =
            CommunityPostReplySerializable.Single(
                post = post.serializable(),
            )
    }

    public data class Thread(val thread: List<CommunityPostDetails>) :
        CommunityPostReply {
        override fun serializable(): CommunityPostReplySerializable.Thread =
            CommunityPostReplySerializable.Thread(
                thread = thread.map { post -> post.serializable() },
            )
    }
}
