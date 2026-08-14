package friendly.sdk.examples

import friendly.sdk.Field
import friendly.sdk.Interest
import friendly.sdk.InterestList
import friendly.sdk.Nickname
import friendly.sdk.SocialLink
import friendly.sdk.UserDescription

suspend fun usersExample() {
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
    val user1Details = client.users.details(authorization1).orThrow()
    println("=== Self User Details ===")
    println(user1Details)
    println()
    val authorization2 = client.auth.generate(
        nickname = Nickname.orThrow("y9demn"),
        description = UserDescription.orThrow("Zed Enjoyer"),
        interests = InterestList.orThrow(
            Interest.orThrow("zed"),
        ),
        avatar = null,
        socialLink = SocialLink.orThrow("https://github.com/demndevel"),
    ).orThrow()
    println("=== Authorization 2 ===")
    println(authorization2)
    println()
    val user2Details = client.users.details(
        authorization = authorization1,
        id = authorization2.id,
        accessHash = authorization2.accessHash,
    ).orThrow()
    println("=== Other User Details ===")
    println(user2Details)
    println()
    val editResult = client.users.edit(
        authorization = authorization2,
        nickname = null,
        avatar = null,
        socialLink = Field(value = null),
        description = Field(UserDescription.orThrow("Description?")),
        interests = Field(InterestList.orThrow(Interest.orThrow("dez"))),
    ).orThrow()
    println("=== Other User Edit Success ===")
    println(editResult)
    println()
    val user2NewDetails = client.users.details(
        authorization = authorization1,
        id = authorization2.id,
        accessHash = authorization2.accessHash,
    ).orThrow()
    println("=== Other User New Details ===")
    println(user2NewDetails)
    println()
    usersDetails2Example()
}

suspend fun usersDetails2Example() {
    val authorization1 = client.auth.generate(
        nickname = Nickname.orThrow("User 1"),
        description = UserDescription.orThrow("I want to sleep."),
        interests = InterestList.orThrow(),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization 1 ===")
    println(authorization1)
    println()
    val authorization2 = client.auth.generate(
        nickname = Nickname.orThrow("User 2"),
        description = UserDescription.orThrow("Pleeeease."),
        interests = InterestList.orThrow(),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization 2 ===")
    println(authorization2)
    println()
    val authorization3 = client.auth.generate(
        nickname = Nickname.orThrow("CommonFriend"),
        description = UserDescription.orThrow("Okay??"),
        interests = InterestList.orThrow(),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization 3 ===")
    println(authorization3)
    println()
    for (first in listOf(authorization1, authorization2, authorization3)) {
        for (second in listOf(authorization1, authorization2, authorization3)) {
            if (first == second) continue
            client.friends.request(
                authorization = first,
                userId = second.id,
                userAccessHash = second.accessHash,
            ).orThrow()
        }
    }
    val otherDetails = client.users.details2(
        authorization = authorization1,
        id = authorization2.id,
        accessHash = authorization2.accessHash,
    ).orThrow()
    require(otherDetails.commonFriends!!.first().id == authorization3.id)
    println("=== Other Details ===")
    println(otherDetails)
    println()
    val selfDetails = client.users.details2(
        authorization = authorization1,
        id = authorization1.id,
        accessHash = authorization1.accessHash,
    ).orThrow()
    require(selfDetails.commonFriends == null)
    println("=== Self Details ===")
    println(selfDetails)
    println()
}
