package friendly.sdk.examples

import friendly.sdk.Interest
import friendly.sdk.InterestList
import friendly.sdk.Nickname
import friendly.sdk.UserDescription

suspend fun feedExample() {
    println()
    val authorization1 = client.auth.generate(
        nickname = Nickname.orThrow("y9san9"),
        description = UserDescription.orThrow("Phronology Evangelist"),
        interests = InterestList.orThrow(
            Interest.orThrow("phronology"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization 1 ===")
    println(authorization1)
    println()
    val authorization2 = client.auth.generate(
        nickname = Nickname.orThrow("y9demn"),
        description = UserDescription.orThrow("Zed Enjoyer"),
        interests = InterestList.orThrow(
            Interest.orThrow("zed"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization 2 ===")
    println(authorization2)
    println()
    val authorization3 = client.auth.generate(
        nickname = Nickname.orThrow("y9kap"),
        description = UserDescription.orThrow("Senior Python Developer"),
        interests = InterestList.orThrow(
            Interest.orThrow("python3+"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization 3 ===")
    println(authorization3)
    println()
    val authorization4 = client.auth.generate(
        nickname = Nickname.orThrow("otomir23"),
        description = UserDescription.orThrow("Webring Master"),
        interests = InterestList.orThrow(
            Interest.orThrow("webring"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization 4 ===")
    println(authorization4)
    println()
    val friend1Token = client.friends.generate(authorization1).orThrow()
    println("=== Friend 1 Token ===")
    println(friend1Token)
    println()
    val add1ResultSuccess = client.friends
        .add(authorization2, friend1Token)
        .orThrow()
    println("=== Add Friend 1 Success ===")
    println(add1ResultSuccess)
    println()
    val friend2Token = client.friends.generate(authorization2).orThrow()
    println("=== Friend 2 Token ===")
    println(friend1Token)
    println()
    val add2ResultSuccess = client.friends
        .add(authorization3, friend2Token)
        .orThrow()
    println("=== Add Friend 2 Success ===")
    println(add2ResultSuccess)
    println()
    val friend3Token = client.friends.generate(authorization3).orThrow()
    println("=== Friend 3 Token ===")
    println(friend2Token)
    println()
    val add3ResultSuccess = client.friends
        .add(authorization4, friend3Token)
        .orThrow()
    println("=== Add Friend 3 Success ===")
    println(add3ResultSuccess)
    println()
    val feed1 = client.feed.queue(authorization1).orThrow()
    require(feed1.entries.size == 2)
    println("=== Feed Before ===")
    println(feed1.entries)
    println()
    val requestSuccess = client.friends
        .request(
            authorization = authorization1,
            userId = feed1.entries.first().details.id,
            userAccessHash = feed1.entries.first().details.accessHash,
        )
        .orThrow()
    println("=== Request Success ===")
    println(requestSuccess)
    println()
    val feed2 = client.feed.queue(authorization1).orThrow()
    require(feed2.entries.size == 1)
    println("=== Feed After Request ===")
    println(feed2.entries)
    println()
    val feed3 = client.feed.queue(authorization3).orThrow()
    println("=== Feed After Request From The Other Side ===")
    println(feed3)
}
