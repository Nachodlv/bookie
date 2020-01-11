package com.bookie.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RatingRequest(@JsonProperty("ids") val ids: List<String>)