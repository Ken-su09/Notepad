package com.example.notepad

import androidx.room.*

@Dao
interface NoteDAO {

    /**
     * Usage:
     * dao.getAllNotes()
     */
    @Query("SELECT * FROM note WHERE isDeleted == 0")
    fun getAllNotes(): MutableList<Note>

    /**
     * Usage:
     * dao.getNoteById(1)
     */
    @Query("SELECT * FROM note WHERE id == :noteId")
    fun getNoteById(noteId: Int): Note

    /**
     * Usage:
     * dao.getFavoritesNotes()
     */
    @Query("SELECT * FROM note WHERE isFavorite == 1 AND isDeleted == 0")
    fun getFavoritesNotes(): MutableList<Note>

    /**
     * Usage:
     * dao.getDeletedNotes()
     */
    @Query("SELECT * FROM note WHERE isDeleted == 1")
    fun getDeletedNotes(): MutableList<Note>

    /**
     * Usage:
     * dao.insertNote(note)
     */
    @Insert
    fun insertNote(note: Note)

    /**
     * Usage:
     * notes = mutableListOf(note1, note2, note3.....)
     * dao.insertNotes(notes)
     */
    @Insert
    fun insertNotes(notes: MutableList<Note>)

    /**
     * Usage:
     * dao.updateNote(note)
     */
    @Update
    fun updateNote(note: Note)

    /**
     * Usage:
     * notes = mutableListOf(note1, note2, note3.....)
     * val rows = dao.updateNotes(notes)
     */
    @Update
    fun updateNotes(notes: MutableList<Note>): Int

    /**
     * Usage:
     * dao.deleteNote(note)
     */
    @Delete
    fun deleteNote(note: Note)

    /**
     * Usage:
     * notes = mutableListOf(note1, note2, note3.....)
     * dao.updateNotes(notes)
     */
    @Delete
    fun deleteNotes(notes: Note)
}