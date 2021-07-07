package com.example.notepad.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "category",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("id_note"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class Category(
    /**
     * Titre de la catégorie.
     */
    @ColumnInfo(name = "title")
    var title: String = "",
    /**
     * Couleur de la catégorie.
     */
    @ColumnInfo(name = "color")
    var color: String = "",
    /**
     * Id de la note.
     */
    @ColumnInfo(name = "id_note")
    var id_note: Int = 0,
    /**
     * Id de la catégorie
     */
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)
