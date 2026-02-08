package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@JvmInline
@Serializable
public value class InterestSerializable(public val string: String) {
    init {
        if (string.length > Interest.MaxLength) {
            throw SerializationException(
                "Interest length might not be more than ${Interest.MaxLength}, was ${string.length}",
            )
        }
        if (string.isBlank()) {
            throw SerializationException("Interest cannot be blank")
        }
    }

    public fun typed(): Interest = Interest.orThrow(string)
}

public fun List<InterestSerializable>.typed(): List<Interest> =
    map(InterestSerializable::typed)
