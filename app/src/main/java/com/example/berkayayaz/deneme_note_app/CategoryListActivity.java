package com.example.berkayayaz.deneme_note_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class  CategoryListActivity extends AppCompatActivity {

    ListView listView;
    DBHelperNote db;
    CustomListSimpleAdapter arrayAdapter;
    ArrayList<String> allCategoryArray = new ArrayList<>();
    String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.categoryListView) ;
        db = new DBHelperNote(this);
        allCategoryArray = db.getAllCategoryArray();
        arrayAdapter = new CustomListSimpleAdapter(db,this,R.layout.cell_category,allCategoryArray);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gotoNoteList = new Intent(view.getContext(), NoteListActivity.class);
                gotoNoteList.putExtra("selectedCategory", parent.getItemAtPosition(position).toString());
                startActivity(gotoNoteList);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptBuilder();
            }
        });
    }

    public void promptBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Category");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategory = input.getText().toString();

                Intent goToAddNote = new Intent(getApplicationContext(),AddNoteActivity.class);
                goToAddNote.putExtra("selectedCategory",selectedCategory);
                startActivity(goToAddNote);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
