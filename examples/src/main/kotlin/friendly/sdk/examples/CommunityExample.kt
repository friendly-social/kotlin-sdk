package friendly.sdk.examples

import friendly.sdk.Authorization
import friendly.sdk.CommunityPostText
import friendly.sdk.CursorId
import friendly.sdk.Field
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
        cursorId = null,
    ).orThrow()
    require(priorPosts.data.isEmpty())
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
    val secondPost = client.community.post(
        authorization = poster,
        text = CommunityPostText.orThrow("Hello, Paging!"),
    ).orThrow()
    println("=== Second Post ===")
    println(secondPost)
    println()
    val afterPosts = client.community.list(
        authorization = viewerAllowed,
        cursorId = null,
    ).orThrow()
    require(afterPosts.data.size == 2)
    println("=== After posts ===")
    println(afterPosts)
    println()
    val forbiddenPosts = client.community.list(
        authorization = viewerForbidden,
        cursorId = null,
    ).orThrow()
    require(forbiddenPosts.data.isEmpty())
    println("=== Forbidden posts ===")
    println(forbiddenPosts)
    println()
    val selfPosts = client.community.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    require(selfPosts.data.size == 2)
    require(selfPosts.data.all { post -> post.owner.id == poster.id })
    println("=== Self posts ===")
    println(selfPosts)
    println()
    val pagedPosts = client.community.list(
        authorization = poster,
        cursorId = CursorId(selfPosts.data.first().id.long.toString()),
    ).orThrow()
    require(pagedPosts.data.size == 1)
    require(pagedPosts.nextId == null)
    println("=== Paged Posts Check ===")
    println(pagedPosts)
    println()
    val delete = client.community.delete(
        authorization = poster,
        id = selfPosts.data.first().id,
    ).orThrow()
    println("=== Delete ===")
    println(delete)
    println()
    val afterDelete = client.community.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    require(afterDelete.data.first().id == selfPosts.data.last().id)
    println("=== After Delete ===")
    println(afterDelete)
    println()
    val editText = CommunityPostText.orThrow("edited")
    val edit = client.community.edit(
        authorization = poster,
        id = selfPosts.data.last().id,
        text = Field(editText),
    ).orThrow()
    println("=== Edit ===")
    println(edit)
    println()
    val afterEdit = client.community.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    require(afterEdit.data.first().text == editText)
    require(afterEdit.data.first().edited)
    println("=== After Edit ===")
    println(afterEdit)
    println()
    val reply = client.community.post(
        authorization = viewerAllowed,
        text = CommunityPostText.orThrow("reply!"),
        replyTo = afterEdit.data.first().descriptor,
    ).orThrow()
    println("=== Reply ===")
    println(reply)
    println()
    val afterReply = client.community.replies(
        authorization = poster,
        replyTo = afterEdit.data.first().descriptor,
        cursorId = null,
    ).orThrow()
    require(afterReply.data.first().id == reply.id)
    println("=== After Reply ===")
    println(afterReply)
    println()
    val replyPreviews = client.community.list(
        authorization = poster,
        cursorId = null,
    ).orThrow()
    require(replyPreviews.data.first().replyPreviews.size == 1)
    require(
        replyPreviews.data.first().replyPreviews.first().id == viewerAllowed.id,
    )
    println("=== Reply Previews ===")
    println(replyPreviews)
    println()
    communityFromExample(poster, viewerAllowed)
    communityGetExample(poster)
}

suspend fun communityFromExample(
    poster: Authorization,
    viewer: Authorization,
) {
    val fromPoster = client.community.from(
        authorization = poster,
        userDescriptor = poster.descriptor,
        cursorId = null,
    ).orThrow()
    require(fromPoster.data.size == 1)
    println("=== From Poster ===")
    println(fromPoster)
    println()
    val fromViewer = client.community.from(
        authorization = poster,
        userDescriptor = viewer.descriptor,
        cursorId = null,
    ).orThrow()
    require(fromViewer.data.isEmpty())
    println("=== From Viewer ===")
    println(fromViewer)
    println()
}

suspend fun communityGetExample(poster: Authorization) {
    val fromPoster = client.community.from(
        authorization = poster,
        userDescriptor = poster.descriptor,
        cursorId = null,
    ).orThrow()
    require(fromPoster.data.size == 1)
    println("=== From Poster ===")
    println(fromPoster)
    println()
    val replies = client.community.replies(
        authorization = poster,
        replyTo = fromPoster.data.first().descriptor,
        cursorId = null,
    ).orThrow()
    require(replies.data.size == 1)
    println("=== Replies ===")
    println(replies)
    println()
    val getReplies = client.community.details(
        authorization = poster,
        descriptor = fromPoster.data.first().descriptor,
    ).orThrow()
    require(getReplies.post == fromPoster.data.first())
    require(getReplies.replies == replies)
    println("=== Get Replies ===")
    println(getReplies)
    println()
    val getUpstream = client.community.details(
        authorization = poster,
        descriptor = replies.data.first().descriptor,
    ).orThrow()
    require(getUpstream.upstream.size == 1)
    require(getUpstream.upstream.first() == fromPoster.data.first())
    println("=== Get Upstream ===")
    println(getUpstream)
    println()
}
