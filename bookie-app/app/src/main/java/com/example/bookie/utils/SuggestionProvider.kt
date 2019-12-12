package com.example.bookie.utils

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.bookie.models.BookSearch


class SuggestionProvider : ContentProvider() {


    companion object {
        val columns: Array<String> = arrayOf(
            "_id",
            "icon_id",  // ID of a drawable (icon) as String
            SearchManager.SUGGEST_COLUMN_TEXT_1,  // main text for suggestion display
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA // book id
        )
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        return if (p3 != null && p3.isNotEmpty() && p3[0].isNotEmpty()) { // the entered text can be found in selectionArgs[0]
            val cursor = MatrixCursor(columns)
            getSuggestions(p3[0]).forEach { cursor.addRow(it) }
            cursor
        } else { // user hasn't entered anything
            MatrixCursor(columns)
        }
    }

    private fun getSuggestions(query: String): Array<Array<String>> {
        val books: Array<BookSearch> = arrayOf(
            BookSearch("0", "The Fellowship of the Ring", "JRR Tolkien"),
            BookSearch("1", "Jesus our lord, Bible Study", "Charles R. Swindoll")
        )

        return books.map { arrayOf("0", "0", it.name, "by ${it.author}", it.id) }.toTypedArray()
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun getType(p0: Uri): String? {
        return null
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }
}