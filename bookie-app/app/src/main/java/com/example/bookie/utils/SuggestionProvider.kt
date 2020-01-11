package com.example.bookie.utils

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.bookie.MyApplication
import com.example.bookie.repositories.BookRepository
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.FutureTask
import kotlin.coroutines.resume


class SuggestionProvider : ContentProvider() {

    private val injector = KodeinInjector()
    private val bookRepository: BookRepository by injector.instance()
//    private val context: Context? = MyApplication.appContext


    companion object {
        val columns: Array<String> = arrayOf(
            "_id",
            "icon_id",  // ID of a drawable (icon) as String
            SearchManager.SUGGEST_COLUMN_TEXT_1,  // main text for suggestion display
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA // book id
        )
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        injector.inject(MyApplication.appKodein)

        return if (p3 != null && p3.isNotEmpty() && p3[0].isNotEmpty()) { // the entered text can be found in selectionArgs[0]
            val cursor = MatrixCursor(columns)
            runBlocking {
                getSuggestions(p3[0]).forEach {
                    cursor.addRow(it)
                }
            }
            cursor
        } else { // user hasn't entered anything
            MatrixCursor(columns)
        }
    }

    private suspend fun getSuggestions(query: String): List<Array<String>> {

        return suspendCancellableCoroutine { continuation ->
            bookRepository.searchRecommendation(
                query
            ) { books ->
                continuation.resume(books.map {
                    arrayOf(
                        "0",
                        "0",
                        it.title,
                        if (it.authors != null) "by ${it.authors.reduce { a1, a2 -> "$a1, $a2" }}" else "",
                        it.id
                    )
                })
            }
        }

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