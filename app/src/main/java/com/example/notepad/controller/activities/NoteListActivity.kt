package com.example.notepad.controller.activities

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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.*
import com.example.notepad.controller.adapters.RecyclerViewNoteAdapter
import com.example.notepad.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

class NoteListActivity : AppCompatActivity(), View.OnClickListener {

    //region ========================================== Val or Var ==========================================

    private var fromAllNotes = true
    private var fromFavorites = false
    private var fromDeletedNotes = false

    private lateinit var noteDao: NoteDAO
    private lateinit var categoryDAO: CategoryDAO

    private lateinit var sortByDate: MenuItem
    private lateinit var sortByTitle: MenuItem
    private lateinit var sortByFavorite: MenuItem
    private lateinit var sortByNone: MenuItem
    private lateinit var menuItemListFavorites: MenuItem

    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPref: SharedPreferences
    private lateinit var adapter: RecyclerViewNoteAdapter

    private lateinit var searchBarEditText: AppCompatEditText

    private lateinit var listOfAllCategories: MutableList<Category>
    private lateinit var listOfAllNotes: MutableList<Note>
    private lateinit var listOfDeletedNotes: MutableList<Note>
    private lateinit var listOfFavoritesNotes: MutableList<Note>

    private var bottomNavigationView: BottomNavigationView? = null
    private var floatingButtonAddNewNote: FloatingActionButton? = null
    private var floatingButtonDeleteTrash: FloatingActionButton? = null

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.activity_note_list_bottom_nav_view_notes -> {
                    val editor: SharedPreferences.Editor = sharedPref.edit()
                    editor.putInt("bottomNavigationView", 0)
                    editor.apply()
                    editor.commit()

                    searchBarEditText.text!!.clear()
                    toAllNotes()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_favorites -> {
                    val editor: SharedPreferences.Editor = sharedPref.edit()
                    editor.putInt("bottomNavigationView", 1)
                    editor.apply()
                    editor.commit()
                    menuItem.setIcon(R.drawable.ic_full_star)

                    searchBarEditText.text!!.clear()

                    toFavorites()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_trash -> {
                    val editor: SharedPreferences.Editor = sharedPref.edit()
                    editor.putInt("bottomNavigationView", 2)
                    editor.apply()
                    editor.commit()

                    searchBarEditText.text!!.clear()

                    toDeletedNotes()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_categories -> {
                    startActivity(Intent(this@NoteListActivity, CategoriesActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags =
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(dragFlags, 0)
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)

            Collections.swap(listOfAllNotes, viewHolder.adapterPosition, target.adapterPosition)

            val sharedPreferences = getSharedPreferences("Sort_by", Context.MODE_PRIVATE)
            val edit: SharedPreferences.Editor = sharedPreferences.edit()
            edit.putString("Sort_by", "none")
            edit.apply()
            sortByNone.isChecked = true

            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            noteDao.updateNotes(listOfAllNotes)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
//            refreshActivity()
        }
    }
    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        //region ======================================== getNoteDAO ========================================

        noteDao = App.database.noteDao()
        listOfAllNotes = noteDao.getAllNotes()
        listOfFavoritesNotes = noteDao.getFavoritesNotes()
        listOfDeletedNotes = noteDao.getDeletedNotes()

        //endregion

        //region ========================================== Toolbar =========================================

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

        //region ===================================== SharedPreferences ====================================

        sharedPref = getSharedPreferences("bottomNavigationView", Context.MODE_PRIVATE)
        val bottomNavPosition = sharedPref.getInt("bottomNavigationView", 0)

        val sharedPreferences = getSharedPreferences("Sort_by", Context.MODE_PRIVATE)
        when (sharedPreferences.getString("Sort_by", "date")) {
            "title" -> {
                if (fromFavorites) {
                    adapter =
                        RecyclerViewNoteAdapter(
                            noteDao.getAllFavoriteNotesOrderByTitleAZ(),
                            this
                        )
                    listOfFavoritesNotes = noteDao.getAllFavoriteNotesOrderByTitleAZ()
                } else {
                    adapter = RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByTitleAZ(), this)
                    listOfAllNotes = noteDao.getAllNotesOrderByTitleAZ()
                }
            }
            "favorite" -> {
                adapter = RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByFavoriteAZ(), this)
                listOfAllNotes = noteDao.getAllNotesOrderByFavoriteAZ()
            }
            "date" -> {
                if (fromFavorites) {
                    adapter = RecyclerViewNoteAdapter(
                        noteDao.getAllFavoriteNotesOrderByDateAZ(),
                        this
                    )
                    listOfFavoritesNotes = noteDao.getAllFavoriteNotesOrderByDateAZ()
                } else {
                    adapter = RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByDateAZ(), this)
                    listOfAllNotes = noteDao.getAllNotesOrderByDateAZ()
                }
            }
            "none" -> {
                if (fromFavorites) {
                    adapter = RecyclerViewNoteAdapter(
                        noteDao.getFavoritesNotes(),
                        this
                    )
                    listOfFavoritesNotes = noteDao.getFavoritesNotes()
                } else {
                    adapter = RecyclerViewNoteAdapter(noteDao.getAllNotes(), this)
                    listOfAllNotes = noteDao.getAllNotes()
                }
            }
            else -> adapter = RecyclerViewNoteAdapter(noteDao.getAllNotes(), this)
        }

