package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class CommunityPostTextSerializable(public val string: String) {
    init {
        if (string.length > CommunityPostText.MaxLength) {
            throw SerializationException(
                "Post cannot be longer than ${CommunityPostText.MaxLength}",
            )
        }
    }

    public fun typed(): CommunityPostText = CommunityPostText.orThrow(string)
}
