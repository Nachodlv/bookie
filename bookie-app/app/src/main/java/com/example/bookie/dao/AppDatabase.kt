package com.example.bookie.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookie.models.User

@Database(entities = [User::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}