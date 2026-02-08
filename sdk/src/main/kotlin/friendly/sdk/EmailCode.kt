package friendly.sdk

public data class EmailCode private constructor(val int: Int) {
    public fun serializable(): EmailCodeSerializable =
        EmailCodeSerializable(int)

    public companion object {
        public val Min: Int = 10_000_000
        public val Max: Int = 99_999_999

        public fun orThrow(int: Int): EmailCode {
            require(int in Min..Max) {
                "Codes are exactly 8 digits long"
            }
            return EmailCode(int)
        }
    }
}
