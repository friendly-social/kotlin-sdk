package friendly.sdk

public data class InterestList private constructor(val raw: List<Interest>) {
    public fun serializable(): InterestListSerializable =
        InterestListSerializable(
            raw = raw.map(Interest::serializable),
        )

    public companion object {
        public val MaxSize: Int = 100

        public val Empty: InterestList = orThrow(emptyList())

        public fun orThrow(vararg raw: Interest): InterestList =
            orThrow(raw.toList())

        public fun orThrow(raw: List<Interest>): InterestList {
            require(raw.size <= MaxSize) {
                "You cannot pick more than 100 interests"
            }
            require(raw.toSet().size == raw.size) {
                "A list of interests must be unique"
            }
            return InterestList(raw)
        }
    }
}
