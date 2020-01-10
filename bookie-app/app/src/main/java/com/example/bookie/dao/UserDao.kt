package com.example.bookie.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookie.models.User
import java.util.*


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg users: User)

    @Update
    fun update(vararg users: User)

    @Delete
    fun delete(vararg users: User)

    @Query("SELECT * FROM user WHERE id = :id")
    fun getById(id: String): LiveData<User>

    @Query("SELECT * FROM user WHERE email = :email")
    fun getByEmail(email: String): LiveData<User>

    @Query("SELECT * FROM USER WHERE id = :id and lastFetch >= (:now - :timeout)")
    fun hasUser(id: String, timeout: Long, now: Long = Calendar.getInstance().timeInMillis): User?

    @Query("SELECT * FROM USER WHERE email = :email and lastFetch >= (:now - :timeout)")
    fun hasUserByEmail(email: String, timeout: Long, now: Long = Calendar.getInstance().timeInMillis): User?

}