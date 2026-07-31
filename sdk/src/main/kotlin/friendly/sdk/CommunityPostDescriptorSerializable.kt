package friendly.sdk

import kotlinx.serialization.Serializable

@Serializable
public data class CommunityPostDescriptorSerializable(
    val id: CommunityPostIdSerializable,
    val accessHash: CommunityPostAccessHashSerializable,
) {
    public fun typed(): CommunityPostDescriptor = CommunityPostDescriptor(
        id = id.typed(),
        accessHash = accessHash.typed(),
    )
}
