package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class ConfirmationCodeSerializable(public val int: Int) {
    init {
        if (int !in ConfirmationCode.Min..ConfirmationCode.Max) {
            throw SerializationException("Codes are exactly 8 digits long")
        }
    }

    public fun typed(): ConfirmationCode = ConfirmationCode.orThrow(int)
}
