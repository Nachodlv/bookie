package com.example.bookie.utils

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.bookie.MyApplication
import com.example.bookie.models.UserPreview
import com.example.bookie.repositories.BookRepository
import com.example.bookie.repositories.UserRepository
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.FutureTask
import kotlin.coroutines.resume


class FollowingsSuggestionProvider : ContentProvider() {

    private val injector = KodeinInjector()
    private val userRepository: UserRepository by injector.instance()


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
            userRepository.searchUserWithCallbacks(query, 0, 5, { users ->
                continuation.resume(users.map {
                    arrayOf(
                        "0",
                        "0",
                        "${it.firstName} ${it.lastName}",
                        "",
                        it.id
                    )
                })
            }, {})

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