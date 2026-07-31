package friendly.sdk

public data class CommunityPostDescriptor(
    val id: CommunityPostId,
    val accessHash: CommunityPostAccessHash,
) {
    public fun serializable(): CommunityPostDescriptorSerializable =
        CommunityPostDescriptorSerializable(
            id = id.serializable(),
            accessHash = accessHash.serializable(),
        )
}
