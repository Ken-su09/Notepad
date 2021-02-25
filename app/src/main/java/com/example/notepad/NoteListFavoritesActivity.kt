package com.example.notepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteListFavoritesActivity : AppCompatActivity(), View.OnClickListener {

    //region ========================================== Val or Var ==========================================

    private lateinit var listOfFavoritesNotes: MutableList<Note>
    private lateinit var adapter: RecyclerViewNoteAdapter

    private var noteDao = App.database.noteDao()

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.activity_note_list_bottom_nav_view_notes -> {
                    startActivity(Intent(this, NoteListActivity::class.java))
                    overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
                    menuItem.setIcon(R.drawable.ic_star)
                    finish()
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
        setContentView(R.layout.activity_note_list_favorites)

        listOfFavoritesNotes = noteDao.getFavoritesNotes()
        adapter = RecyclerViewNoteAdapter(listOfFavoritesNotes, this)

        val toolbar = findViewById<Toolbar>(R.id.activity_note_list_favorites_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val floatingButton =
            findViewById<FloatingActionButton>(R.id.activity_note_list_favorites_floating_button)
        floatingButton.setOnClickListener(this)

        val recyclerView =
            findViewById<RecyclerView>(R.id.activity_note_list_favorites_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.activity_note_list_favorites_bottom_nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val menu = bottomNavigationView.menu
        val navItem = menu.findItem(R.id.activity_note_list_bottom_nav_view_notes)
        navItem.isChecked = true
        bottomNavigationView!!.menu.getItem(1).isChecked = true
        bottomNavigationView.menu.getItem(1).setIcon(R.drawable.ic_full_star)
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
        val note = listOfFavoritesNotes[noteIndex]

        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
        startActivity(intent)
    }

    private fun createNewNote() {
        val intent =
            Intent(this, NoteDetailActivity::class.java).putExtra("fromNoteListFavorite", true)
        startActivity(intent)
    }
}