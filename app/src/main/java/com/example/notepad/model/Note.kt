package com.example.notepad.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    /**
     * Titre de la note.
     */
    @ColumnInfo(name = "title") var title: String = "",
    /**
     * Contenu de la note.
     */
    @ColumnInfo(name = "content") var content: String = "",
    var filename: String = "",
    /**
     * Date de la note.
     */
    @ColumnInfo(name = "date") var date: String = "",
    /**
     * 1 si la note est favorite, sinon 0.
     */
    @ColumnInfo(name = "isFavorite") var isFavorite: Int,
    /**
     * 0 si la note est supprimée temporairement.
     */
    @ColumnInfo(name = "isDeleted") var isDeleted: Int = 0,

    /**
     * Id de la note.
     */
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)