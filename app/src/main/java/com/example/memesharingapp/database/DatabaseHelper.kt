package com.example.memesharingapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.memesharingapp.models.PostData

class DatabaseHelper (context:Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        const val DATABASE_NAME = "posts.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "posts"
        const val COLUMN_POST_ID = "post_id"
        const val COLUMN_SUBREDDIT = "subreddit"
        const val COLUMN_TITLE = "title"
        const val COLUMN_URL = "url"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_UPS = "ups"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSQL = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_POST_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_SUBREDDIT TEXT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_URL TEXT," +
                "$COLUMN_AUTHOR TEXT," +
                "$COLUMN_UPS INTEGER)"

        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertPost(post: PostData) {
        val db = writableDatabase
        val values = ContentValues()

        values.put(COLUMN_SUBREDDIT, post.subreddit)
        values.put(COLUMN_TITLE, post.title)
        values.put(COLUMN_URL, post.url)
        values.put(COLUMN_AUTHOR, post.author)
        values.put(COLUMN_UPS, post.ups)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getAllPosts(): Cursor? {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it

        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
//        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)

        return db.rawQuery("SELECT * FROM " + TABLE_NAME+ " ORDER BY " + COLUMN_POST_ID + " DESC", null)
    }

    fun deleteAllPosts() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

}