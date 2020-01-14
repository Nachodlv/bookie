package com.bookie.backend.controllers

import com.bookie.backend.dto.LikeRequest
import com.bookie.backend.models.Review
import com.bookie.backend.services.BookService
import com.bookie.backend.util.exceptions.BookNotFoundException
import com.bookie.backend.util.exceptions.LikeException
import com.bookie.backend.util.exceptions.ReviewNotFoundException
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/review")
class ReviewController(private val bookService: BookService) {

    /**
     * Adds a like to a review.
     *
     * The structure of the request body should be as follows:
     *
     * {
     *     bookId: String,
     *     userId: String
     * }
     *
     * Where bookId is the id of the book for which the review was written and userId is the id of the review's author.
     */
    @PostMapping("/like")
    fun addLike(@RequestHeader headers: Map<String, String>,
                @RequestBody request: LikeRequest): ResponseEntity<Review> { // Probably should return a subset of its attributes.
        val token = headers["authorization"]?.substring(7)

        if (token != null) {
            return try {
                ResponseEntity(bookService.addLikeToReview(token, request.bookId, request.userId), HttpStatus.OK)
            } catch (e1: BookNotFoundException) { // This could be improved.
                ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (e2: ReviewNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (e3: UserNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (e4: LikeException) {
                ResponseEntity(HttpStatus.CONFLICT)
            }
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    /**
     * Removes a like from a review.
     *
     * The structure of the request body should be as follows:
     *
     * {
     *     bookId: String,
     *     userId: String
     * }
     *
     * Where bookId is the id of the book for which the review was written and userId is the id of the review's author.
     */
    @PostMapping("unlike")
    fun removeLike(@RequestHeader headers: Map<String, String>,
                   @RequestBody request: LikeRequest): ResponseEntity<Review> { // Probably should return a subset of its attributes.
        val token = headers["authorization"]?.substring(7)

        if (token != null) {
            return try {
                ResponseEntity(bookService.removeLikeFromReview(token, request.bookId, request.userId), HttpStatus.OK)
            } catch (e1: BookNotFoundException) { // This could be improved.
                ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (e2: ReviewNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (e3: UserNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (e4: LikeException) {
                ResponseEntity(HttpStatus.CONFLICT)
            }
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

}