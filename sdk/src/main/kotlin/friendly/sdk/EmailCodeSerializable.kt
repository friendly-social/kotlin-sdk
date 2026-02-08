package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class EmailCodeSerializable(public val int: Int) {
    init {
        if (int !in EmailCode.Min..EmailCode.Max) {
            throw SerializationException("Codes are exactly 8 digits long")
        }
    }

    public fun typed(): EmailCode = EmailCode.orThrow(int)
}
