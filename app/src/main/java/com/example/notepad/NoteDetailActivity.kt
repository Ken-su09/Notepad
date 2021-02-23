package com.example.notepad

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar

class NoteDetailActivity : AppCompatActivity() {

    //region ========================================== Val or Var ==========================================

    companion object {
        const val EXTRA_NOTE_ID = "noteID"
    }

    private var note: Note? = null
    private var noteId = 0

    private var noteDetailActivityTitle: AppCompatEditText? = null
    private var noteDetailActivityContent: AppCompatEditText? = null
    private var noteDetailActivitySpinner: Spinner? = null

    private var noteDao = App.database.noteDao()
    private var listOfNotes: MutableList<Note> = noteDao.getAllNotes()

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        //region ========================================== Toolbar =========================================

        val toolbar = findViewById<Toolbar>(R.id.activity_note_detail_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //endregion

        noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1)
        note = noteDao.getNoteById(noteId)

        //region ======================================= FindViewById =======================================

        noteDetailActivityTitle = findViewById(R.id.activity_note_detail_title)
        noteDetailActivityContent = findViewById(R.id.activity_note_detail_content)
        noteDetailActivitySpinner = findViewById(R.id.activity_note_detail_spinner)

        //endregion

        if (note != null) {
            noteDetailActivityTitle!!.setText(note?.title)
            noteDetailActivityContent!!.setText(note?.content)
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            noteDetailActivitySpinner!!.adapter = adapter
        }
    }

    //region =========================================== Override ===========================================

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_detail_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.note_detail_toolbar_menu_validate -> {
                saveNote()
            }
            R.id.note_detail_toolbar_menu_delete -> {
                saveConfirmDeleteNoteDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //endregion

    //region =========================================== Functions ==========================================

    private fun saveNote() {
        if (note != null) {
            note!!.title = noteDetailActivityTitle!!.text.toString()
            note!!.content = noteDetailActivityContent!!.text.toString()
            noteDao.updateNote(note!!)
        } else {
            note = Note(
                listOfNotes.size + 1,
                noteDetailActivityTitle!!.text.toString(),
                noteDetailActivityContent!!.text.toString()
            )
            noteDao.insertNote(note!!)
        }

        startActivity(Intent(this, NoteListActivity::class.java).putExtra(EXTRA_NOTE_ID, noteId))
        finish()
    }

    private fun deleteNote() {
        noteDao.deleteNote(note!!)
        startActivity(Intent(this, NoteListActivity::class.java))
        finish()
    }

    private fun saveConfirmDeleteNoteDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.note_detail_material_builder_delete_note_title))
            .setMessage(getString(R.string.note_detail_material_builder_delete_note_message) + " \"${noteDetailActivityTitle!!.text}\" ?")
            .setPositiveButton(getString(R.string.note_detail_material_builder_delete_note_positive_button)) { _, _ ->
                deleteNote()
            }
            .setNegativeButton(R.string.note_detail_material_builder_delete_note_negative_button) { dialog, _ ->
                dialog.cancel()
                dialog.dismiss()
            }
            .show()
    }

    //endregion
}