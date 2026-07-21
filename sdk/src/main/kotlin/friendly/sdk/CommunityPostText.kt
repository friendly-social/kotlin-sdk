package friendly.sdk

public data class CommunityPostText private constructor(val string: String) {
    public fun serializable(): CommunityPostTextSerializable =
        CommunityPostTextSerializable(string)

    public companion object {
        public val MaxLength: Int = 4096

        public fun orThrow(string: String): CommunityPostText {
            require(string.length < MaxLength) {
                "Community post can't be longer than $MaxLength"
            }
            return CommunityPostText(string)
        }
    }
}
