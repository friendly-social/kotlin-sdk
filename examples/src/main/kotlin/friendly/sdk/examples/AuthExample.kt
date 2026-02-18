package friendly.sdk.examples

import friendly.sdk.ConfirmationCode
import friendly.sdk.Email
import friendly.sdk.Interest
import friendly.sdk.InterestList
import friendly.sdk.LocaleCode
import friendly.sdk.LoginCode
import friendly.sdk.Nickname
import friendly.sdk.UserDescription
import kotlin.random.Random

suspend fun authExample() {
    val authorization = client.auth.generate(
        nickname = Nickname.orThrow("y9san9"),
        description = UserDescription.orThrow("Phronology Evangelist"),
        interests = InterestList.orThrow(
            Interest.orThrow("phronology"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization ===")
    println(authorization)
    println()
    val email = Email.orThrow("friendly-sdk-test${Random.nextInt()}@y9san9.me")
    val linkSuccess = client.email.link(
        authorization = authorization,
        email = email,
        localeCode = LocaleCode.En,
    ).orThrow()
    println("=== Link Success ===")
    println(linkSuccess)
    println()
    print("Enter confirmation code from email: ")
    val confirmationCode = ConfirmationCode.orThrow(readln().toInt())
    val confirmSuccess = client.email.confirm(
        authorization = authorization,
        code = confirmationCode,
    ).orThrow()
    println("=== Confirm Success ===")
    println(confirmSuccess)
    println()
    val emailAuthSuccess = client.auth.email(email, LocaleCode.En).orThrow()
    println("=== Email Auth Success ===")
    println(emailAuthSuccess)
    println()
    print("Enter auth code from email: ")
    val authCode = LoginCode.orThrow(readln().toInt())
    val loginSuccess = client.auth.login(email, authCode).orThrow()
    println("=== Login Success ===")
    println(loginSuccess)
    println()
}
