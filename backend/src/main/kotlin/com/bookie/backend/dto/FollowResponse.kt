package com.bookie.backend.dto

data class FollowResponse(val id: String, val firstName: String, val lastName: String, var followed: Boolean? = false)