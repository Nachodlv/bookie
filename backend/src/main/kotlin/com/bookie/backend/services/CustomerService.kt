package com.bookie.backend.services

import com.bookie.backend.models.Customer
import com.bookie.backend.util.BasicCrud
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerService(val customerDao: CustomerDao) : BasicCrud<String, Customer> {

    override fun getAll(pageable: Pageable): Page<Customer> = customerDao.findAll(pageable)

    override fun getById(id: String): Optional<Customer> = customerDao.findById(id)

    override fun insert(obj: Customer): Customer = customerDao.insert(obj.apply {})

    @Throws(Exception::class)
    override fun update(obj: Customer): Customer {
        return if (obj.id != null && customerDao.existsById(obj.id)) {
            customerDao.save(obj.apply {})
        } else {
            throw object : Exception("Schemas not found") {}
        }
    }

    override fun deleteById(id: String): Optional<Customer> {
        return customerDao.findById(id).apply {
            this.ifPresent { customerDao.delete(it) }
        }
    }
}