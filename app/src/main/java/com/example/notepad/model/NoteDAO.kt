package com.example.notepad.model

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
     * dao.getAllNotesOrderByTitleAZ()
     */
    @Query("SELECT * FROM note WHERE isDeleted == 0 ORDER BY title COLLATE NOCASE ASC")
    fun getAllNotesOrderByTitleAZ(): MutableList<Note>

    /**
     * Usage:
     * dao.getAllNotesOrderByFavoriteAZ()
     */
    @Query("SELECT * FROM note WHERE isDeleted == 0 ORDER BY isFavorite DESC")
    fun getAllNotesOrderByFavoriteAZ(): MutableList<Note>

    /**
     * Usage:
     * dao.getAllNotesOrderByDateAZ()
     */
    @Query("SELECT * FROM note WHERE isDeleted == 0  ORDER BY date DESC")
    fun getAllNotesOrderByDateAZ(): MutableList<Note>

    /**
     * Usage:
     * dao.getAllFavoriteNotesOrderByTitleAZ()
     */
    @Query("SELECT * FROM note WHERE isFavorite == 1 AND isDeleted == 0 ORDER BY title COLLATE NOCASE ASC")
    fun getAllFavoriteNotesOrderByTitleAZ(): MutableList<Note>

    /**
     * Usage:
     * dao.getAllFavoriteNotesOrderByDateAZ()
     */
    @Query("SELECT * FROM note WHERE isFavorite == 1 AND isDeleted == 0 ORDER BY date DESC")
    fun getAllFavoriteNotesOrderByDateAZ(): MutableList<Note>

    /**
     * Usage:
     * dao.getDeletedNotesOrderByTitleAZ()
     */
    @Query("SELECT * FROM note WHERE isDeleted == 1 ORDER BY title COLLATE NOCASE ASC")
    fun getDeletedNotesOrderByTitleAZ(): MutableList<Note>

    /**
     * Usage:
     * dao.getDeletedNotesOrderByFavoriteAZ()
     */
    @Query("SELECT * FROM note WHERE isDeleted == 1 ORDER BY isFavorite DESC")
    fun getDeletedNotesOrderByFavoriteAZ(): MutableList<Note>

    /**
     * Usage:
     * dao.getDeletedNotesOrderByDateAZ()
     */
    @Query("SELECT * FROM note WHERE isDeleted == 1 ORDER BY date ASC")
    fun getDeletedNotesOrderByDateAZ(): MutableList<Note>

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
     * dao.updateNotes(note)
     */
    @Update
    fun updateNotes(notes: MutableList<Note>)

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
    fun deleteNotes(notes: MutableList<Note>)
}