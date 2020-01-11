package com.bookie.backend.models

/**
 * Represents the author of a review (or comment)
 */
// This class could be unified with the Follower class, as they are the same, at least for now.
data class Author(val id: String,
                  val firstName: String,
                  val lastName: String)