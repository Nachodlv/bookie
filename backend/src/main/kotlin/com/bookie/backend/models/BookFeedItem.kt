package com.bookie.backend.models

import java.time.Instant

class BookFeedItem(id: String,
                   type: Int,
                   val rating: Double): FeedItem(id, type)