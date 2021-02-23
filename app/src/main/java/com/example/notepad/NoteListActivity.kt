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

    private lateinit var listOfNotes: MutableList<Note>
    private lateinit var adapter: RecyclerViewNoteAdapter

    private var noteDao = App.database.noteDao()

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.activity_note_list_bottom_nav_view_notes -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_favorites -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_trash -> {
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

        listOfNotes = noteDao.getAllNotes()
        adapter = RecyclerViewNoteAdapter(listOfNotes, this)

        val toolbar = findViewById<Toolbar>(R.id.activity_note_list_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val floatingButton =
            findViewById<FloatingActionButton>(R.id.activity_note_list_floating_button)
        floatingButton.setOnClickListener(this)

        val recyclerView = findViewById<RecyclerView>(R.id.activity_note_list_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.activity_note_list_bottom_nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val menu = bottomNavigationView.menu
        val navItem = menu.findItem(R.id.activity_note_list_bottom_nav_view_notes)
        navItem.isChecked = true
        bottomNavigationView!!.menu.getItem(0).isChecked = true
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
            showNoteDetail(v.tag as Int)
        } else {
            when (v?.id) {
                R.id.activity_note_list_floating_button -> createNewNote()
            }
        }
    }

    private fun showNoteDetail(noteIndex: Int) {
        val note = listOfNotes[noteIndex]

        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
        startActivity(intent)
    }

    private fun createNewNote() {
        val intent = Intent(this, NoteDetailActivity::class.java)
        startActivity(intent)
    }
}