package com.random.app.codeplay

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by root on 9/6/18.
 */
@Entity
data class Snippet(@PrimaryKey var snippetName: String,
                   var snippetCode: String)