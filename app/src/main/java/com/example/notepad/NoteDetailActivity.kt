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
import com.google.android.material.bottomnavigation.BottomNavigationView

class NoteDetailActivity : AppCompatActivity() {

    //region ========================================== Val or Var ==========================================

    companion object {
        const val EXTRA_NOTE_ID = "noteID"
    }

    private var note: Note? = null
    private var noteId = 0
    private var isFavorite = 0

    private var noteDetailActivityTitle: AppCompatEditText? = null
    private var noteDetailActivityContent: AppCompatEditText? = null
    private var noteDetailActivitySpinner: Spinner? = null

    private var noteDao = App.database.noteDao()
    private var listOfNotes: MutableList<Note> = noteDao.getAllNotes()

    private var navAddToFavoritesItem: MenuItem? = null
    private var navRemoveFromFavoritesItem: MenuItem? = null
    private var bottomNavigationView: BottomNavigationView? = null

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.note_detail_bottom_nav_view_menu_share -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.note_detail_bottom_nav_view_menu_add_to_favorites -> {
                    addNoteToFavorites()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.note_detail_bottom_nav_view_menu_remove_from_favorites -> {
                    removeNoteFromFavorites()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.note_detail_bottom_nav_view_menu_trash -> {
                    saveConfirmDeleteNoteDialog()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.note_detail_bottom_nav_view_menu_print -> {
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

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

        bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.activity_note_detail_bottom_nav_view)
        bottomNavigationView!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val menu = bottomNavigationView!!.menu
        navAddToFavoritesItem =
            menu.findItem(R.id.note_detail_bottom_nav_view_menu_add_to_favorites)
        navRemoveFromFavoritesItem =
            menu.findItem(R.id.note_detail_bottom_nav_view_menu_remove_from_favorites)

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
            R.id.note_detail_toolbar_menu_undo -> {
            }
            R.id.note_detail_toolbar_menu_redo -> {
            }
            R.id.note_detail_toolbar_menu_validate -> {
                saveNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //endregion

    //region =========================================== Functions ==========================================

    private fun saveNote() {
        if (noteDetailActivityTitle!!.text.toString().isEmpty()) {
            if (noteDetailActivityContent!!.text.toString().length > 15) {
                noteDetailActivityTitle!!.setText(
                    noteDetailActivityContent!!.text.toString().take(15) + "..."
                )
            } else {
                noteDetailActivityTitle!!.setText(
                    noteDetailActivityContent!!.text.toString().take(15)
                )
            }
        }

        if (note != null) {
            note!!.title = noteDetailActivityTitle!!.text.toString()
            note!!.content = noteDetailActivityContent!!.text.toString()
            noteDao.updateNote(note!!)
        } else {
            note = Note(
                listOfNotes.size + 1,
                noteDetailActivityTitle!!.text.toString(),
                noteDetailActivityContent!!.text.toString(),
                "",
                java.util.Calendar.getInstance().toString(),
                isFavorite
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

    private fun addNoteToFavorites() {
        navAddToFavoritesItem!!.isVisible = false
        navRemoveFromFavoritesItem!!.isVisible = true
        navRemoveFromFavoritesItem!!.isChecked = true
        bottomNavigationView!!.menu.getItem(2).isChecked = true
    }

    private fun removeNoteFromFavorites() {
        navRemoveFromFavoritesItem!!.isVisible = false
        navRemoveFromFavoritesItem!!.isChecked = false
        navAddToFavoritesItem!!.isVisible = true
        bottomNavigationView!!.menu.getItem(2).isChecked = false
    }

    //endregion
}