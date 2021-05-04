package com.example.notepad

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class NoteDetailActivity : AppCompatActivity(), View.OnClickListener {

    //region ========================================== Val or Var ==========================================

    companion object {
        const val EXTRA_NOTE_ID = "noteID"
    }

    private var note: Note? = null
    private var noteId = 0
    private var isFavorite = 0
    private var isDeleted = 0
    private var date = Calendar.getInstance()

    private var noteDetailActivityTitle: AppCompatEditText? = null
    private var noteDetailActivityContent: AppCompatEditText? = null
    private var noteDetailActivityDate: TextView? = null
    private var noteDetailActivitySpinner: Spinner? = null

    private var noteDao = App.database.noteDao()
    private var listOfNotes: MutableList<Note> = noteDao.getAllNotes()

    private var noteDetailToolbar: Toolbar? = null
    private var noteDetailToolbarMenu: Menu? = null
    private var noteDetailBottomNav: LinearLayout? = null
    private var noteDetailEditionModeBottomNav: BottomNavigationView? = null
    private var noteDetailDeletedBottomNav: BottomNavigationView? = null

    private lateinit var noteDetailBottomNavItemShare: RelativeLayout
    private lateinit var noteDetailBottomNavItemFav: RelativeLayout
    private lateinit var noteDetailBottomNavItemFavImage: AppCompatImageView
    private lateinit var noteDetailBottomNavItemFavText: TextView
    private lateinit var noteDetailBottomNavItemTrash: RelativeLayout
    private lateinit var noteDetailBottomNavItemPrint: RelativeLayout


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

        noteDetailBottomNavItemShare = findViewById(R.id.note_detail_bottom_nav_share)
        noteDetailBottomNavItemFav = findViewById(R.id.note_detail_bottom_nav_favorite)
        noteDetailBottomNavItemFavImage = findViewById(R.id.note_detail_bottom_nav_favorite_image)
        noteDetailBottomNavItemFavText = findViewById(R.id.note_detail_bottom_nav_favorite_text)
        noteDetailBottomNavItemTrash = findViewById(R.id.note_detail_bottom_nav_trash)
        noteDetailBottomNavItemPrint = findViewById(R.id.note_detail_bottom_nav_print)

        noteDetailBottomNav = findViewById(R.id.note_detail_bottom_nav)
        noteDetailEditionModeBottomNav = findViewById(R.id.note_detail_bottom_nav_edition_mode)
        noteDetailDeletedBottomNav = findViewById(R.id.note_deleted_detail_bottom_nav)

        //endregion

        noteDetailDeletedBottomNav!!.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListenerNoteDetailDeleted
        )

        noteDetailEditionModeBottomNav!!.menu.setGroupCheckable(0, false, false)
        noteDetailDeletedBottomNav!!.menu.setGroupCheckable(0, false, true)

        if (note != null) {
            noteDetailActivityTitle!!.setText(note?.title)
            noteDetailActivityContent!!.setText(note?.content)
            noteDetailActivityDate!!.text = note?.date
            isFavorite = note!!.isFavorite

            if (note!!.isDeleted == 1) {
                noteDetailBottomNav!!.visibility = View.GONE
                noteDetailToolbar!!.visibility = View.INVISIBLE
                noteDetailDeletedBottomNav!!.visibility = View.VISIBLE
                noteDetailActivitySpinner!!.visibility = View.INVISIBLE
                noteDetailActivityTitle!!.isEnabled = false
                noteDetailActivityContent!!.isEnabled = false
            }
        } else {
            changeToEditionMode()
            convertDate(date)
        }

        initFavoriteImageAndText()

        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            noteDetailActivitySpinner!!.adapter = adapter
        }

        //region ========================================= Listeners ========================================

        noteDetailActivityContent!!.setOnFocusChangeListener { _, _ ->
            changeToEditionMode()
//            noteDetailActivityTitle!!.clearFocus()
        }

        noteDetailActivityTitle!!.setOnFocusChangeListener { _, _ ->
//            changeToEditionMode()
//            noteDetailActivityContent!!.clearFocus()
        }

