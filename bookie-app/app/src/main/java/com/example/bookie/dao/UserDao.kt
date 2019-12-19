package com.example.bookie.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookie.models.User
import java.util.*
import java.util.concurrent.TimeUnit


@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}


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

    @Query("SELECT * FROM USER WHERE id = :id and lastFetch >= (:now - :timeout)")
    fun hasUser(id: String, timeout: Long, now: Long = Calendar.getInstance().timeInMillis): Boolean

}