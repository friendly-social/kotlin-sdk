package friendly.sdk

public data class FilePreuploadId(public val long: Long) {
    public fun serializable(): FilePreuploadIdSerializable =
        FilePreuploadIdSerializable(long)
}
