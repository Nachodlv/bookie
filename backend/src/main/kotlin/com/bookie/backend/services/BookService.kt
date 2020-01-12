package com.bookie.backend.services

import com.bookie.backend.dto.RatingResponse
import com.bookie.backend.models.*
import com.bookie.backend.util.BasicCrud
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.InvalidScoreException
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class BookService(val bookDao: BookDao,
                  private val userService: UserService,
                  private val tokenUtil: JwtTokenUtil) : BasicCrud<String, Book> {

    override fun getAll(pageable: Pageable): Page<Book> = bookDao.findAll(pageable)

    override fun getById(id: String): Optional<Book> = bookDao.findById(id)

    override fun insert(obj: Book): Book = bookDao.insert(obj.apply {})

    @Throws(Exception::class)
    override fun update(obj: Book): Book {
        return if (bookDao.existsById(obj.id)) {
            bookDao.save(obj.apply {})
        } else {
            throw object : Exception("Schemas not found") {}
        }
    }

    override fun deleteById(id: String): Optional<Book> {
        return bookDao.findById(id).apply {
            this.ifPresent { bookDao.delete(it) }
        }
    }

    /**
     * Adds a new review to a book.
     *
     * If a book with the specified id already existed, the review is added and the book is updated.
     *
     * If no book exists with that id, a new book is created with the review and saved.
     */
    fun reviewBook(id: String, comment: String, score: Int, token: String): Review {

        if (score < 1 || score > 5) throw InvalidScoreException("The score must be a value between 1 and 5")

        val email = tokenUtil.getUsernameFromToken(token)
        val user: User = userService.getByEmail(email).get()

        val timestamp: Instant = Instant.now()
        val author = Author(user.id!!, user.firstName, user.lastName)

        val result: Optional<Book> = bookDao.findById(id)

        val book: Book
        val review: Review
        if (result.isPresent) {
            book = result.get()
            review = Review(score, comment, author, timestamp, book.id)
            book.addReview(review)
            update(book)
        } else {
            book = Book(id, 0.0)
            review = Review(score, comment, author, timestamp, book.id)
            book.addReview(review)
            insert(book)
        }

        val userReview = Review(score, comment, null, timestamp, book.id)
        user.addReview(userReview)
        userService.update(user)

        userService.addFeedItems(review, user, book)

        return review
    }

    /**
     * Returns the score for a specific book and the number of reviews.
     */
    fun getBookScore(id: String): RatingResponse {
        val result = bookDao.findScoreById(id)
        return result.orElse(RatingResponse(id, 0.0, 0))
    }

    /**
     * Returns the reviews for a specific book.
     */
    fun getReviews(id: String, page: Int, size: Int): List<Review> {
        val result = bookDao.findReviewsById(id, page * size, size)
        if (result.isPresent) {
            return result.get().reviews
        }
        return emptyList()
    }

}