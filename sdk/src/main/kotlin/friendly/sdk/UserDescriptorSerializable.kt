package friendly.sdk

public data class UserDescriptorSerializable(
    val id: UserIdSerializable,
    val accessHash: UserAccessHashSerializable,
) {
    public fun typed(): UserDescriptor = UserDescriptor(
        id = id.typed(),
        accessHash = accessHash.typed(),
    )
}
