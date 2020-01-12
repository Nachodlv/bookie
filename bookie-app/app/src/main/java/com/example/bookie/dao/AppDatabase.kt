package com.example.bookie.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.bookie.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


@Database(entities = [User::class, Book::class, Review::class], version = 10)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun reviewDao(): ReviewDao
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

    @TypeConverter
    fun fromRatingToString(value: Rating?): String? {
        return value?.toJSON()
    }

    @TypeConverter
    fun fromStringToRating(value: String?): Rating? {
        return value?.toObject()
    }

    @TypeConverter
    fun fromTimestampToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}
