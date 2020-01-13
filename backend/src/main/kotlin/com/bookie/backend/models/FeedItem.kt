package com.bookie.backend.models

abstract class FeedItem(val id: String, // Book id
                        val type: Int) // Should this be here? How do we send it?