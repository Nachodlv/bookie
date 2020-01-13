package com.example.bookie.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookie.models.Book
import com.example.bookie.models.User
import java.util.*

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg books: Book)
    @Update
    fun update(vararg books: Book)
    @Delete
    fun delete(vararg books: Book)

    @Query("SELECT * FROM BOOK WHERE id = :id")
    fun getById(id: String): LiveData<Book>

    @Query("SELECT * FROM BOOK WHERE id = :id")
    fun getByIdInstant(id: String): Book?


    @Query("SELECT * FROM BOOK WHERE id = :id and lastFetch >= (:now - :timeout)")
    fun hasBook(id: String, timeout: Long, now: Long = Calendar.getInstance().timeInMillis): Book?

}