package friendly.sdk

public data class FilePreuploadAccessHash private constructor(
    val string: String,
) {

    public fun serializable(): FilePreuploadAccessHashSerializable =
        FilePreuploadAccessHashSerializable(string)

    public companion object {
        public val Length: Int = 256

        public fun orThrow(string: String): FilePreuploadAccessHash {
            require(string.length == Length) {
                "Token should have $Length length, but was ${string.length}"
            }
            return FilePreuploadAccessHash(string)
        }
    }
}
