package friendly.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@JvmInline
@Serializable
public value class FilePreuploadAccessHashSerializable(
    public val string: String,
) {
    init {
        if (string.length != FilePreuploadAccessHash.Length) {
            throw SerializationException(
                "FilePreuploadAccessHash is supposed to have a length of ${FilePreuploadAccessHash.Length}, but was ${string.length}",
            )
        }
    }

    public fun typed(): FilePreuploadAccessHash =
        FilePreuploadAccessHash.orThrow(string)
}
