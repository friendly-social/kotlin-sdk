package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class LoginCodeSerializable(public val int: Int) {
    init {
        if (int !in LoginCode.Min..LoginCode.Max) {
            throw SerializationException("Codes are exactly 8 digits long")
        }
    }

    public fun typed(): LoginCode = LoginCode.orThrow(int)
}
