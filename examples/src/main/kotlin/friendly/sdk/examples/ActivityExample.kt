package friendly.sdk.examples

import friendly.sdk.ActivityDetails
import friendly.sdk.CommunityPostText
import friendly.sdk.FriendlyActivityClient
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
    val noActivity = client.activity.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    require(noActivity.data.isEmpty())
    println("=== No Activity ===")
    println(noActivity)
    println()
    val addFriendFirst = client.friends.request(
        authorization = poster,
        userId = replier.id,
        userAccessHash = replier.accessHash,
    ).orThrow()
    println("=== Add Friend First ===")
    println(addFriendFirst)
    println()
    val nonMutualActivity = client.activity.list(
        authorization = replier,
        cursorId = null,
    ).orThrow()
    run {
        require(nonMutualActivity.data.size == 1)
        val first = nonMutualActivity.data.first()
        require(first is ActivityDetails.NewRequest)
        require(first.isMutual == false)
    }
    println("=== Non Mutual Activity ===")
    println(nonMutualActivity)
    println()
    val addFriendBack = client.friends.request(
        authorization = replier,
        userId = poster.id,
        userAccessHash = poster.accessHash,
    ).orThrow()
    println("=== Add Friend Back ===")
    println(addFriendBack)
    println()
    val mutualActivity = client.activity.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    run {
        require(mutualActivity.data.size == 1)
        val first = mutualActivity.data.first()
        require(first is ActivityDetails.NewRequest)
        require(first.isMutual)
    }
    println("=== Mutual Activity ===")
    println(mutualActivity)
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
    require(activity.data.size == 2)
    require(first is ActivityDetails.NewReply)
    require(first.post.descriptor == reply)
    require(!first.isRead)
    println("=== Activity ===")
    println(activity)
    println()
    val newUnreads = client.activity.newUnreads(poster).orThrow()
    require(newUnreads is FriendlyActivityClient.NewUnreadsResult.Some)
    println("=== New Unreads ===")
    println(newUnreads)
    println()
    val read = client.activity.read(
        authorization = poster,
        id = first.id,
    ).orThrow()
    println("=== Read ===")
    println(read)
    println()
    val readActivity = client.activity.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    val readFirst = readActivity.data.first()
    require(readFirst.isRead)
    println("=== Read Activity ===")
    println(readActivity)
    println()
    val noUnreads = client.activity.newUnreads(poster).orThrow()
    require(noUnreads is FriendlyActivityClient.NewUnreadsResult.None)
    println("=== New Unreads ===")
    println(noUnreads)
    println()
    val delete = client.community.delete(
        authorization = replier,
        id = reply.id,
    ).orThrow()
    println("=== Delete ===")
    println(delete)
    println()
    val deletedActivity = client.activity.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    require(deletedActivity.data.size == 1)
    println("=== Deleted Activity ===")
    println(deletedActivity)
    println()
    val indirectReply = client.community.post(
        authorization = poster,
        text = CommunityPostText.orThrow("indirect reply"),
        replyTo = post,
    ).orThrow()
    println("=== Indirect Reply ===")
    println(indirectReply)
    println()
    val indirectReplyActivity = client.activity.list(
        authorization = replier,
        cursorId = null,
    ).orThrow()
    run {
        val first = indirectReplyActivity.data.first()
        require(first is ActivityDetails.IndirectReplies)
        require(first.post.descriptor == post)
    }
    println("=== Indirect Reply Activity ===")
    println(indirectReplyActivity)
    println()
}
