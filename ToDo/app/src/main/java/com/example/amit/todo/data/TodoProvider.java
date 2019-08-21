package com.example.amit.todo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.amit.todo.data.TodoContract.TodoEntry;

public class TodoProvider extends ContentProvider {

    public static final int TODO=100;
    public static final int TODO_ID=101;

    public static final UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(TodoContract.CONTENT_AUTHORITY,TodoContract.PATH_TODO,TODO);
        uriMatcher.addURI(TodoContract.CONTENT_AUTHORITY,TodoContract.PATH_TODO+"/#",TODO_ID);
    }

    TodoDbHelper mDbHelper;

    /** Tag for the log messages */
    public static final String LOG_TAG = TodoProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mDbHelper=new TodoDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db=mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match=uriMatcher.match(uri);
        switch (match){
            case TODO:
                cursor=db.query(TodoEntry.TABLE_NAME,projection,selection,selectionArgs,null,
                        null,sortOrder);
                break;
            case TODO_ID:
                selection=TodoEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=db.query(TodoEntry.TABLE_NAME,projection,selection,selectionArgs,null,
                        null,sortOrder);
            default:
                throw new IllegalArgumentException("no match in query for given uri");
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TODO:
                return insertTask(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTask(Uri uri, ContentValues values) {
        String task=values.getAsString(TodoEntry.COLUMN_TASK);
        int priority=values.getAsInteger(TodoEntry.COLUMN_PRIORITY);
        int done=values.getAsInteger(TodoEntry.COLUMN_DONE);
        if(task==null)
            throw new IllegalArgumentException("inserting null task");
        if(!TodoEntry.isValidPriority(priority))
            throw new IllegalArgumentException("inserting invalid priority");
        if(!TodoEntry.isValidDone(done))
            throw new IllegalArgumentException("inserting invalid done(checked) value");

        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        long id=db.insert(TodoEntry.TABLE_NAME,null,values);

        if(id==-1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        ///notifying all listeners that the content has changed at this uri
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TODO:
                // Delete all rows that match the selection and selection args
                rowsUpdated=database.delete(TodoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:
                // Delete a single row given by the ID in the URI
                selection = TodoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsUpdated=database.delete(TodoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsUpdated!=0){
            ///notifying all listeners if affected rows >0 that the content has changed at this uri
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsUpdated;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match=uriMatcher.match(uri);
        switch(match){
            case TODO:
                return updatePet(uri,values,selection,selectionArgs);
            case TODO_ID:
                selection=TodoEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("no update operation associated with the uri");
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if(values.containsKey(TodoEntry.COLUMN_TASK)){
            String task=values.getAsString(TodoEntry.COLUMN_TASK);
            if(task==null)
                throw new IllegalArgumentException("null task in update");
        }
        if(values.containsKey(TodoEntry.COLUMN_PRIORITY)){
            int priority=values.getAsInteger(TodoEntry.COLUMN_PRIORITY);
            if(!TodoEntry.isValidPriority(priority))
                throw new IllegalArgumentException("invalid priority in update");
        }
        if(values.containsKey(TodoEntry.COLUMN_DONE)){
            int done=values.getAsInteger(TodoEntry.COLUMN_DONE);
            if(!TodoEntry.isValidDone(done))
                throw new IllegalArgumentException("invalid done value in update");
        }
        if(values.size()==0)
            return 0;

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated= database.update(TodoEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated!=0){
            ///notifying all listeners if affected rows >0 that the content has changed at this uri
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
