package com.bookie.backend.controllers

import com.bookie.backend.models.Customer
import com.bookie.backend.services.CustomerService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/customer")
class CustomerController(private val customerService: CustomerService) {

    @GetMapping
    fun getAll(pageable: Pageable): Page<Customer> = customerService.getAll(pageable)

    @GetMapping("{id}")
    fun getById(@PathVariable id: String): Optional<Customer> = customerService.getById(id)

    @PostMapping
    fun insert(@RequestBody customer: Customer): Customer = customerService.insert(customer)

    @PutMapping
    fun update(@RequestBody customer: Customer): Customer = customerService.update(customer)

    @DeleteMapping("{id}")
    fun deleteById(@PathVariable id: String): Optional<Customer> = customerService.deleteById(id)
}