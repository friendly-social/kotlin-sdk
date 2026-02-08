package friendly.sdk

public data class Interest private constructor(val string: String) {
    public fun serializable(): InterestSerializable =
        InterestSerializable(string)

    public companion object {
        public val MaxLength: Int = 64

        public fun validate(string: String): Boolean =
            string.isNotEmpty() && string.length <= MaxLength

        public fun orNull(string: String): Interest? {
            if (validate(string)) return orThrow(string)
            return null
        }

        public fun orThrow(string: String): Interest {
            require(string.length <= MaxLength)
            require(string.isNotBlank())
            return Interest(string)
        }
    }
}
