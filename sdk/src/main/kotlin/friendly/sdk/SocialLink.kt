package friendly.sdk

public data class SocialLink private constructor(val string: String) {
    public fun serializable(): SocialLinkSerializable =
        SocialLinkSerializable(string)

    public companion object {
        public val MaxLength: Int = 2048

        public fun orThrow(string: String): SocialLink {
            require(string.isNotBlank())
            require(string.length <= MaxLength)
            return SocialLink(string)
        }
    }
}
