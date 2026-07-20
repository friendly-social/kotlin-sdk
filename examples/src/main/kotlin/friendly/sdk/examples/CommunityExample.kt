package friendly.sdk.examples

import friendly.sdk.CommunityPostText
import friendly.sdk.Interest
import friendly.sdk.InterestList
import friendly.sdk.Nickname
import friendly.sdk.UserDescription

suspend fun communityExample() {
    val poster = client.auth.generate(
        nickname = Nickname.orThrow("y9san9"),
        description = UserDescription.orThrow("Phronology Evangelist"),
        interests = InterestList.orThrow(
            Interest.orThrow("phronology"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization Poster ===")
    println(poster)
    println()
    val viewerAllowed = client.auth.generate(
        nickname = Nickname.orThrow("Friend of y9san9"),
        description = UserDescription.orThrow("Phronology Evangelist"),
        interests = InterestList.orThrow(
            Interest.orThrow("phronology"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization Viewer Allowed ===")
    println(viewerAllowed)
    println()
    val viewerForbidden = client.auth.generate(
        nickname = Nickname.orThrow("Not friend of y9san9"),
        description = UserDescription.orThrow("Phronology Evangelist"),
        interests = InterestList.orThrow(
            Interest.orThrow("phronology"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization Viewer Forbidden ===")
    println(viewerForbidden)
    println()
    val addFriendFirst = client.friends.request(
        authorization = poster,
        userId = viewerAllowed.id,
        userAccessHash = viewerAllowed.accessHash,
    ).orThrow()
    println("=== Add Friend First ===")
    println(addFriendFirst)
    println()
    val addFriendBack = client.friends.request(
        authorization = viewerAllowed,
        userId = poster.id,
        userAccessHash = poster.accessHash,
    ).orThrow()
    println("=== Add Friend Back ===")
    println(addFriendBack)
    println()
    val priorPosts = client.community.list(
        authorization = viewerAllowed,
    ).orThrow()
    require(priorPosts.isEmpty())
    println("=== Prior posts ===")
    println(priorPosts)
    println()
    val post = client.community.post(
        authorization = poster,
        text = CommunityPostText.orThrow("Hello, World!"),
    ).orThrow()
    println("=== Post ===")
    println(post)
    println()
    val afterPosts = client.community.list(
        authorization = viewerAllowed,
    ).orThrow()
    require(afterPosts.isNotEmpty())
    println("=== After posts ===")
    println(afterPosts)
    println()
    val forbiddenPosts = client.community.list(
        authorization = viewerForbidden,
    ).orThrow()
    require(forbiddenPosts.isEmpty())
    println("=== Forbidden posts ===")
    println(forbiddenPosts)
    println()
}
