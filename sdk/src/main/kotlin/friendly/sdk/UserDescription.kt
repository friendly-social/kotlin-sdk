package friendly.sdk

public data class UserDescription private constructor(val string: String) {
    public fun serializable(): UserDescriptionSerializable =
        UserDescriptionSerializable(string)

    public companion object {
        public val MaxLength: Int = 1_024

        public fun validate(string: String): Boolean =
            string.isNotBlank() && string.length <= MaxLength

        public fun orNull(string: String): UserDescription? {
            if (validate(string)) return orThrow(string)
            return null
        }

        public fun orThrow(string: String): UserDescription {
            require(string.length <= MaxLength)
            require(string.isNotBlank())
            return UserDescription(string)
        }
    }
}
