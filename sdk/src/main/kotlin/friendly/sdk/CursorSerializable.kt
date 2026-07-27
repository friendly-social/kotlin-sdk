package friendly.sdk

import kotlinx.serialization.Serializable

@Serializable
public data class CursorSerializable<out T>(
    val data: List<T>,
    val nextId: CursorIdSerializable?,
) {
    public inline fun <R> typed(block: (T) -> R): Cursor<R> = Cursor(
        data = data.map { element -> block(element) },
        nextId = nextId?.typed(),
    )
}
