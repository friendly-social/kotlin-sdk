package friendly.sdk.examples

import friendly.sdk.Friendship
import friendly.sdk.Interest
import friendly.sdk.InterestList
import friendly.sdk.Nickname
import friendly.sdk.UserDescription

suspend fun friendsExample() {
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
        .add(authorization2, friend1Token, authorization1.id)
        .orThrow()
    println("=== Add Friend 1 Success ===")
    println(add1ResultSuccess)
    println()
    val friend2Token = client.friends.generate(authorization2).orThrow()
    println("=== Friend 2 Token ===")
    println(friend1Token)
    println()
    val add2ResultSuccess = client.friends
        .add(authorization3, friend2Token, authorization2.id)
        .orThrow()
    println("=== Add Friend 2 Success ===")
    println(add2ResultSuccess)
    println()
    val friend3Token = client.friends.generate(authorization3).orThrow()
    println("=== Friend 3 Token ===")
    println(friend2Token)
    println()
    val add3ResultSuccess = client.friends
        .add(authorization4, friend3Token, authorization3.id)
        .orThrow()
    println("=== Add Friend 3 Success ===")
    println(add3ResultSuccess)
    println()
    val feed = client.feed.queue(authorization1).orThrow()
    println("=== Feed ===")
    println(feed.entries)
    println()
    val (first, second) = feed.entries.map { entry -> entry.details }
    val requestSuccess = client.friends.request(
        authorization = authorization1,
        userId = first.id,
        userAccessHash = first.accessHash,
    ).orThrow()
    println("=== Request Success ===")
    println(requestSuccess)
    println()
    val declineSuccess = client.friends.decline(
        authorization = authorization1,
        userId = second.id,
        userAccessHash = second.accessHash,
    ).orThrow()
    println("=== Decline Success ===")
    println(declineSuccess)
    println()
    testFriendship()
}

suspend fun testFriendship() {
    val friend1 = client.auth.generate(
        nickname = Nickname.orThrow("y9san9"),
        description = UserDescription.orThrow("Anti-phronology"),
        interests = InterestList.orThrow(
            Interest.orThrow("anti-phronology"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    val friend2 = client.auth.generate(
        nickname = Nickname.orThrow("kotleni"),
        description = UserDescription.orThrow("Web Dev"),
        interests = InterestList.orThrow(
            Interest.orThrow("web"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    val noneDetails = client.users.details2(
        authorization = friend1,
        id = friend2.id,
        accessHash = friend2.accessHash,
    ).orThrow()
    require(noneDetails.user.friendship == Friendship.None)
    println("=== None Details ===")
    println(noneDetails)
    println()
    client.friends.request(
        authorization = friend1,
        userId = friend2.id,
        userAccessHash = friend2.accessHash,
    ).orThrow()
    client.friends.request(
        authorization = friend2,
        userId = friend1.id,
        userAccessHash = friend1.accessHash,
    ).orThrow()
    val friendDetails = client.users.details2(
        authorization = friend1,
        id = friend2.id,
        accessHash = friend2.accessHash,
    ).orThrow()
    require(friendDetails.user.friendship == Friendship.Friends)
    println("=== Friend Details ===")
    println(friendDetails)
    println()
    client.friends.decline(
        authorization = friend1,
        userId = friend2.id,
        userAccessHash = friend2.accessHash,
    ).orThrow()
    val incomingDetails = client.users.details2(
        authorization = friend1,
        id = friend2.id,
        accessHash = friend2.accessHash,
    ).orThrow()
    require(incomingDetails.user.friendship == Friendship.IncomingRequest)
    println("=== Incoming Details ===")
    println(incomingDetails)
    println()
    client.friends.decline(
        authorization = friend2,
        userId = friend1.id,
        userAccessHash = friend1.accessHash,
    ).orThrow()
    client.friends.request(
        authorization = friend1,
        userId = friend2.id,
        userAccessHash = friend2.accessHash,
    ).orThrow()
    val outgoingDetails = client.users.details2(
        authorization = friend1,
        id = friend2.id,
        accessHash = friend2.accessHash,
    ).orThrow()
    require(outgoingDetails.user.friendship == Friendship.OutgoingRequest)
    println("=== Outgoing Details ===")
    println(outgoingDetails)
    println()
    client.friends.decline(
        authorization = friend1,
        userId = friend2.id,
        userAccessHash = friend2.accessHash,
    ).orThrow()
    val blockDetails = client.users.details2(
        authorization = friend1,
        id = friend2.id,
        accessHash = friend2.accessHash,
    ).orThrow()
    require(blockDetails.user.friendship == Friendship.OutgoingDecline)
    println("=== Block Details ===")
    println(blockDetails)
    println()
}
