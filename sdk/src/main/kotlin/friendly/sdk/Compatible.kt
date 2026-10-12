package friendly.sdk

import io.ktor.client.request.HttpRequestBuilder

@JvmInline
public value class Compatible(public val int: Int) {
    public companion object {
        public val Latest: Compatible = Compatible(2)
    }
}

internal fun HttpRequestBuilder.compatible() {
    headers["X-Compatible"] = Compatible.Latest.int.toString()
}
