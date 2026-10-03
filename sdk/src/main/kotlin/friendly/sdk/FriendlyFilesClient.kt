package friendly.sdk

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.content.ProgressListener
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import kotlinx.io.Sink

public class FriendlyFilesClient(
    endpoint: FriendlyEndpoint,
    private val httpClient: HttpClient,
) {
    private val endpoint = endpoint / "files"

    public fun getEndpoint(descriptor: FileDescriptor): FriendlyEndpoint =
        endpoint /
            "download" /
            "${descriptor.id.long}" /
            descriptor.accessHash.string

    public sealed interface PreuploadFileResult {
        public fun orThrow(): FilePreuploadDescriptor

        public data class IOError(val cause: Exception) : PreuploadFileResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : PreuploadFileResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val descriptor: FilePreuploadDescriptor) :
            PreuploadFileResult {
            override fun orThrow(): FilePreuploadDescriptor = descriptor
        }
    }

    public suspend fun preupload(
        filename: String,
        size: Long,
        contentType: ContentType? = null,
        onUpload: ProgressListener? = null,
        bodyBuilder: Sink.() -> Unit,
    ): PreuploadFileResult {
        val endpoint = endpoint / "preupload"
        val requestBody = MultiPartFormDataContent(
            formData {
                append(
                    key = "file",
                    filename = filename,
                    contentType = contentType,
                    size = size,
                    bodyBuilder = bodyBuilder,
                )
            },
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            setBody(requestBody)
            header("X-File-Size", size)
            if (onUpload != null) {
                onUpload(onUpload)
            }
        }
        val response = when (request) {
            is IOError -> return PreuploadFileResult.IOError(request.cause)
            is ServerError -> return PreuploadFileResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            OK -> response.body<FilePreuploadDescriptorSerializable>()
            else -> error("Unknown status code")
        }
        val descriptor = responseBody.typed()
        return PreuploadFileResult.Success(descriptor)
    }

    public sealed interface UploadFileResult {
        public fun orThrow(): FileDescriptor

        public data class IOError(val cause: Exception) : UploadFileResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data object ServerError : UploadFileResult {
            override fun orThrow(): Nothing = error("$this")
        }
        public data class Success(val descriptor: FileDescriptor) :
            UploadFileResult {
            override fun orThrow(): FileDescriptor = descriptor
        }
    }

    public suspend fun upload(
        authorization: Authorization,
        filename: String,
        size: Long,
        contentType: ContentType? = null,
        onUpload: ProgressListener? = null,
        bodyBuilder: Sink.() -> Unit,
    ): UploadFileResult {
        val endpoint = endpoint / "upload"
        val requestBody = MultiPartFormDataContent(
            formData {
                append(
                    key = "file",
                    filename = filename,
                    contentType = contentType,
                    size = size,
                    bodyBuilder = bodyBuilder,
                )
            },
        )
        val request = httpClient.safeHttpRequest(endpoint.string) {
            method = Post
            authorization(authorization)
            header("X-File-Size", size)
            setBody(requestBody)
            if (onUpload != null) {
                onUpload(onUpload)
            }
        }
        val response = when (request) {
            is IOError -> return UploadFileResult.IOError(request.cause)
            is ServerError -> return UploadFileResult.ServerError
            is Success -> request.response
        }
        val responseBody = when (response.status) {
            OK -> response.body<FileDescriptorSerializable>()
            else -> error("Unknown status code")
        }
        val descriptor = responseBody.typed()
        return UploadFileResult.Success(descriptor)
    }
}
