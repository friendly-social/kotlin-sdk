package friendly.sdk

public data class Cursor<out T>(val data: List<T>, val nextId: CursorId?) {
    public inline fun <R> serializable(block: (T) -> R): CursorSerializable<R> =
        CursorSerializable(
            data = data.map { element -> block(element) },
            nextId = nextId?.serializable(),
        )
}
