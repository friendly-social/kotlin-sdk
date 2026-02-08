package friendly.sdk

public data class Email private constructor(val string: String) {
    public fun serializable(): EmailSerializable = EmailSerializable(string)

    public companion object {
        public val MaxLength: Int = 2048

        /**
         * You know what? I don't care. Like, really. After reading countless
         * atricles about how no one is validating email, I will not even try to
         * do that. Got @, got .? Congrats, that's an email. You will need to
         * receive a message there tho, but it's not my business.
         */
        public fun orThrow(string: String): Email {
            require(string.length <= MaxLength) {
                "We can't store emails that have more than 2048 chars"
            }
            require("@" in string) {
                "Email must contain @ symbol"
            }
            require("." in string) {
                "Email must contain at least one . symbol"
            }
            return Email(string)
        }
    }
}
