package com.bookie.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PasswordChangeRequest(@JsonProperty("newPassword") val newPassword: String)