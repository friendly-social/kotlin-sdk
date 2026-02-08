package friendly.sdk.examples

import friendly.sdk.Email
import friendly.sdk.EmailCode
import friendly.sdk.Interest
import friendly.sdk.InterestList
import friendly.sdk.Nickname
import friendly.sdk.UserDescription
import kotlin.random.Random

suspend fun emailExample() {
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
    val linkSuccess = client.email.link(
        authorization = authorization,
        email = Email.orThrow("friendly-sdk-test${Random.nextInt()}@y9san9.me"),
    ).orThrow()
    println("=== Link Success ===")
    println(linkSuccess)
    println()
    print("Enter code from email: ")
    val code = EmailCode.orThrow(readln().toInt())
    val confirmSuccess = client.email.confirm(authorization, code).orThrow()
    println("=== Confirm Success ===")
    println(confirmSuccess)
    println()
    val unlinkSuccess = client.email.unlink(authorization).orThrow()
    println("=== Unlink Success ===")
    println(unlinkSuccess)
    println()
}
