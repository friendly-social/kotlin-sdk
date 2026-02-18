package friendly.sdk

import io.ktor.client.request.HttpRequestBuilder

public sealed interface LocaleCode {
    public data object En : LocaleCode
    public data object Ru : LocaleCode
}

internal fun HttpRequestBuilder.localeCode(localeCode: LocaleCode) {
    headers["X-Locale"] = when (localeCode) {
        En -> "en"
        Ru -> "ru"
    }
}
