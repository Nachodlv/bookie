package com.example.bookie.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.bookie.models.Book
import com.example.bookie.models.BookImage
import com.example.bookie.models.User
import com.example.bookie.models.toObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


@Database(entities = [User::class, Book::class], version = 6)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
}

class Converters {
    @TypeConverter
    fun fromStringToList(value: String?): List<String?>? {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }


    @TypeConverter
    fun fromListToString(list: List<String?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromBookImageToString(value: BookImage?): String? {
        return value?.toJSON()
    }

    @TypeConverter
    fun fromStringToBookImage(value: String?): BookImage? {
        return value?.toObject()
    }
}
