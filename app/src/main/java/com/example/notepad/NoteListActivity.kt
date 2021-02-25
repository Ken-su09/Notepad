package com.example.notepad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var adapter: RecyclerViewNoteAdapter
    private lateinit var recyclerView: RecyclerView

    private var noteDao = App.database.noteDao()

    private lateinit var menuItemListFavorites: MenuItem

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.activity_note_list_bottom_nav_view_notes -> {
                    toAllNotes()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_favorites -> {
                    menuItem.setIcon(R.drawable.ic_full_star)
                    toFavorites()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_trash -> {
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

        listOfAllNotes = noteDao.getAllNotes()
        listOfFavoritesNotes = noteDao.getFavoritesNotes()
        listOfDeletedNotes = noteDao.getDeletedNotes()

        adapter = RecyclerViewNoteAdapter(listOfAllNotes, this)

        val toolbar = findViewById<Toolbar>(R.id.activity_note_list_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val floatingButton =
            findViewById<FloatingActionButton>(R.id.activity_note_list_floating_button)
        floatingButton.setOnClickListener(this)

        recyclerView = findViewById(R.id.activity_note_list_recyclerview)
        recyclerViewInit(adapter)

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.activity_note_list_bottom_nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val menu = bottomNavigationView.menu
        val navItem = menu.findItem(R.id.activity_note_list_bottom_nav_view_notes)
        navItem.isChecked = true
        bottomNavigationView!!.menu.getItem(0).isChecked = true
        menuItemListFavorites = menu.findItem(R.id.activity_note_list_bottom_nav_view_favorites)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_list_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.note_list_toolbar_menu_search -> {

            }
            R.id.note_list_toolbar_menu_sort_by -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        if (v?.tag != null) {
            if (fromAllNotes) {
                showNoteDetail(v.tag as Int, listOfAllNotes)
            } else if (fromFavorites) {
                showNoteDetail(v.tag as Int, listOfFavoritesNotes)
            } else if (fromDeletedNotes) {
                showNoteDetail(v.tag as Int, listOfDeletedNotes)
            }
        } else {
            when (v?.id) {
                R.id.activity_note_list_floating_button -> createNewNote()
            }
        }
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
    }

    private fun toAllNotes() {
        adapter = RecyclerViewNoteAdapter(listOfAllNotes, this)
        recyclerViewInit(adapter)
        menuItemListFavorites.setIcon(R.drawable.ic_star)

        fromAllNotes = true
        fromFavorites = false
        fromDeletedNotes = false
    }

    private fun toDeletedNotes() {
        adapter = RecyclerViewNoteAdapter(listOfDeletedNotes, this)
        recyclerViewInit(adapter)
        menuItemListFavorites.setIcon(R.drawable.ic_star)

        fromAllNotes = false
        fromFavorites = false
        fromDeletedNotes = true
    }
}