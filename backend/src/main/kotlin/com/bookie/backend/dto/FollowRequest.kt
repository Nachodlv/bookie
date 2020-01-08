package com.bookie.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FollowRequest(@JsonProperty("userId") val userId: String)