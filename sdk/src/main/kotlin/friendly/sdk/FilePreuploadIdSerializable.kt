package friendly.sdk

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class FilePreuploadIdSerializable(public val long: Long) {
    public fun typed(): FilePreuploadId = FilePreuploadId(long)
}
