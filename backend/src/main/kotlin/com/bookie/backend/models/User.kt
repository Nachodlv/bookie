package com.bookie.backend.models

import com.bookie.backend.util.exceptions.FollowingException
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String,
        @Id val id: String? = null,
        val roles: List<String> = emptyList(),
        var followerAmount: Int = 0,
        val followers: MutableList<Follower> = mutableListOf(),
        val following: MutableList<Follower> = mutableListOf(),
        val reviews: MutableList<Review> = mutableListOf(),
        var feed: List<FeedItem> = emptyList()) {

    companion object {
        const val MAX_FEED_SIZE = 50
    }

    /**
     * Adds a follower to the user and increments its follower count by one.
     *
     * The user is also added to the following list of the provided user to maintain consistency.
     *
     * @throws FollowingException if the user was already being followed by the provided user.
     */
    fun addFollower(follower: User) {
        val repeated = this.followers.find{ item -> item.id == follower.id}
        if (repeated != null) throw FollowingException("User already followed")

        val newFollower = Follower(follower.firstName, follower.lastName, follower.id)
        this.followers.add(newFollower)
        this.followerAmount++

        follower.addFollowing(this)
    }

    /**
     * Removes a follower from the user and decrements its follower count by one.
     *
     * The user is also removed from the following list of the provided user to maintain consistency.
     *
     * @throws FollowingException if the user was not being followed by the provided user.
     */
    fun removeFollower(follower: User) {
        val existing = this.followers.find{ item -> item.id == follower.id}
                ?: throw FollowingException("User already followed")

        this.followers.remove(existing)
        this.followerAmount--

        follower.removeFollowing(this)
    }

    /**
     * Adds a review to the user.
     */
    fun addReview(review: Review) {
        // We could add more checks to make sure he is the author and it is not repeated.
        reviews.add(review)
    }

    private fun addFollowing(following: User) {
        val newFollowing = Follower(following.firstName, following.lastName, following.id)
        this.following.add(newFollowing)
    }

    private fun removeFollowing(following: User) {
        val oldFollowing = Follower(following.firstName, following.lastName, following.id)
        this.following.remove(oldFollowing)
    }

    /**
     * Adds an item to the user's feed.
     */
    fun addFeedItem(item: FeedItem) {
        if (this.feed.size >= MAX_FEED_SIZE) {
            this.feed = this.feed.drop(1).plus(item)
        } else this.feed = this.feed.plus(item)
    }

    /**
     * Returns the last 'amount' elements from the feed, removing them from it.
     */
    fun getLatestFeedItems(amount: Int): List<FeedItem> {
        val result: List<FeedItem> = this.feed.takeLast(amount)
        this.feed = this.feed.dropLast(amount)
        return result
    }
}