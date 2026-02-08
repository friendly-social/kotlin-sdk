package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class EmailSerializable(public val string: String) {
    init {
        if ("@" !in string) {
            throw SerializationException("Email should contain @ symbol")
        }
        if ("." !in string) {
            throw SerializationException("Email should contain . symbol")
        }
        if (string.length > Email.MaxLength) {
            throw SerializationException(
                "Email should not be more than 2048 symbols",
            )
        }
    }

    public fun typed(): Email = Email.orThrow(string)
}
