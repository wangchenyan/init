package me.wcy.arch.module

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Created by wcy on 2020/12/10.
 */
class ArchProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        Arch.init(context!!.applicationContext)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}