package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class NicknameSerializable(public val string: String) {
    init {
        if (string.length > Nickname.MaxLength) {
            throw SerializationException(
                "Nickname length is ${string.length}, but should be less than ${Nickname.MaxLength}",
            )
        }
        if (string.isBlank()) {
            throw SerializationException("Nickname cannot be blank")
        }
    }

    public fun typed(): Nickname = Nickname.orThrow(string)
}
