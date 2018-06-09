package com.random.app.codeplay

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by root on 9/6/18.
 */
@Database(entities = [Snippet::class],version = 1)
abstract class CodePlayDB: RoomDatabase() {
    abstract fun getSnippetDao(): SnippetDao
}