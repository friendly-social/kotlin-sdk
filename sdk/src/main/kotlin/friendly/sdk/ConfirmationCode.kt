package friendly.sdk

public data class ConfirmationCode private constructor(val int: Int) {
    public fun serializable(): ConfirmationCodeSerializable =
        ConfirmationCodeSerializable(int)

    public companion object {
        public val Min: Int = 10_000_000
        public val Max: Int = 99_999_999

        public fun orThrow(int: Int): ConfirmationCode {
            require(int in Min..Max) {
                "Codes are exactly 8 digits long"
            }
            return ConfirmationCode(int)
        }
    }
}
