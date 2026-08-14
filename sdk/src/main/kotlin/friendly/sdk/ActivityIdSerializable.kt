package friendly.sdk

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class ActivityIdSerializable(public val long: Long) {
    public fun typed(): ActivityId = ActivityId(long)
}
