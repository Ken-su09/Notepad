package com.example.notepad

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String = "",
    var content: String = "",
    var filename: String = "",
    var date: String = "",
    var isFavorite: Int
)