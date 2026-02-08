package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class SocialLinkSerializable(public val string: String) {
    init {
        if (string.length > SocialLink.MaxLength) {
            throw SerializationException(
                "Nickname length is ${string.length}, but should be less than ${SocialLink.MaxLength}",
            )
        }
        if (string.isBlank()) {
            throw SerializationException("Social link cannot be blank")
        }
    }

    public fun typed(): SocialLink = SocialLink.orThrow(string)
}
