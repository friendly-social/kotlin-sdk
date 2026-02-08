package friendly.sdk

public data class Nickname private constructor(val string: String) {
    public fun serializable(): NicknameSerializable =
        NicknameSerializable(string)

    public companion object {
        public val MaxLength: Int = 256

        public fun validate(string: String): Boolean =
            string.isNotBlank() && string.length <= MaxLength

        public fun orNull(string: String): Nickname? {
            if (validate(string)) return orThrow(string)
            return null
        }

        public fun orThrow(string: String): Nickname {
            require(string.length <= MaxLength)
            require(string.isNotBlank())
            return Nickname(string)
        }
    }
}
