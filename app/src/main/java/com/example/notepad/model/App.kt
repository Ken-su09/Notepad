package com.example.notepad.model

import android.app.Application
import androidx.room.Room
import com.example.notepad.AppDatabase
import com.example.notepad.MIGRATION_1_2
import com.example.notepad.MIGRATION_2_3
import com.example.notepad.MIGRATION_3_4

class App : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "note")
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_3_4)
            .build()
    }
}