package com.bookie.backend.controllers

import com.bookie.backend.models.User
import com.bookie.backend.services.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAll(pageable: Pageable): Page<User> = userService.getAll(pageable)

    @GetMapping("{id}")
    fun getById(@PathVariable id: String): Optional<User> = userService.getById(id)

    /**
     * Registers a new user in the system.
     */
    @PostMapping
    // Do we need to have a confirm password?
    fun insert(@RequestBody customer: User): User = userService.insert(customer)

    @PutMapping
    fun update(@RequestBody customer: User): User = userService.update(customer)

    @DeleteMapping("{id}")
    fun deleteById(@PathVariable id: String): Optional<User> = userService.deleteById(id)
}
