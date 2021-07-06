package com.example.notepad

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


class CategoriesActivity : AppCompatActivity() {

    //region ========================================== Val or Var ==========================================

    private lateinit var categoryArrowUp: AppCompatImageView
    private lateinit var categoryArrowDown: AppCompatImageView
    private lateinit var categoryLayout: RelativeLayout

    private var bottomNavigationView: BottomNavigationView? = null

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.activity_note_list_bottom_nav_view_notes -> {
                    startActivity(Intent(this@CategoriesActivity, NoteListActivity::class.java))

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_favorites -> {
                    startActivity(Intent(this@CategoriesActivity, NoteListActivity::class.java))

                    return@OnNavigationItemSelectedListener true
                }
                R.id.activity_note_list_bottom_nav_view_trash -> {
                    startActivity(Intent(this@CategoriesActivity, NoteListActivity::class.java))

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
        setContentView(R.layout.activity_categories)

        //region ========================================== Toolbar =========================================

        val toolbar = findViewById<Toolbar>(R.id.activity_note_categories_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //endregion

        //region ======================================= FindViewById =======================================

        categoryLayout = findViewById(R.id.activity_note_categories_category)
        categoryArrowUp = findViewById(R.id.activity_note_categories_category_arrow_up)
        categoryArrowDown = findViewById(R.id.activity_note_categories_category_arrow_down)
        bottomNavigationView = findViewById(R.id.activity_note_categories_bottom_nav_view)

        //endregion

        //region =================================== BottomNavigationView ===================================

        val menu = bottomNavigationView!!.menu
        val navItem = menu.findItem(R.id.activity_note_list_bottom_nav_view_categories)
        navItem.isChecked = true

        //endregion

        //region ========================================= Animations =======================================

        val rotate = RotateAnimation(
            0F,
            180F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 500
        rotate.interpolator = LinearInterpolator()

        //endregion

        //region ========================================= Listeners ========================================

        bottomNavigationView!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        categoryLayout.setOnClickListener {
            if (categoryArrowUp.visibility == View.VISIBLE) {
//            categoryArrowUp.animate().rotation(180F).start()
                categoryArrowUp.startAnimation(rotate)
                Handler().postDelayed({
                    run {
                        categoryArrowUp.visibility = View.GONE
                        categoryArrowDown.visibility = View.VISIBLE
                    }
                }, 500)
            } else {
                categoryArrowDown.startAnimation(rotate)
                Handler().postDelayed({
                    run {
                        categoryArrowUp.visibility = View.VISIBLE
                        categoryArrowDown.visibility = View.GONE
                    }
                }, 500)
            }
        }

        //endregion
    }
}