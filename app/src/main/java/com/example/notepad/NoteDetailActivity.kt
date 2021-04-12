package com.example.notepad

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class NoteDetailActivity : AppCompatActivity() {

    //region ========================================== Val or Var ==========================================

    companion object {
        const val EXTRA_NOTE_ID = "noteID"
    }

    private var note: Note? = null
    private var noteId = 0
    private var isFavorite = 0
    private var isDeleted = 0
    private var date: Calendar? = null
    private var noteDetailActivityDateField = ""

    private var noteDetailActivityTitle: AppCompatEditText? = null
    private var noteDetailActivityContent: AppCompatEditText? = null
    private var noteDetailActivityDate: TextView? = null
    private var noteDetailActivitySpinner: Spinner? = null

    private var noteDao = App.database.noteDao()
    private var listOfNotes: MutableList<Note> = noteDao.getAllNotes()

    private var noteDetailToolbar: Toolbar? = null
    private var noteDetailBottomNavigationView: BottomNavigationView? = null
    private var noteDetailEditionModeBottomNavigationView: BottomNavigationView? = null
    private var noteDetailDeletedBottomNavigationView: BottomNavigationView? = null

    private val mOnNavigationItemSelectedListenerNoteDetail =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.note_detail_bottom_nav_view_menu_share -> {
                    shareNote(note!!)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.note_detail_bottom_nav_view_menu_favorites -> {
                    if (isFavorite == 1) {
                        noteDetailBottomNavigationView!!.menu.setGroupCheckable(1, false, false)
                        menuItem.setIcon(R.drawable.ic_star)
                        menuItem.setTitle(R.string.note_detail_bottom_nav_view_menu_add_to_favorites)
                        isFavorite = 0
                        menuItem.isChecked = false
                    } else {
                        noteDetailBottomNavigationView!!.menu.setGroupCheckable(1, true, false)
                        menuItem.setIcon(R.drawable.ic_full_star)
                        isFavorite = 1
                    }
                    saveNote()

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

    private val mOnNavigationItemSelectedListenerNoteDetailDeleted =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.note_deleted_detail_bottom_nav_view_menu_delete -> {
                    saveConfirmPermanentlyDeleteNoteDialog()
                }
                R.id.note_deleted_detail_bottom_nav_view_menu_restore -> {
                    restoreNote()
                }
            }
            true
        }

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        //region ========================================== Toolbar =========================================

        noteDetailToolbar = findViewById(R.id.activity_note_detail_toolbar)
        setSupportActionBar(noteDetailToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //endregion

        noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1)
        note = noteDao.getNoteById(noteId)

        //region ======================================= FindViewById =======================================

        noteDetailActivityTitle = findViewById(R.id.activity_note_detail_title)
        noteDetailActivityContent = findViewById(R.id.activity_note_detail_content)
        noteDetailActivityDate = findViewById(R.id.activity_note_detail_date)
        noteDetailActivitySpinner = findViewById(R.id.activity_note_detail_spinner)

        //endregion

        noteDetailBottomNavigationView =
            findViewById(R.id.activity_note_detail_bottom_nav_view)
        noteDetailBottomNavigationView!!.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListenerNoteDetail
        )

        noteDetailEditionModeBottomNavigationView =
            findViewById(R.id.activity_note_detail_bottom_nav_view_edition_mode)

        noteDetailDeletedBottomNavigationView =
            findViewById(R.id.activity_note_deleted_detail_bottom_nav_view)
        noteDetailDeletedBottomNavigationView!!.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListenerNoteDetailDeleted
        )

