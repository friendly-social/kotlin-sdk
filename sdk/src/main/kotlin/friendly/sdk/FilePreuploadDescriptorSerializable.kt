package friendly.sdk

import kotlinx.serialization.Serializable

@Serializable
public data class FilePreuploadDescriptorSerializable(
    val id: FilePreuploadIdSerializable,
    val accessHash: FilePreuploadAccessHashSerializable,
) {
    public fun typed(): FilePreuploadDescriptor =
        FilePreuploadDescriptor(id.typed(), accessHash.typed())
}
