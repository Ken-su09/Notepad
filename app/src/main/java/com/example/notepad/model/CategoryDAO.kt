package com.example.notepad.model

import androidx.room.*

@Dao
interface CategoryDAO {

    /**
     * Usage:
     * dao.getAllCategories()
     */
    @Query("SELECT * FROM category")
    fun getAllCategories(): MutableList<Category>

    /**
     * Usage:
     * dao.getCategoryById(1)
     */
    @Query("SELECT * FROM category WHERE id = category.id")
    fun getCategoryById(): Category

    /**
     * Usage:
     * dao.getCategoryById(1)
     */
    @Query("SELECT * FROM category WHERE id = category.id_note")
    fun getCategoryByNote(): Category

    /**
     * Usage:
     * dao.insertCategory(category)
     */
    @Insert
    fun insertCategory(category: Category)
}