        recyclerViewInit(adapter)

        //endregion

        //region =================================== BottomNavigationView ===================================

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

        //endregion

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
                getNoteByFilterSearchBar(charSequence.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }


        })

        //endregion

//        itemTouchHelper.attachToRecyclerView(recyclerView)

        //region =========================================== Fill Category Default ===========================================

        val sharedPrefFillCategory = getSharedPreferences("FillCategory", Context.MODE_PRIVATE)

        if (sharedPrefFillCategory.getBoolean("FillCategory", true)) {
            categoryDAO = App.database.categoryDao()
            listOfAllCategories = categoryDAO.getAllCategories()

            val arrayOfCategory = resources.getStringArray(R.array.categories_array)

            for (i in arrayOfCategory.indices) {
                val category = Category(arrayOfCategory[i], defaultColorCategory(i).toString(), 0)
                categoryDAO.insertCategory(category)
            }

            val editFillCategory: SharedPreferences.Editor = sharedPrefFillCategory.edit()
            editFillCategory.putBoolean("FillCategory", false)
            editFillCategory.apply()
        }

        //endregion
    }

    //region =========================================== Override ===========================================

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_list_toolbar_menu, menu)
        sortByTitle = menu!!.findItem(R.id.sort_by_title)
        sortByFavorite = menu.findItem(R.id.sort_by_favorite)
        sortByDate = menu.findItem(R.id.sort_by_date)
        sortByNone = menu.findItem(R.id.sort_by_none)

        val sharedPreferences = getSharedPreferences("Sort_by", Context.MODE_PRIVATE)
        when (sharedPreferences.getString("Sort_by", "date")) {
            "title" -> sortByTitle.isChecked = true
            "favorite" -> sortByFavorite.isChecked = true
            "date" -> sortByDate.isChecked = true
            "none" -> sortByNone.isChecked = true
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
                    when {
                        fromFavorites -> {
                            adapter = RecyclerViewNoteAdapter(
                                noteDao.getAllFavoriteNotesOrderByTitleAZ(),
                                this
                            )
                            listOfFavoritesNotes = noteDao.getAllFavoriteNotesOrderByTitleAZ()
                        }
                        fromDeletedNotes -> {
                            adapter = RecyclerViewNoteAdapter(
                                noteDao.getDeletedNotesOrderByTitleAZ(),
                                this
                            )
                            listOfDeletedNotes = noteDao.getDeletedNotesOrderByTitleAZ()
                        }
                        else -> {
                            adapter =
                                RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByTitleAZ(), this)
                            listOfAllNotes = noteDao.getAllNotesOrderByTitleAZ()
                        }
                    }
                    recyclerViewInit(adapter)
                    edit.putString("Sort_by", "title")
                    edit.apply()
                    item.isChecked = true
                }
            }
            R.id.sort_by_favorite -> {
                when {
                    fromFavorites -> {
                        adapter = RecyclerViewNoteAdapter(
                            noteDao.getAllFavoriteNotesOrderByTitleAZ(),
                            this
                        )
                        listOfFavoritesNotes = noteDao.getAllFavoriteNotesOrderByTitleAZ()
                    }
                    fromDeletedNotes -> {
                        adapter = RecyclerViewNoteAdapter(
                            noteDao.getDeletedNotesOrderByFavoriteAZ(),
                            this
                        )
                        listOfDeletedNotes = noteDao.getDeletedNotesOrderByFavoriteAZ()
                    }
                    else -> {
                        adapter =
                            RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByFavoriteAZ(), this)
                        listOfAllNotes = noteDao.getAllNotesOrderByFavoriteAZ()
                    }
                }
                recyclerViewInit(adapter)
                edit.putString("Sort_by", "favorite")
                edit.apply()
                item.isChecked = true
            }
            R.id.sort_by_date -> {
                when {
                    fromFavorites -> {
                        adapter = RecyclerViewNoteAdapter(
                            noteDao.getAllFavoriteNotesOrderByDateAZ(),
                            this
                        )
                        listOfFavoritesNotes = noteDao.getAllFavoriteNotesOrderByDateAZ()
                    }
                    fromDeletedNotes -> {
                        adapter = RecyclerViewNoteAdapter(
                            noteDao.getDeletedNotesOrderByDateAZ(),
                            this
                        )
                        listOfDeletedNotes = noteDao.getDeletedNotesOrderByDateAZ()
                    }
                    else -> {
                        adapter = RecyclerViewNoteAdapter(noteDao.getAllNotesOrderByDateAZ(), this)
                        listOfAllNotes = noteDao.getAllNotesOrderByDateAZ()
                    }
                }
                recyclerViewInit(adapter)
                edit.putString("Sort_by", "date")
                edit.apply()
                item.isChecked = true
            }
            R.id.sort_by_none -> {
                when {
                    fromFavorites -> {
                        adapter = RecyclerViewNoteAdapter(
                            noteDao.getFavoritesNotes(),
                            this
                        )
                        listOfFavoritesNotes = noteDao.getFavoritesNotes()
                    }
                    fromDeletedNotes -> {
                        adapter = RecyclerViewNoteAdapter(
                            noteDao.getDeletedNotes(),
                            this
                        )
                        listOfDeletedNotes = noteDao.getDeletedNotes()
                    }
                    else -> {
                        adapter = RecyclerViewNoteAdapter(noteDao.getAllNotes(), this)
                        listOfAllNotes = noteDao.getAllNotes()
                    }
                }
                recyclerViewInit(adapter)
                edit.putString("Sort_by", "none")
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
                    if (listOfDeletedNotes.isEmpty()) {
                        Toast.makeText(
                            this,
                            getString(R.string.note_list_activity_list_empty),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        confirmDeleteAllNotesDialog()
                    }
                }
            }
        }
    }

    //endregion

    //region =========================================== Functions ==========================================

    private fun defaultColorCategory(i: Int): Int {
        return when (i) {
            0 -> {
                R.drawable.ic_bookmark_dark_blue
            }
            1 -> {
                R.drawable.ic_bookmark_dark_green
            }
            2 -> {
                R.drawable.ic_bookmark_orange
            }
            3 -> {
                R.drawable.ic_bookmark_purple
            }
            4 -> {
                R.drawable.ic_bookmark
            }
            5 -> {
                R.drawable.ic_bookmark_red
            }
            6 -> {
                R.drawable.ic_bookmark_pink
            }
            7 -> {
                R.drawable.ic_bookmark_green
            }
            8 -> {
                R.drawable.ic_bookmark_blue
            }
            else -> {
                R.drawable.ic_bookmark_red
            }
        }
    }

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
        intent.putExtra("fromFavorites", fromFavorites)
        startActivity(intent)
    }

    private fun recyclerViewInit(recyclerViewNoteAdapter: RecyclerViewNoteAdapter) {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = recyclerViewNoteAdapter
        recyclerViewNoteAdapter.notifyDataSetChanged()
    }

    private fun toFavorites() {
        if (sortByFavorite.isChecked) {
            val sharedPreferences = getSharedPreferences("Sort_by", Context.MODE_PRIVATE)
            val edit: SharedPreferences.Editor = sharedPreferences.edit()
            edit.putString("Sort_by", "title")
            edit.apply()
            sortByTitle.isChecked = true
        }

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
        sortByFavorite.isVisible = false
        floatingButtonAddNewNote!!.visibility = View.GONE
        floatingButtonDeleteTrash!!.visibility = View.VISIBLE
    }

    private fun getNoteByFilterSearchBar(searchBarText: String) {
        when {
            fromFavorites -> {
                adapter =
                    RecyclerViewNoteAdapter(
                        checkToolbarText(
                            noteDao.getAllFavoriteNotesOrderByTitleAZ(),
                            searchBarText
                        ), this
                    )
                recyclerViewInit(adapter)
            }
            fromDeletedNotes -> {
                adapter =
                    RecyclerViewNoteAdapter(
                        checkToolbarText(
                            noteDao.getDeletedNotesOrderByFavoriteAZ(),
                            searchBarText
                        ), this
                    )
            }
            else -> {
                adapter =
                    RecyclerViewNoteAdapter(
                        checkToolbarText(
                            noteDao.getAllNotesOrderByTitleAZ(),
                            searchBarText
                        ), this
                    )
                recyclerViewInit(adapter)
            }
        }

//        val contactFilterList: ArrayList<Note>? = getAllContactFilter(filterList)
//        val contactList = getContactByName(name)
//        if (contactFilterList != null) {
//            return intersectContactWithAllInformation(contactList, contactFilterList)
//        }
    }

    private fun checkToolbarText(
        listOfNotes: MutableList<Note>,
        searchBarText: String
    ): ArrayList<Note> {
        val noteListFilter = arrayListOf<Note>()

        val lowerSearchBarText = searchBarText.toLowerCase(Locale.ROOT)
        val upperSearchBarText = searchBarText.toUpperCase(Locale.ROOT)

        for (note in listOfNotes) {
            if (note.title.contains(searchBarText) || note.content.contains(searchBarText) ||
                note.title.contains(lowerSearchBarText) ||
                note.content.contains(lowerSearchBarText) ||
                note.title.contains(upperSearchBarText) ||
                note.content.contains(upperSearchBarText)
            ) {
                noteListFilter.add(note)
            }
        }

        return noteListFilter
    }

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