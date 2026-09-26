package friendly.sdk.examples

import friendly.sdk.InterestList
import friendly.sdk.Nickname
import friendly.sdk.UserDescription
import io.ktor.http.ContentType
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

// This example only works in production
suspend fun filesExample() {
    val fs = SystemFileSystem
    val path = fs.resolve(Path("assets/image.webp"))
    val metadata = fs.metadataOrNull(path) ?: error("Can't access assets")

    val avatar = client.files.preupload(
        filename = "image.webp",
        contentType = ContentType.Image.WEBP,
        size = metadata.size,
        onUpload = { sent, total ->
            val percent = "%.0f".format(
                sent.toDouble() / metadata.size * 100,
            )
            println("Transferring: $sent of $total ($percent%)")
        },
    ) {
        fs.source(path)
            .buffered()
            .transferTo(this)
    }.orThrow()

    val authorization = client.auth.generate(
        nickname = Nickname.orThrow("files"),
        description = UserDescription.orThrow("test"),
        interests = InterestList.orThrow(),
        avatar = avatar,
        socialLink = null,
    ).orThrow()

    val details = client.users.details2(authorization).orThrow()
    require(details.user.avatar?.accessHash == avatar.accessHash)

    val fileDescriptor = client.files.upload(
        authorization = authorization,
        filename = "image.webp",
        contentType = ContentType.Image.WEBP,
        size = metadata.size,
        onUpload = { sent, total ->
            val percent = "%.0f".format(
                sent.toDouble() / metadata.size * 100,
            )
            println("Transferring: $sent of $total ($percent%)")
        },
    ) {
        fs.source(path)
            .buffered()
            .transferTo(this)
    }.orThrow()

    val url = client.files.getEndpoint(fileDescriptor)
    println("File uploaded to: ${url.string}")
}
