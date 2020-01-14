package com.bookie.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PasswordResetRequest(@JsonProperty("email") val email: String)