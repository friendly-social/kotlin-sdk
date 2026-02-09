package friendly.sdk

public data class LoginCode private constructor(val int: Int) {
    public fun serializable(): LoginCodeSerializable =
        LoginCodeSerializable(int)

    public companion object {
        public val Min: Int = 10_000_000
        public val Max: Int = 99_999_999

        public fun orThrow(int: Int): LoginCode {
            require(int in Min..Max) {
                "Codes are exactly 8 digits long"
            }
            return LoginCode(int)
        }
    }
}
