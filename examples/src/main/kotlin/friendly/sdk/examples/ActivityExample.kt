package friendly.sdk.examples

import friendly.sdk.ActivityDetails
import friendly.sdk.CommunityPostText
import friendly.sdk.Interest
import friendly.sdk.InterestList
import friendly.sdk.Nickname
import friendly.sdk.UserDescription

suspend fun activityExample() {
    activityReplyExample()
}

suspend fun activityReplyExample() {
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
    val replier = client.auth.generate(
        nickname = Nickname.orThrow("Friend of y9san9"),
        description = UserDescription.orThrow("Phronology Evangelist"),
        interests = InterestList.orThrow(
            Interest.orThrow("phronology"),
        ),
        avatar = null,
        socialLink = null,
    ).orThrow()
    println("=== Authorization Replier ===")
    println(replier)
    println()
    val addFriendFirst = client.friends.request(
        authorization = poster,
        userId = replier.id,
        userAccessHash = replier.accessHash,
    ).orThrow()
    println("=== Add Friend First ===")
    println(addFriendFirst)
    println()
    val addFriendBack = client.friends.request(
        authorization = replier,
        userId = poster.id,
        userAccessHash = poster.accessHash,
    ).orThrow()
    println("=== Add Friend Back ===")
    println(addFriendBack)
    println()
    val post = client.community.post(
        authorization = poster,
        text = CommunityPostText.orThrow("Hello, World!"),
    ).orThrow()
    println("=== Post ===")
    println(post)
    println()
    val selfReply = client.community.post(
        authorization = poster,
        text = CommunityPostText.orThrow("Self Reply!"),
        replyTo = post,
    ).orThrow()
    println("=== Self Reply ===")
    println(selfReply)
    println()
    val noActivity = client.activity.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    require(noActivity.data.isEmpty())
    println("=== No Activity ===")
    println(noActivity)
    println()
    val reply = client.community.post(
        authorization = replier,
        text = CommunityPostText.orThrow("reply!"),
        replyTo = post,
    ).orThrow()
    println("=== Reply ===")
    println(reply)
    println()
    val activity = client.activity.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    val first = activity.data.first()
    require(activity.data.size == 1)
    require(first is ActivityDetails.Reply)
    require(first.post.descriptor == reply)
    println("=== Activity ===")
    println(activity)
    println()
}
