package friendly.sdk

public data class CursorId(val string: String) {
    public fun serializable(): CursorIdSerializable =
        CursorIdSerializable(string)
}
