package com.bookie.backend.controllers

import com.bookie.backend.dto.UserDto
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

    @GetMapping("{id}")
    fun getById(@PathVariable id: String): Optional<User> = userService.getById(id)

    @PostMapping
    // Do we need to have a confirm password?
    fun insert(@RequestBody user: User): User = userService.insert(user)

    @PutMapping
    fun update(@RequestBody user: User): User = userService.update(user)

    @DeleteMapping("{id}")
    fun deleteById(@PathVariable id: String): Optional<User> = userService.deleteById(id)

    @PostMapping("/register")
    fun registerUser(@RequestBody user: UserDto): ResponseEntity<User> {
        return try {
            // We should return a DTO with the relevant data.
            ResponseEntity(userService.registerUser(user), HttpStatus.OK)
        } catch (e: EmailAlreadyExistsException) {
            // This behavior can be generalized so that exceptions are handled automatically and return a desired status.
            // This is done using exception handlers
            ResponseEntity(HttpStatus.CONFLICT) // We could send a message too, but how do we do that?
        }
    }
}
