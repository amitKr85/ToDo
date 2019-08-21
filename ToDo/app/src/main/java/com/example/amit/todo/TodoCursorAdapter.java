package com.example.amit.todo;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amit.todo.data.TodoContract.TodoEntry;

public class TodoCursorAdapter extends CursorAdapter {

    public TodoCursorAdapter(Context context,Cursor cursor){
        super(context,cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final String task=cursor.getString(cursor.getColumnIndexOrThrow(TodoEntry.COLUMN_TASK));
        final int priority=cursor.getInt(cursor.getColumnIndexOrThrow(TodoEntry.COLUMN_PRIORITY));
        final int checked=cursor.getInt(cursor.getColumnIndexOrThrow(TodoEntry.COLUMN_DONE));
        final long id=cursor.getLong(cursor.getColumnIndexOrThrow(TodoEntry._ID));
        final Context fContext=context;

        TextView textView=view.findViewById(R.id.task_text_view);
        textView.setText(task);

        CheckBox checkBox=view.findViewById(R.id.completed_checkbox);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked( TodoEntry.getEquivalentBoolean(checked) );
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Uri uri= ContentUris.withAppendedId(TodoEntry.CONTENT_URI,id);
                    ContentValues values=new ContentValues();
                    values.put(TodoEntry.COLUMN_DONE,TodoEntry.TRUE);
                    fContext.getContentResolver().update(uri,values,null,null);
                }
                else{
                    Uri uri= ContentUris.withAppendedId(TodoEntry.CONTENT_URI,id);
                    ContentValues values=new ContentValues();
                    values.put(TodoEntry.COLUMN_DONE,TodoEntry.FALSE);
                    fContext.getContentResolver().update(uri,values,null,null);
                }

            }
        });

        switch (priority){
            case TodoEntry.VERY_IMPORTANT:
                view.setBackgroundColor(context.getResources().getColor(R.color.very_important));
                break;
            case TodoEntry.IMPORTANT:
                view.setBackgroundColor(context.getResources().getColor(R.color.important));
                break;
            case TodoEntry.NORMAL:
                view.setBackgroundColor(context.getResources().getColor(R.color.normal));
                break;
            case TodoEntry.SHOULD_DO:
                view.setBackgroundColor(context.getResources().getColor(R.color.should_do));
                break;
            case TodoEntry.DO_IF_POSSIBLE:
                view.setBackgroundColor(context.getResources().getColor(R.color.do_if_possible));
        }

        ImageButton deleteButton=view.findViewById(R.id.delete_image_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(fContext);
                builder.setMessage("Are you sure to delete this task ?");
                builder.setNegativeButton("cancel",null);
                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri= ContentUris.withAppendedId(TodoEntry.CONTENT_URI,id);
                        fContext.getContentResolver().delete(uri,null,null);
                    }
                });
                builder.create().show();
            }
        });
    }
}
