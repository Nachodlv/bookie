package com.bookie.backend.controllers

import com.bookie.backend.dto.RatingRequest
import com.bookie.backend.dto.ReviewRequest
import com.bookie.backend.dto.RatingResponse
import com.bookie.backend.dto.ReviewResponse
import com.bookie.backend.models.Review
import com.bookie.backend.services.BookService
import com.bookie.backend.util.exceptions.InvalidScoreException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.websocket.server.PathParam

@RestController
@RequestMapping("/book")
class BookController(private val bookService: BookService) {

    /**
     * Adds a new review to a book
     *
     * The structure of the request should be the following:
     *
     * {
     *     id: String,
     *     comment: String,
     *     score: Int
     * }
     *
     * Where id is the id of the book, comment is the text entered by the user and score is a value from 1 to 5
     */
    @PostMapping("/review")
    fun reviewBook(@RequestBody review: ReviewRequest,
                   @RequestHeader headers: Map<String, String>): ResponseEntity<Review> {
        val token = headers["authorization"]?.substring(7)
        if (token != null) {
            return try {
                ResponseEntity(bookService.reviewBook(review.id, review.comment, review.score, token), HttpStatus.OK)
            } catch (e: InvalidScoreException) {
                ResponseEntity(HttpStatus.BAD_REQUEST)
            }
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    /**
     * Returns the score of a book, as well as the number of existing reviews
     *
     * The structure of the response is as follows:
     *
     * {
     *     id: String,
     *     rating: Double,
     *     reviewAmount: Int
     * }
     *
     * If there are no reviews for book, the score returned will be 0
     */
    @GetMapping("/rating/{id}")
    fun getBookScore(@PathVariable id: String): ResponseEntity<RatingResponse> {
        return ResponseEntity(bookService.getBookScore(id), HttpStatus.OK)
    }

    /**
     * Returns the scores of several books.
     *
     * The ids of the books should be specified as follows:
     *
     * {
     *     ids: ["1", "2", ... ]
     * }
     *
     * And the result will be a list of elements with the following structure:
     *
     * {
     *     id: String,
     *     rating: Double,
     *     amountOfReviews: Int
     * }
     *
     * The results will be in the order they were received.
     */
    @PutMapping("/rating")
    fun getBooksScore(@RequestBody request: RatingRequest): ResponseEntity<List<RatingResponse>> {
        val result: List<RatingResponse> = request.ids.map{
            id -> bookService.getBookScore(id)
        }
        return ResponseEntity(result, HttpStatus.OK)
    }

    /**
     * Returns a page of reviews for a specific book
     *
     * The structure of the response is as follows:
     *
     * {
     *     id: String,
     *     comment: String,
     *     rating: Int,
     *     author: {
     *         id: String,
     *         firstName: String,
     *         lastName: String
     *     },
     *     timestamp: Instant,
     *     likes: Int,
     *     liked: Boolean
     * }
     *
     * If there are no reviews for the book, an empty list is returned.
     *
     * This route returns information on whether the current user has liked each review or not.
     * This information is shown in the liked attribute.
     */
    @GetMapping("/reviews/{id}")
    fun getReviews(@PathVariable id: String,
                   @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
                   @RequestParam(value = "size",required = false, defaultValue = "10") size: Int,
                   @RequestHeader headers: Map<String, String>): ResponseEntity<List<ReviewResponse>> {
        val token = headers["authorization"]?.substring(7)

        if (token != null) {
            return ResponseEntity(bookService.getReviews(id, page, size, token), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }
}