//        noteDetailBottomNavigationView!!.menu.setGroupCheckable(0, false, false)
        noteDetailEditionModeBottomNavigationView!!.menu.setGroupCheckable(0, false, true)
        noteDetailDeletedBottomNavigationView!!.menu.setGroupCheckable(0, false, true)

        val menuItemFavorite =
            noteDetailBottomNavigationView!!.menu.getItem(1)

        if (isFavorite == 1) {
            menuItemFavorite.setIcon(R.drawable.ic_full_star)
            menuItemFavorite.isChecked = true
        } else {
            menuItemFavorite.setIcon(R.drawable.ic_star)
            menuItemFavorite.setTitle(R.string.note_detail_bottom_nav_view_menu_add_to_favorites)
            menuItemFavorite.isChecked = false
        }

        if (note != null) {
            noteDetailActivityTitle!!.setText(note?.title)
            noteDetailActivityContent!!.setText(note?.content)
            noteDetailActivityDate!!.text = note?.date
            isFavorite = note!!.isFavorite

            if (note!!.isDeleted == 1) {
                noteDetailBottomNavigationView!!.visibility = View.GONE
                noteDetailToolbar!!.visibility = View.INVISIBLE
                noteDetailDeletedBottomNavigationView!!.visibility = View.VISIBLE
                noteDetailActivitySpinner!!.visibility = View.INVISIBLE
                noteDetailActivityTitle!!.isEnabled = false
                noteDetailActivityContent!!.isEnabled = false
            }
        } else {
            noteDetailActivityContent!!.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(noteDetailActivityContent, InputMethodManager.SHOW_IMPLICIT)
            getDateTime()

            changeToEditionMode()
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            noteDetailActivitySpinner!!.adapter = adapter
        }

        //region ========================================= Listeners ========================================

        noteDetailActivityTitle!!.setOnClickListener {
            changeToEditionMode()
        }
        noteDetailActivityContent!!.setOnClickListener {
            changeToEditionMode()
        }

        //endregion
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

    private fun changeToEditionMode() {
        if (noteDetailActivityContent!!.requestFocus() || noteDetailActivityTitle!!.requestFocus()) {
            noteDetailBottomNavigationView!!.visibility = View.GONE
            noteDetailEditionModeBottomNavigationView!!.visibility = View.VISIBLE
            noteDetailToolbar!!.menu.setGroupVisible(0, true)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getDateTime() {
        val rightNow = Calendar.getInstance()
        val hour24 = rightNow.get(Calendar.HOUR_OF_DAY)
        val minutes = rightNow.get(Calendar.MINUTE)

        noteDetailActivityDateField = if (minutes < 10 && hour24 < 10) {
            "0$hour24:0$minutes"
        } else if (minutes < 10 && hour24 > 10) {
            "$hour24:0$minutes"
        } else if (minutes > 10 && hour24 < 10) {
            "0$hour24:$minutes"
        } else {
            "$hour24:$minutes"
        }

        val month = when (rightNow.get(Calendar.MONTH)) {
            rightNow.get(Calendar.JANUARY) -> {
                "Janvier"
            }
            rightNow.get(Calendar.FEBRUARY) -> {
                "Février"
            }
            rightNow.get(Calendar.MARCH) -> {
                "Mars"
            }
            rightNow.get(Calendar.APRIL) -> {
                "Avril"
            }
            rightNow.get(Calendar.MAY) -> {
                "Mai"
            }
            rightNow.get(Calendar.JUNE) -> {
                "Juin"
            }
            rightNow.get(Calendar.JULY) -> {
                "Juillet"
            }
            rightNow.get(Calendar.AUGUST) -> {
                "Aout"
            }
            rightNow.get(Calendar.SEPTEMBER) -> {
                "Septembre"
            }
            rightNow.get(Calendar.OCTOBER) -> {
                "Octobre"
            }
            rightNow.get(Calendar.NOVEMBER) -> {
                "Novembre"
            }
            rightNow.get(Calendar.DECEMBER) -> {
                "Décembre"
            }
            else -> {
                "Avril"
            }
        }

        if (date != null) {
            if (date!!.time.day == rightNow.get(Calendar.DAY_OF_MONTH)) {
                noteDetailActivityDate!!.text =
                    "Aujourd'hui à $noteDetailActivityDateField"
            } else {
                noteDetailActivityDate!!.text =
                    "${rightNow.get(Calendar.DAY_OF_MONTH)} $month à $noteDetailActivityDateField"
            }
        } else {
            date = rightNow
            noteDetailActivityDate!!.text =
                "Aujourd'hui à $noteDetailActivityDateField"
        }
    }

    private fun saveNote() {
        getDateTime()
        if (note != null) {
            if (emptyFields(
                    noteDetailActivityTitle!!.text.toString(),
                    noteDetailActivityContent!!.text.toString()
                )
            ) {
                Toast.makeText(this, R.string.note_detail_is_empty, Toast.LENGTH_SHORT).show()
            } else {
                note!!.title = noteDetailActivityTitle!!.text.toString()
                note!!.content = noteDetailActivityContent!!.text.toString()
                note!!.isFavorite = isFavorite
                note!!.isDeleted = isDeleted
                note!!.date = date!!.time.toString()
                noteDao.updateNote(note!!)

                afterSavingNote()
            }
        } else {
            if (emptyFields(
                    noteDetailActivityTitle!!.text.toString(),
                    noteDetailActivityContent!!.text.toString()
                )
            ) {
                Toast.makeText(this, R.string.note_detail_is_empty, Toast.LENGTH_SHORT).show()
            } else {
                note = Note(
                    noteDetailActivityTitle!!.text.toString(),
                    noteDetailActivityContent!!.text.toString(),
                    "",
                    date!!.time.toString(),
                    isFavorite, isDeleted
                )
                noteDao.insertNote(note!!)
                afterSavingNote()
            }
        }
    }

    private fun afterSavingNote() {
//        noteDetailActivityContent!!.focusable = View.NOT_FOCUSABLE
        noteDetailBottomNavigationView!!.visibility = View.VISIBLE
        noteDetailEditionModeBottomNavigationView!!.visibility = View.GONE
        closeKeyboard()
        noteDetailToolbar!!.menu.setGroupVisible(0, false)
//        noteDetailActivityContent!!.focusable = View.FOCUSABLE_AUTO
    }

    private fun intentToNoteListActivity() {
        startActivity(Intent(this, NoteListActivity::class.java))
        finish()
    }

    private fun emptyFields(
        noteDetailActivityTitle: String,
        noteDetailActivityContent: String
    ): Boolean {
        return noteDetailActivityTitle.isEmpty() && noteDetailActivityContent.isEmpty()
    }

    private fun deleteNote() {
        noteDao.deleteNote(note!!)
        intentToNoteListActivity()
    }

    private fun saveConfirmDeleteNoteDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.note_detail_material_builder_delete_note_title))
            .setMessage(getString(R.string.note_detail_material_builder_delete_note_message) + " \"${noteDetailActivityTitle!!.text}\" ?")
            .setPositiveButton(getString(R.string.note_detail_material_builder_delete_note_positive_button)) { _, _ ->
                isDeleted = 1
                saveNote()
                intentToNoteListActivity()
            }
            .setNegativeButton(R.string.note_detail_material_builder_delete_note_negative_button) { dialog, _ ->
                dialog.cancel()
                dialog.dismiss()
            }
            .show()
    }

    private fun saveConfirmPermanentlyDeleteNoteDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.note_detail_material_builder_delete_note_title))
            .setMessage(getString(R.string.note_detail_material_builder_delete_permanently_note_message) + " \"${noteDetailActivityTitle!!.text}\" ?")
            .setPositiveButton(getString(R.string.note_detail_material_builder_delete_note_positive_button)) { _, _ ->
                deleteNote()
            }
            .setNegativeButton(R.string.note_detail_material_builder_delete_note_negative_button) { dialog, _ ->
                dialog.cancel()
                dialog.dismiss()
            }
            .show()
    }

    private fun restoreNote() {
        isDeleted = 0
        saveNote()

        startActivity(Intent(this, NoteListActivity::class.java).putExtra("restoreNote", 0))
        finish()
    }

    private fun shareNote(note: Note) {
        val message = "Title : ${note.title}, content : ${note.content}"
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)

        startActivity(Intent.createChooser(share, "Title of the dialog the system will open"))
    }

    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    //endregion
}