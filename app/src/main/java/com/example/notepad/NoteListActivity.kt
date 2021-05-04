package com.example.notepad

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NoteListActivity : AppCompatActivity(), View.OnClickListener {

    //region ========================================== Val or Var ==========================================

    private lateinit var listOfAllNotes: MutableList<Note>
    private lateinit var listOfFavoritesNotes: MutableList<Note>
    private lateinit var listOfDeletedNotes: MutableList<Note>

    private var fromAllNotes = true
    private var fromFavorites = false
    private var fromDeletedNotes = false

    private lateinit var sortByTitle: MenuItem
    private lateinit var sortByFavorite: MenuItem
    private lateinit var sortByDate: MenuItem

    private lateinit var adapter: RecyclerViewNoteAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBarEditText: AppCompatEditText

    private var bottomNavigationView: BottomNavigationView? = null
    private var floatingButtonAddNewNote: FloatingActionButton? = null
    private var floatingButtonDeleteTrash: FloatingActionButton? = null

    private var sharedPref: SharedPreferences? = null

    private var noteDao = App.database.noteDao()

    private lateinit var menuItemListFavorites: MenuItem

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.activity_note_list_bottom_nav_view_notes -> {
                    val editor: SharedPreferences.Editor = sharedPref!!.edit()
                    editor.putInt("bottomNavigationView", 0)
                    editor.apply()
                    editor.commit()

                    toAllNotes()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_favorites -> {
                    val editor: SharedPreferences.Editor = sharedPref!!.edit()
                    editor.putInt("bottomNavigationView", 1)
                    editor.apply()
                    editor.commit()
                    menuItem.setIcon(R.drawable.ic_full_star)

                    toFavorites()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_trash -> {
                    val editor: SharedPreferences.Editor = sharedPref!!.edit()
                    editor.putInt("bottomNavigationView", 2)
                    editor.apply()
                    editor.commit()

                    toDeletedNotes()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_categories -> {
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        //region ======================================== getNoteDAO ========================================

        listOfAllNotes = noteDao.getAllNotes()
        listOfFavoritesNotes = noteDao.getFavoritesNotes()
        listOfDeletedNotes = noteDao.getDeletedNotes()

        //endregion

        sharedPref = getSharedPreferences("bottomNavigationView", Context.MODE_PRIVATE)
        val bottomNavPosition = sharedPref!!.getInt("bottomNavigationView", 0)

        //region ======================================== Toolbar ========================================

        val toolbar = findViewById<Toolbar>(R.id.activity_note_list_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //endregion

        //region ======================================= FindViewById =======================================

        recyclerView = findViewById(R.id.activity_note_list_recyclerview)
        floatingButtonAddNewNote = findViewById(R.id.activity_note_list_add_new_note)
        floatingButtonDeleteTrash = findViewById(R.id.activity_note_list_delete_trash)
        bottomNavigationView = findViewById(R.id.activity_note_list_bottom_nav_view)
        searchBarEditText = findViewById(R.id.activity_note_list_search)

        //endregion

        val sharedPreferences = getSharedPreferences("Sort_by", Context.MODE_PRIVATE)
        when (sharedPreferences.getString("tri", "date")) {
            "title" -> {
                adapter = if (fromFavorites) {
                    RecyclerViewNoteAdapter(
                        noteDao.getAllFavoriteNotesOrderByTitleAZ(),
                        this
                    )
                } else {
                    RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByTitleAZ(), this)
                }
            }
            "favorite" -> {
                adapter = RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByFavoriteAZ(), this)
            }
            "date" -> {
                adapter = if (fromFavorites) {
                    RecyclerViewNoteAdapter(
                        noteDao.getAllFavoriteNotesOrderByDateAZ(),
                        this
                    )
                } else {
                    RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByDateAZ(), this)
                }
            }
            else -> RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByDateAZ(), this)
        }

//        adapter = RecyclerViewNoteAdapter(listOfAllNotes, this)
        recyclerViewInit(adapter)

        val fromRestoreNote = intent.getIntExtra("restoreNote", 1)
        if (fromRestoreNote == 0) {
            bottomNavigationView!!.menu.getItem(fromRestoreNote).isChecked = true
        } else {
            bottomNavigationView!!.menu.getItem(bottomNavPosition).isChecked = true
        }

        val menu = bottomNavigationView!!.menu
        val navItem = menu.findItem(R.id.activity_note_list_bottom_nav_view_notes)
        navItem.isChecked = true
        menuItemListFavorites = menu.findItem(R.id.activity_note_list_bottom_nav_view_favorites)

        //region ========================================= Listeners ========================================

        bottomNavigationView!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        floatingButtonAddNewNote!!.setOnClickListener(this)
        floatingButtonDeleteTrash!!.setOnClickListener(this)

        searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
//                listOfAllNotes = noteDao.getAllNotes()
//                listOfFavoritesNotes = noteDao.getFavoritesNotes()
//                listOfDeletedNotes = noteDao.getDeletedNotes()
//
//                main_search_bar_value = main_SearchBar!!.text.toString()
//
//                val filteredList = gestionnaireContacts!!.getContactConcernByFilter(main_filter, main_search_bar_value)
//                val contactListDb = ContactManager(this@MainActivity)
//
////                if (sharedPref.getString("tri", "nom") == "nom") {
////                    contactListDb.sortContactByFirstNameAZ()
////                    contactListDb.contactList.retainAll(filteredList)
////                } else {
////                    contactListDb.sortContactByPriority()
////                    contactListDb.contactList.retainAll(filteredList)
////                }
//                gestionnaireContacts!!.contactList.clear()
//                gestionnaireContacts!!.contactList.addAll(contactListDb.contactList)
//
//
//                adapter = RecyclerViewNoteAdapter(listOfAllNotes, this)
//                recyclerViewInit(adapter)
            }

            override fun afterTextChanged(s: Editable) {

            }


        })

        //endregion
    }

    //region =========================================== Override ===========================================

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_list_toolbar_menu, menu)
        sortByTitle = menu!!.findItem(R.id.sort_by_title)
        sortByFavorite = menu.findItem(R.id.sort_by_favorite)
        sortByDate = menu.findItem(R.id.sort_by_date)

        val sharedPreferences = getSharedPreferences("Sort_by", Context.MODE_PRIVATE)
        when (sharedPreferences.getString("tri", "date")) {
            "title" -> sortByTitle.isChecked = true
            "favorite" -> sortByFavorite.isChecked = true
            "date" -> sortByDate.isChecked = true
            else -> sortByDate.isChecked = true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sharedPreferences = getSharedPreferences("Sort_by", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        when (item.itemId) {
            R.id.sort_by_title -> {
                if (!item.isChecked) {
                    adapter = when {
                        fromFavorites -> RecyclerViewNoteAdapter(
                            noteDao.getAllFavoriteNotesOrderByTitleAZ(),
                            this
                        )
                        fromDeletedNotes -> RecyclerViewNoteAdapter(
                            noteDao.getDeletedNotesOrderByTitleAZ(),
                            this
                        )
                        else -> RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByTitleAZ(), this)
                    }
                    recyclerViewInit(adapter)
                    edit.putString("Sort_by", "title")
                    edit.apply()
                    item.isChecked = true
                }
            }
            R.id.sort_by_favorite -> {
                adapter = when {
                    fromFavorites -> RecyclerViewNoteAdapter(
                        noteDao.getAllFavoriteNotesOrderByTitleAZ(),
                        this
                    )
                    fromDeletedNotes -> RecyclerViewNoteAdapter(
                        noteDao.getDeletedNotesOrderByFavoriteAZ(),
                        this
                    )
                    else -> RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByFavoriteAZ(), this)
                }
                recyclerViewInit(adapter)
                edit.putString("Sort_by", "favorite")
                edit.apply()
                item.isChecked = true
            }
            R.id.sort_by_date -> {
                adapter = when {
                    fromFavorites -> RecyclerViewNoteAdapter(
                        noteDao.getAllFavoriteNotesOrderByDateAZ(),
                        this
                    )
                    fromDeletedNotes -> RecyclerViewNoteAdapter(
                        noteDao.getDeletedNotesOrderByDateAZ(),
                        this
                    )
                    else -> RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByDateAZ(), this)
                }
                recyclerViewInit(adapter)
                edit.putString("Sort_by", "date")
                edit.apply()
                item.isChecked = true
            }
        }
        hideKeyboard()
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        if (v?.tag != null) {
            when {
                fromAllNotes -> {
                    showNoteDetail(v.tag as Int, listOfAllNotes)
                }
                fromFavorites -> {
                    showNoteDetail(v.tag as Int, listOfFavoritesNotes)
                }
                fromDeletedNotes -> {
                    showNoteDetail(v.tag as Int, listOfDeletedNotes)
                }
            }
        } else {
            when (v?.id) {
                R.id.activity_note_list_add_new_note -> createNewNote()
                R.id.activity_note_list_delete_trash -> {
                    confirmDeleteAllNotesDialog()
                }
            }
        }
    }

    //endregion

    //region =========================================== Functions ==========================================

    private fun confirmDeleteAllNotesDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.note_list_alert_dialog_delete_all_notes_title))
            .setMessage(getString(R.string.note_list_alert_dialog_delete_all_notes_subtitle))
            .setPositiveButton(getString(R.string.note_list_alert_dialog_delete_all_notes_positive_button)) { _, _ ->

                noteDao.deleteNotes(listOfDeletedNotes)
                refreshActivity()
            }
            .setNegativeButton(R.string.note_list_alert_dialog_delete_all_notes_negative_button) { dialog, _ ->
                dialog.cancel()
                dialog.dismiss()
            }
            .show()
    }

    private fun refreshActivity() {
        startActivity(Intent(this, NoteListActivity::class.java))
        finish()
    }

    private fun showNoteDetail(noteIndex: Int, notes: MutableList<Note>) {
        val note = notes[noteIndex]

        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
        startActivity(intent)
    }

    private fun createNewNote() {
        val intent = Intent(this, NoteDetailActivity::class.java)
        startActivity(intent)
    }

    private fun recyclerViewInit(recyclerViewNoteAdapter: RecyclerViewNoteAdapter) {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = recyclerViewNoteAdapter
        recyclerViewNoteAdapter.notifyDataSetChanged()
    }

    private fun toFavorites() {
        adapter = RecyclerViewNoteAdapter(listOfFavoritesNotes, this)
        recyclerViewInit(adapter)
        menuItemListFavorites.setIcon(R.drawable.ic_full_star)

        fromAllNotes = false
        fromFavorites = true
        fromDeletedNotes = false
        sortByFavorite.isVisible = false
        floatingButtonAddNewNote!!.visibility = View.VISIBLE
        floatingButtonDeleteTrash!!.visibility = View.GONE
    }

    private fun toAllNotes() {
        adapter = RecyclerViewNoteAdapter(listOfAllNotes, this)
        recyclerViewInit(adapter)
        menuItemListFavorites.setIcon(R.drawable.ic_star)

        fromAllNotes = true
        fromFavorites = false
        fromDeletedNotes = false
        sortByFavorite.isVisible = true
        floatingButtonAddNewNote!!.visibility = View.VISIBLE
        floatingButtonDeleteTrash!!.visibility = View.GONE
    }

    private fun toDeletedNotes() {
        adapter = RecyclerViewNoteAdapter(listOfDeletedNotes, this)
        recyclerViewInit(adapter)
        menuItemListFavorites.setIcon(R.drawable.ic_star)

        fromAllNotes = false
        fromFavorites = false
        fromDeletedNotes = true
        sortByFavorite.isVisible = true
        floatingButtonAddNewNote!!.visibility = View.GONE
        floatingButtonDeleteTrash!!.visibility = View.VISIBLE
    }

//    fun getNoteByFilterSearchBar(filterList: ArrayList<String>, name: String): ArrayList<Note> {
//        val contactFilterList: ArrayList<Note>? = getAllContactFilter(filterList)
//        val contactList = getContactByName(name)
//        if (contactFilterList != null) {
//            return intersectContactWithAllInformation(contactList, contactFilterList)
//        }
//        return contactList
//    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //endregion
}