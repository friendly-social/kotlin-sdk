package friendly.sdk

public data class ActivityId(val long: Long) {
    public fun serializable(): ActivityIdSerializable =
        ActivityIdSerializable(long)
}
