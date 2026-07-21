package friendly.sdk

public data class CommunityPostId(val long: Long) {
    public fun serializable(): CommunityPostIdSerializable =
        CommunityPostIdSerializable(long)
}
