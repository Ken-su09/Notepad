package com.example.notepad

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notepad.model.Category
import com.example.notepad.model.CategoryDAO
import com.example.notepad.model.Note
import com.example.notepad.model.NoteDAO

@Database(entities = [Note::class, Category::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDAO
    abstract fun categoryDao(): CategoryDAO
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `note` ADD isFavorite INT NOT NULL DEFAULT''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `note` ADD isDeleted INT NOT NULL DEFAULT''")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `category` ( `id` INTEGER NOT NULL, `title` TEXT NOT NULL, `color` TEXT NOT NULL, `id_note` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`id_note`) REFERENCES `note`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
    }
}