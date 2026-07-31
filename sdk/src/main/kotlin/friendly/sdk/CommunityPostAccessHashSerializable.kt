package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class CommunityPostAccessHashSerializable(
    public val string: String,
) {
    init {
        if (string.length != CommunityPostAccessHash.Length) {
            throw SerializationException(
                "CommunityPostAccessHash is supposed to have a length of ${CommunityPostAccessHash.Length}, but was ${string.length}",
            )
        }
    }

    public fun typed(): CommunityPostAccessHash =
        CommunityPostAccessHash.orThrow(string)
}
