package com.bookie.backend.util.exceptions

import java.lang.Exception

class SelfFollowingException(message: String): Exception(message)