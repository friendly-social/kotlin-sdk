package friendly.sdk

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class CommunityPostIdSerializable(public val long: Long) {
    public fun typed(): CommunityPostId = CommunityPostId(long)
}
