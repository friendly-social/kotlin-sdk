package friendly.sdk

public sealed interface Friendship {
    public fun serializable(): FriendshipSerializable

    public data object Friends : Friendship {
        override fun serializable(): FriendshipSerializable =
            FriendshipSerializable(string = "friends")
    }
    public data object IncomingRequest : Friendship {
        override fun serializable(): FriendshipSerializable =
            FriendshipSerializable(string = "incomingRequest")
    }
    public data object OutgoingRequest : Friendship {
        override fun serializable(): FriendshipSerializable =
            FriendshipSerializable(string = "outgoingRequest")
    }
    public data object Block : Friendship {
        override fun serializable(): FriendshipSerializable =
            FriendshipSerializable(string = "block")
    }
    public data object None : Friendship {
        override fun serializable(): FriendshipSerializable =
            FriendshipSerializable(string = "none")
    }
}
