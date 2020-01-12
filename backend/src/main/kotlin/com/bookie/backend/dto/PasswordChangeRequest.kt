package com.bookie.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PasswordChangeRequest(@JsonProperty("email") val email: String)