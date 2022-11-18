package us.xylight.surveyer.database.dataclass

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Listing(val kind: String, @SerialName("data") val subreddit: Subreddit)

@Serializable
data class Subreddit(
    @SerialName("children") val posts: List<Post>
)

@Serializable
data class Post(val kind: String, val data: PostData)

@Serializable
data class PostData(
    val title: String,
    val subreddit: String,
    @SerialName("selftext") val text: String?,
    @SerialName("ups") val upvotes: Int?,
    @SerialName("num_comments") val commentCount: Int?,
    @SerialName("upvote_ratio") val upvoteRatio: Float?,
    @SerialName("over_18") val isNsfw: Boolean,
    val pinned: Boolean,
    @SerialName("url") val mediaUrl: String?,
    @SerialName("is_video") val isVideo: Boolean,
    @SerialName("permalink") val url: String,
    val stickied: Boolean
)