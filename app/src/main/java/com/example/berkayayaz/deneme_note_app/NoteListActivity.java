package com.example.berkayayaz.deneme_note_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity {
    FloatingActionButton fab;
    ListView listView;
    SearchView searchBar;
    String selectedCategory;
    String userSortChoosenTask;


    CustomListCursorAdapter dataCursorAdapter;
    DBHelperNote db;
    boolean registeredNote = false;
    Toolbar toolbar;
    boolean searchActive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.listView);
        searchBar = (SearchView) findViewById(R.id.searchView);
        Intent intent = getIntent();
        selectedCategory = intent.getStringExtra("selectedCategory");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProduct = new Intent(view.getContext(), AddNoteActivity.class);
                addProduct.putExtra("selectedCategory", selectedCategory);
                startActivity(addProduct);
            }
        });
        db = new DBHelperNote(getApplicationContext());



        //TEST
//        Note testInsert = new Note("AAAA","",45,43,"19/12/12","konya");
//        Note testUpdate = new Note(3,"CCCC","",45,43,"19/12/12","konya");
//        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
//                R.drawable.albania);
//
//      int noteID =  db.insertNote(testInsert);
//        //db.updateNoteByID(testUpdate);
//        db.insertPhoto(photoTest);
//       // db.deleteAllPhotos(1);
//        //db.deleteNote(2);
//       // db.updateNoteByID(testUpdate);
//        photoArrayList=db.getAllPhotos();
//
//        //noteArrayList = db.getAllData();

        Cursor dataCursor = db.getNoteCursor(selectedCategory);
        String from[] = {db.TITLE};
        int to[] = {R.id.cell_titleTextView};

        if (dataCursor != null) {
            //ARRAYADAPTER
            //dataArrayAdapter = new CustomListSimpleAdapter(getApplicationContext(),R.layout.note_cell, noteArrayList);
            //CURSOR ADAPTER
            dataCursorAdapter = new CustomListCursorAdapter(db, this, R.layout.note_cell, dataCursor, from, to, 0);
            listView.setAdapter(dataCursorAdapter);

        }

        dataCursorAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                final int columnIndex = cursor.getColumnIndexOrThrow(db._id);
                final String str = cursor.getString(columnIndex);
                return str;
            }
        });

        dataCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                Cursor cursor = db.getNoteCursorFiltered(
                        (constraint != null ? constraint.toString() : null));
                return cursor;
            }
        });





    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("CELL CLICKED!!!!");
                Log.e("cell", "cell clicked");

                //long rowid =  dataCursorAdapter.getItemId(position);
                //FIXME: INTENT DOESN'T WORK
                Intent goToNote = new Intent(view.getContext(), AddNoteActivity.class);
                goToNote.putExtra("id",id);
                goToNote.putExtra("selectedCategory", selectedCategory);

                goToNote.putExtra("registeredNote",true);
                startActivity(goToNote);

            }
        });

        //FIXME: SEARCHBAR DOESN'T WORK
        searchBar.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
              String  searchText = newText.isEmpty() ? "" :
                        "LOWER(" + DBHelperNote.TITLE + ") LIKE '%" + newText.toLowerCase() + "%'";
                CustomListCursorAdapter adapter = (CustomListCursorAdapter) listView.getAdapter();
                adapter.swapCursor(db.getNoteCursorFiltered(searchText));
                adapter.notifyDataSetChanged();

//                if (!newText.isEmpty()) {
//
//
//                    searchActive = true;
//                    Cursor dataCursor = db.getNoteCursorFiltered(newText);
//                    String from[] = {db.TITLE};
//                    int to[] = {R.id.cell_titleTextView};
//                    dataCursorAdapter = new CustomListCursorAdapter(db, getApplicationContext(), R.layout.note_cell, dataCursor, from, to, 0);
//                    listView.setAdapter(dataCursorAdapter);
//                    dataCursorAdapter.swapCursor(dataCursor);
//                    dataCursorAdapter.notifyDataSetChanged();
//
//
//                } else {
//                    searchActive = false;
//                    Cursor dataCursor = db.getNoteCursorFiltered("");
//                    String from[] = {db.TITLE};
//                    int to[] = {R.id.cell_titleTextView};
//                    dataCursorAdapter = new CustomListCursorAdapter(db, getApplicationContext(), R.layout.note_cell, dataCursor, from, to, 0);
//                    listView.setAdapter(dataCursorAdapter);
//                    dataCursorAdapter.swapCursor(dataCursor);
//                    dataCursorAdapter.notifyDataSetChanged();
//                }
                return false;
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_sort){

            selectSort();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //sort

    private void selectSort() {
        final CharSequence[] items = { "A-Z", "Z-A","DATE DSC",
                "DATE ASC","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(NoteListActivity.this);
        builder.setTitle("Select Sort Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(NoteListActivity.this);

                if (items[item].equals("A-Z")) {
                    userSortChoosenTask ="A-Z";
                    if(result) {
                        dataCursorAdapter.swapCursor(db.getNoteCursor(selectedCategory));
                        dataCursorAdapter.notifyDataSetChanged();
                    }
                } else if (items[item].equals("Z-A")) {
                    userSortChoosenTask ="Z-A";
                    if(result) {
                        dataCursorAdapter.swapCursor(db.getNoteCursorDESC(selectedCategory));
                        dataCursorAdapter.notifyDataSetChanged();
                    }
                } else if (items[item].equals("DATE ASC")) {
                    userSortChoosenTask ="DATE ASC";
                    if(result) {
                        dataCursorAdapter.swapCursor(db.getNoteCursorDateASC(selectedCategory));
                        dataCursorAdapter.notifyDataSetChanged();
                    }

                } else if (items[item].equals("DATE DSC")) {
                    userSortChoosenTask ="DATE DSC";
                    if(result) {
                        dataCursorAdapter.swapCursor(db.getNoteCursorDateDESC(selectedCategory));
                        dataCursorAdapter.notifyDataSetChanged();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public Cursor selectedCursor(){

        if(userSortChoosenTask =="A-Z") {
            return db.getNoteCursor(selectedCategory);

        }else


        if(userSortChoosenTask =="Z-A") {
            return db.getNoteCursorDESC(selectedCategory);
        }


        else if(userSortChoosenTask == "DATE ASC") {
            return db.getNoteCursorDateASC(selectedCategory);
        }



        else if(userSortChoosenTask == "DATE DSC") {
            return db.getNoteCursorDateDESC(selectedCategory);
        }
        else {
            return  db.getNoteCursor(selectedCategory);
        }
    }
}
