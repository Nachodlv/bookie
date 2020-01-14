package com.bookie.backend.models

import java.time.Instant

class CommentFeedItem(id: String,
                     type: Int,
                     val time: Instant,
                     val preview: String): FeedItem(id, type)