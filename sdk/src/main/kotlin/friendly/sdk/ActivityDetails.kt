package friendly.sdk

public sealed interface ActivityDetails {
    public val id: ActivityId

    public fun serializable(): ActivityDetailsSerializable

    public data class Reply(
        override val id: ActivityId,
        val post: CommunityPostDetails,
    ) : ActivityDetails {
        override fun serializable(): ActivityDetailsSerializable =
            ActivityDetailsSerializable.Reply(
                id = id.serializable(),
                post = post.serializable(),
            )
    }
}