//        noteDetailActivityContent!!.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                changeToEditionMode()
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                changeToEditionMode()
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                changeToEditionMode()
//            }
//        })

        noteDetailBottomNavItemShare.setOnClickListener(this)
        noteDetailBottomNavItemFav.setOnClickListener(this)
        noteDetailBottomNavItemTrash.setOnClickListener(this)
        noteDetailBottomNavItemPrint.setOnClickListener(this)

        //endregion
    }

    //region =========================================== Override ===========================================

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_detail_toolbar_menu, menu)
        noteDetailToolbarMenu = menu!!

        noteDetailToolbarMenu!!.setGroupVisible(R.id.note_detail_toolbar_menu_group, note == null)
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

    private fun convertDate(date: Calendar): String {
        val rightNow = Calendar.getInstance()
        val hour24 = rightNow.get(Calendar.HOUR_OF_DAY)
        val minutes = rightNow.get(Calendar.MINUTE)

        val hour = if (minutes < 10 && hour24 < 10) {
            "0$hour24:0$minutes"
        } else if (minutes < 10 && hour24 > 10) {
            "$hour24:0$minutes"
        } else if (minutes > 10 && hour24 < 10) {
            "0$hour24:$minutes"
        } else {
            "$hour24:$minutes"
        }

        val day = when {
            date.time.toString().contains("Mon") -> {
                "Lundi"
            }
            date.time.toString().contains("Tue") -> {
                "Mardi"
            }
            date.time.toString().contains("Wed") -> {
                "Mercredi"
            }
            date.time.toString().contains("Thu") -> {
                "Jeudi"
            }
            date.time.toString().contains("Fri") -> {
                "Vendredi"
            }
            date.time.toString().contains("Sat") -> {
                "Samedi"
            }
            date.time.toString().contains("Sun") -> {
                "Dimanche"
            }
            else -> {
                "Lundi"
            }
        }

        val month = when {
            date.time.toString().contains("Jan") -> {
                "Janvier"
            }
            date.time.toString().contains("Feb") -> {
                "Février"
            }
            date.time.toString().contains("Mar") -> {
                "Mars"
            }
            date.time.toString().contains("Apr") -> {
                "Avril"
            }
            date.time.toString().contains("May") -> {
                "Mai"
            }
            date.time.toString().contains("Jun") -> {
                "Juin"
            }
            date.time.toString().contains("Jul") -> {
                "Juillet"
            }
            date.time.toString().contains("Aug") -> {
                "Aout"
            }
            date.time.toString().contains("Sep") -> {
                "Septembre"
            }
            date.time.toString().contains("Oct") -> {
                "Octobre"
            }
            date.time.toString().contains("Nov") -> {
                "Novembre"
            }
            date.time.toString().contains("Dec") -> {
                "Décembre"
            }
            else -> {
                "Avril"
            }
        }

        val year = when {
            date.time.toString().contains("2021") -> {
                "2021"
            }
            date.time.toString().contains("2022") -> {
                "2022"
            }
            else -> {
                "2023"
            }
        }

        noteDetailActivityDate!!.text =
            "$day ${date.time.day} $month $year à $hour"

        return "$day ${date.time.day} $month $year à $hour"
    }

    private fun initFavoriteImageAndText() {
        if (isFavorite == 1) {
            noteDetailBottomNavItemFavImage.setBackgroundResource(R.drawable.ic_full_star)
            noteDetailBottomNavItemFavText.setTextColor(Color.parseColor("#037dff"))
        } else {
            noteDetailBottomNavItemFavImage.setBackgroundResource(R.drawable.ic_star)
            noteDetailBottomNavItemFavText.setTextColor(Color.parseColor("#000000"))
        }
    }

    private fun changeToEditionMode() {
        noteDetailBottomNav!!.visibility = View.GONE
        noteDetailEditionModeBottomNav!!.visibility = View.VISIBLE

        if (noteDetailToolbarMenu != null) {
            noteDetailToolbarMenu!!.setGroupVisible(R.id.note_detail_toolbar_menu_group, true)
        }
    }

    private fun saveNote() {
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
                note!!.date = convertDate(date)
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
                    convertDate(date),
                    isFavorite, isDeleted
                )
                noteDao.insertNote(note!!)
                afterCreateNote()
            }
        }
    }

    private fun afterSavingNote() {
        noteDetailBottomNav!!.visibility = View.VISIBLE
        noteDetailEditionModeBottomNav!!.visibility = View.GONE
        noteDetailToolbarMenu!!.setGroupVisible(R.id.note_detail_toolbar_menu_group, false)
        closeKeyboard()
        refreshActivity()
//        noteDetailActivityContent!!.clearFocus()
    }

    private fun afterCreateNote() {
        noteDetailBottomNav!!.visibility = View.VISIBLE
        noteDetailEditionModeBottomNav!!.visibility = View.GONE
        noteDetailToolbarMenu!!.setGroupVisible(R.id.note_detail_toolbar_menu_group, false)
        closeKeyboard()
//        noteDetailActivityContent!!.clearFocus()
    }

    private fun refreshActivity() {
        startActivity(Intent(this, NoteDetailActivity::class.java).putExtra(EXTRA_NOTE_ID, noteId))
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
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(noteDetailActivityContent!!.windowToken, 0)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.note_detail_bottom_nav_share -> {
                shareNote(note!!)
            }
            R.id.note_detail_bottom_nav_favorite -> {
                isFavorite = if (isFavorite == 1) {
                    noteDetailBottomNavItemFavImage.setBackgroundResource(R.drawable.ic_star)
                    noteDetailBottomNavItemFavText.setTextColor(Color.parseColor("#000000"))
                    0
                } else {
                    noteDetailBottomNavItemFavImage.setBackgroundResource(R.drawable.ic_full_star)
                    noteDetailBottomNavItemFavText.setTextColor(Color.parseColor("#037dff"))
                    1
                }
                saveNote()
            }
            R.id.note_detail_bottom_nav_trash -> {
                saveConfirmDeleteNoteDialog()
            }
            R.id.note_detail_bottom_nav_print -> {
            }
        }
    }

    //endregion
}