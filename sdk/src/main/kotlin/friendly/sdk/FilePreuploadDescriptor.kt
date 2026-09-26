package friendly.sdk

public data class FilePreuploadDescriptor(
    val id: FilePreuploadId,
    val accessHash: FilePreuploadAccessHash,
) {
    public fun serializable(): FilePreuploadDescriptorSerializable =
        FilePreuploadDescriptorSerializable(
            id = id.serializable(),
            accessHash = accessHash.serializable(),
        )
}
