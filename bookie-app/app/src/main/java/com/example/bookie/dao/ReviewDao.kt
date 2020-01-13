package com.example.bookie.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookie.models.Review
import java.util.*

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg reviews: Review)

    @Update
    fun update(vararg reviews: Review)

    @Delete
    fun delete(vararg reviews: Review)

    @Query("SELECT * FROM REVIEW WHERE bookId = :id")
    fun getByBookId(id: String): LiveData<List<Review>>


    @Query("SELECT * FROM REVIEW WHERE bookId = :bookId and userId = :userId and lastFetch >= (:now - :timeout)")
    fun hasReview(
        bookId: String,
        userId: String,
        timeout: Long,
        now: Long = Calendar.getInstance().timeInMillis
    ): Review?

}