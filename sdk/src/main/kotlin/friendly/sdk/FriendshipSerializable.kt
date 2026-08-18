package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
@JvmInline
public value class FriendshipSerializable(public val string: String) {
    init {
        typedOr { string ->
            throw SerializationException("Unknown friendship status '$string'")
        }
    }

    public inline fun typedOr(block: (String) -> Friendship): Friendship =
        when (string) {
            "friends" -> Friends
            "incomingRequest" -> IncomingRequest
            "outgoingRequest" -> OutgoingRequest
            "outgoingDecline" -> OutgoingDecline
            "none" -> None
            else -> block(string)
        }

    public fun typed(): Friendship = typedOr { string ->
        error("Unknwon friendship status '$string'")
    }
}
