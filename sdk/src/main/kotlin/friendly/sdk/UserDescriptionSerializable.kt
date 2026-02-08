package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class UserDescriptionSerializable(public val string: String) {
    init {
        if (string.length > UserDescription.MaxLength) {
            throw SerializationException(
                "User description length must not exceed ${UserDescription.MaxLength}, was ${string.length}",
            )
        }
        if (string.isBlank()) {
            throw SerializationException("User description cannot be blank")
        }
    }

    public fun typed(): UserDescription = UserDescription.orThrow(string)
}
