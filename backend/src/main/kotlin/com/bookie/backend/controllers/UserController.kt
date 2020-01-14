package com.bookie.backend.controllers

import com.bookie.backend.dto.AnonymousReview
import com.bookie.backend.dto.RegisterResponse
import com.bookie.backend.dto.UserData
import com.bookie.backend.dto.UserDto
import com.bookie.backend.models.Review
import com.bookie.backend.models.User
import com.bookie.backend.services.UserService
import com.bookie.backend.util.exceptions.EmailAlreadyExistsException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAll(pageable: Pageable): Page<User> = userService.getAll(pageable)

    /**
     * Returns the data of the user with the provided id
     *
     * The data returned is the following:
     *
     * {
     *     id: String,
     *     firstName: String,
     *     lastName: String,
     *     email: String,
     *     followerAmount: Int
     * }
     *
     */
    @GetMapping("{id}")
    fun getById(@PathVariable id: String): ResponseEntity<UserData> {
        val data: Optional<UserData> = userService.getUserDataById(id)
        if (data.isPresent) {
            return ResponseEntity(data.get(), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun insert(@RequestBody user: User): User = userService.insert(user)

    @PutMapping
    fun update(@RequestBody user: User): User = userService.update(user)

    @DeleteMapping("{id}")
    fun deleteById(@PathVariable id: String): Optional<User> = userService.deleteById(id)

    /**
     * Registers a new user in the application.
     *
     * The data required for the user is the following:
     *
     * {
     *     firstName: String,
     *     lastName: String,
     *     email: String,
     *     password: String
     * }
     *
     * This route requires no authentication
     *
     * @return {@code HttpStatus.OK} if the creation was successful, with the data of the new created user
     * @return {@code HttpStatus.CONFLICT} if the email provided is already in use
     */
    @PostMapping("/register")
    fun registerUser(@RequestBody user: UserDto): ResponseEntity<RegisterResponse> {
        return try {
            val newUser = userService.registerUser(user)
            val response = newUser.id?.let { RegisterResponse(it, newUser.firstName, newUser.lastName, newUser.email) }
            ResponseEntity(response, HttpStatus.OK)
        } catch (e: EmailAlreadyExistsException) {
            // This behavior can be generalized so that exceptions are handled automatically and return a desired status.
            // This is done using exception handlers
            ResponseEntity(HttpStatus.CONFLICT) // We could send a message too, but how do we do that?
        }
    }

    /**
     * Returns the data of the currently logged in user.
     */
    @GetMapping("/current")
    fun getCurrentUser(@RequestHeader headers: Map<String, String>): ResponseEntity<UserData> {

        val token = headers["authorization"]?.substring(7)

        return if (token != null) {
            val user = userService.getByToken(token).get()
            val userData = UserData(
                    user.id!!,
                    user.firstName,
                    user.lastName,
                    user.email,
                    user.followerAmount
            )
            ResponseEntity(userData, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }

    /**
     * Returns the reviews written by a user
     *
     * The structure of the response is as follows:
     *
     * {
     *     id: String,
     *     comment: String,
     *     rating: Int,
     *     timestamp: Instant
     * }
     *
     * The author property is omitted as it is already known and would be repeated for every review.
     *
     */
    @GetMapping("/reviews/{id}")
    fun getUserReviews(@PathVariable id: String,
                       @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
                       @RequestParam(value = "size",required = false, defaultValue = "10") size: Int): ResponseEntity<List<AnonymousReview>> {
        val result: List<AnonymousReview> = userService.getReviews(id, page, size)
                .map{review -> AnonymousReview(review)}
        return ResponseEntity(result, HttpStatus.OK)
    }

}
