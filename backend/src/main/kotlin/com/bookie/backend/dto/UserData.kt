package com.bookie.backend.dto

/**
 * Data returned when requesting the data for a specific user.
 */
data class UserData(val id: String,
                    val firstName: String,
                    val lastName: String,
                    val email: String,
                    val followerAmount: Int)