package friendly.sdk

public data class UserDescriptor(
    val id: UserId,
    val accessHash: UserAccessHash,
) {
    public fun serializable(): UserDescriptorSerializable =
        UserDescriptorSerializable(
            id = id.serializable(),
            accessHash = accessHash.serializable(),
        )
}
