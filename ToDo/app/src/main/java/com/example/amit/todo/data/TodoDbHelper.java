package com.example.amit.todo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.amit.todo.data.TodoContract.TodoEntry;

public class TodoDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="tododatabase.db";
    public static final int DATABASE_VERSION=1;

    public TodoDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TODO_TABLE="CREATE TABLE "+ TodoEntry.TABLE_NAME+"("
                + TodoEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TodoEntry.COLUMN_TASK+" TEXT,"
                + TodoEntry.COLUMN_PRIORITY+" INTEGER,"
                + TodoEntry.COLUMN_DONE+" INTEGER);";
        db.execSQL(SQL_CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
