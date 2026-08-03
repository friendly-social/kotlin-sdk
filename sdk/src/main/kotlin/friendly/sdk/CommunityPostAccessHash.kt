package friendly.sdk

public data class CommunityPostAccessHash private constructor(
    val string: String,
) {

    public fun serializable(): CommunityPostAccessHashSerializable =
        CommunityPostAccessHashSerializable(string)

    public companion object {
        public val Length: Int = 256

        public fun orThrow(string: String): CommunityPostAccessHash {
            require(string.length == Length) {
                "Token should have $Length length, but was ${string.length}"
            }
            return CommunityPostAccessHash(string)
        }
    }
}
