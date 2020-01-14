package com.bookie.backend.models

import java.time.Instant

class ReviewFeedItem(id: String,
                     type: Int,
                     val time: Instant,
                     val rating: Int,
                     val author: Author): FeedItem(id, type)