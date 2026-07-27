package friendly.sdk

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class CursorIdSerializable(public val string: String) {
    public fun typed(): CursorId = CursorId(string)
}
