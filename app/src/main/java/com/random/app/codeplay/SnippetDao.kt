package com.random.app.codeplay

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

/**
 * Created by root on 9/6/18.
 */
@Dao
abstract class SnippetDao {

    @Query("Select * From Snippet")
    abstract fun getAllSnippets(): List<Snippet>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveSnippet(snippet: Snippet): Long

    @Query("Delete From Snippet")
    abstract fun nuke()
}