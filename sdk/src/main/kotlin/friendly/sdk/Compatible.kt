package friendly.sdk

import io.ktor.client.plugins.DefaultRequest.DefaultRequestBuilder

@JvmInline
public value class Compatible(public val int: Int) {
    public companion object {
        public val Latest: Compatible = Compatible(2)
    }
}

internal fun DefaultRequestBuilder.compatible() {
    headers["X-Compatible"] = Compatible.Latest.int.toString()
}
