package com.bookie.backend.services

import com.bookie.backend.dto.RatingResponse
import com.bookie.backend.dto.ReviewResponse
import com.bookie.backend.models.*
import com.bookie.backend.util.BasicCrud
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.BookNotFoundException
import com.bookie.backend.util.exceptions.InvalidScoreException
import com.bookie.backend.util.exceptions.ReviewNotFoundException
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
    fun getReviews(id: String, page: Int, size: Int, token: String): List<ReviewResponse> {

        // This will probably not work
        val result = bookDao.findReviewsById(id, page * size, size)

        val userId = userService.getByToken(token).get().id // Not very efficient

        if (result.isPresent) {
            return result.get().reviews.map{
                review ->
                ReviewResponse(
                        review.rating,
                        review.comment,
                        review.author,
                        review.timestamp,
                        review.id,
                        review.likes,
                        review.likedBy.contains(userId))
            }
        }
        return emptyList()
    }

    /**
     * Returns the review that the current user wrote for the specified book
     */
    fun getReviewForBook(token: String, bookId: String): ReviewResponse {
        val user = userService.getByToken(token).get()
        val book = getById(bookId).orElseThrow { throw BookNotFoundException("No book found with the provided id") }

        val review: Review? = book.reviews.find { review -> review.author?.id == user.id }
        if (review != null) {
            return ReviewResponse(
                    review.rating,
                    review.comment,
                    review.author,
                    review.timestamp,
                    review.id,
                    review.likes,
                    review.likedBy.contains(user.id))
        }
        throw ReviewNotFoundException("No review found for that book by that author")
    }

    /**
     * Adds a like to a review and updates the book it belongs to.
     */
    fun addLikeToReview(token: String, bookId: String, authorId: String): Review {
        val user = userService.getByToken(token).get()
        val book = getById(bookId).orElseThrow{throw BookNotFoundException("No book found with the provided id")}

        if (user.id != null) {
            val review = findReviewInBook(book.reviews, authorId)
            val author = userService.getById(authorId).get()
            val review2 = findReviewInAuthor(author.reviews, bookId)
            review.addLike(user.id)
            review2.addLike(user.id)
            update(book)
            userService.update(author)
            return review
        } else {
            throw UserNotFoundException("User not found") // This should never happen
        }
    }

    /**
     * Removes a like from a review and updates the book it belonged to.
     */
    fun removeLikeFromReview(token: String, bookId: String, authorId: String): Review {
        val user = userService.getByToken(token).get()
        val book = getById(bookId).orElseThrow{throw BookNotFoundException("No book found with the provided id")}

        if (user.id != null) {
            val review = findReviewInBook(book.reviews, authorId)
            val author = userService.getById(authorId).get()
            val review2 = findReviewInAuthor(author.reviews, bookId)
            review.removeLike(user.id)
            review2.removeLike(user.id)
            update(book)
            userService.update(author)
            return review
        } else {
            throw UserNotFoundException("User not found") // This should never happen
        }
    }

    // This is not very efficient or the best way of doing it.
    private fun findReviewInBook(reviews: MutableList<Review>, authorId: String): Review {
        val review: Review? = reviews.firstOrNull { review -> review.author?.id == authorId }

        if (review != null) {
            val index: Int = reviews.indexOf(review) // Get the index of the review
            val likes = review.likes

            var i = index-1
            while (i >= 0 && reviews[i].likes < likes) {
                i--
            }
            if (i+1 != index) {
                reviews.removeAt(index)
                reviews.add(i+1, review)
            }
            return review
        }
        throw ReviewNotFoundException("No review found for that book from that author")
    }

    private fun findReviewInAuthor(reviews: MutableList<Review>, bookId: String): Review {
        val review: Review? = reviews.firstOrNull { review -> review.id == bookId }
        if (review != null) {
            return review
        }
        throw ReviewNotFoundException("No review found from that author for that book")
    }
}