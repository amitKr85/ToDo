package com.example.amit.todo;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amit.todo.data.TodoContract.TodoEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int TODO_LOADER=0;

    TodoCursorAdapter mCursorAdapter;

    private int mPriority=TodoEntry.VERY_IMPORTANT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * setting up floating action button
         * it wil open a alert dialog to take input
         */
        setupFloatingActionButton();

        /**
         * setting adapter to list view
         */
        ListView listView=findViewById(R.id.list_view);
        mCursorAdapter=new TodoCursorAdapter(this,null);
        listView.setAdapter(mCursorAdapter);
        listView.setEmptyView(findViewById(R.id.empty_view));

        /**
         * initialising cursor loader
         */
        getLoaderManager().initLoader(TODO_LOADER,null,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_dummy_task:
                ContentValues values=new ContentValues();
                values.put(TodoEntry.COLUMN_TASK,"dummy task");
                values.put(TodoEntry.COLUMN_PRIORITY,TodoEntry.VERY_IMPORTANT);
                values.put(TodoEntry.COLUMN_DONE,0);

                Uri uri=getContentResolver().insert(TodoEntry.CONTENT_URI,values);
                Log.i("main activity","after insert uri="+uri);
                break;
            case R.id.delete_all_task:
                showConfirmationAndDelete();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationAndDelete() {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setMessage("Delete All Task?");
        builder.setPositiveButton("delete all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int rows=getContentResolver().delete(TodoEntry.CONTENT_URI,null,null);
                if(rows==0){
                    Toast.makeText(MainActivity.this,"failed to delete",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"delete successful",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel,null);
        android.app.AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * building alert dialog an showing it
                 */
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.add_a_task);
                final View dialogView=getLayoutInflater().inflate(R.layout.add_task,null);
                Spinner spinner=dialogView.findViewById(R.id.priority_spinner);
                //ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(MainActivity.this,
                //      R.array.priority_array,android.R.layout.simple_spinner_item);
                /**
                 * setting up adapter for priority spinner
                 * setting up color for each priority in the drop down menu to its corresponding color
                 * and set selected item background to its corresponding color
                 */
                CharSequence[] priorityArray=getResources().getTextArray(R.array.priority_array);
                CustomSpinnerArrayAdapter adapter=new CustomSpinnerArrayAdapter(MainActivity.this,
                        R.layout.custom_spinner_item,priorityArray);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String priority= (String) parent.getItemAtPosition(position);
                        if(priority.equals(getString(R.string.very_important)))
                            mPriority=TodoEntry.VERY_IMPORTANT;
                        else if(priority.equals(getString(R.string.important)))
                            mPriority=TodoEntry.IMPORTANT;
                        else if(priority.equals(getString(R.string.normal)))
                            mPriority=TodoEntry.NORMAL;
                        else if(priority.equals(getString(R.string.should_do)))
                            mPriority=TodoEntry.SHOULD_DO;
                        else if(priority.equals(getString(R.string.do_if_possible)))
                            mPriority=TodoEntry.DO_IF_POSSIBLE;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        mPriority=TodoEntry.VERY_IMPORTANT;
                    }
                });

                builder.setView(dialogView);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.add_task, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //checking for null task
                        String task=((EditText)dialogView.findViewById(R.id.task_edit_text)).getText().toString();
                        if(TextUtils.isEmpty(task)){
                            Toast.makeText(MainActivity.this,"task can't be empty",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //setting up content values//
                        ContentValues values=new ContentValues();
                        values.put(TodoEntry.COLUMN_TASK,task);
                        values.put(TodoEntry.COLUMN_PRIORITY,mPriority);
                        values.put(TodoEntry.COLUMN_DONE,TodoEntry.FALSE);

                        //inserting data
                        Uri newUri=getContentResolver().insert(TodoEntry.CONTENT_URI,values);
                        // Show a toast message depending on whether or not the insertion was successful
                        if (newUri == null) {
                            // If the new content URI is null, then there was an error with insertion.
                            Toast.makeText(MainActivity.this,"Failed to add task",Toast.LENGTH_SHORT).show();
                        } else {
                            // Otherwise, the insertion was successful and we can display a toast.
                            Toast.makeText(MainActivity.this,"Task added",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                builder.create().show();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                TodoEntry._ID,
                TodoEntry.COLUMN_TASK,
                TodoEntry.COLUMN_PRIORITY,
                TodoEntry.COLUMN_DONE
        };

        String sortOrder=TodoEntry.COLUMN_DONE+" ASC,"+TodoEntry.COLUMN_PRIORITY+" DESC";
        return new CursorLoader(this,TodoEntry.CONTENT_URI,projection,
                null,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
