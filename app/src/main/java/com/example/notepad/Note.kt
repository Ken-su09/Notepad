package com.example.notepad

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    var title: String = "",
    var content: String = "",
    var filename: String = "",
    var date: String = "",
    var isFavorite: Int,
    var isDeleted: Int = 0,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)