package com.example.berkayayaz.deneme_note_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by BerkayAyaz on 2016-04-22.
 */
public class DBHelperNote extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "myNotes";
    public static final String NOTE_TABLE_NAME = "NOTE";
    public static final String _id = "_id";
    public static final String TITLE = "title";
    private static final String CONTEXT = "context";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String DATE = "date";
    private static final String ADDRESS = "address";
    private static final String PHOTO = "photo";
    public static final String CATEGORY = "category";

    public DBHelperNote(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("create table " + NOTE_TABLE_NAME);
        String createNoteTableQuery = "create table " + NOTE_TABLE_NAME + "( "
                + _id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " VARCHAR(30), "
                + CONTEXT + " VARCHAR(30), "
                + LONGITUDE + " DOUBLE(30), "
                + LATITUDE + " DOUBLE(30), "
                + DATE + " DATE, "
                + CATEGORY + " VARCHAR(30), "
                + PHOTO + " BLOB NULL, "
                + ADDRESS + " VARCHAR(30))";

        db.execSQL(createNoteTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + NOTE_TABLE_NAME);
        onCreate(db);
    }



    public Note getNoteByID(long id) {
        System.out.println("getNoteByID");
        String selectQuery = "select * from " + NOTE_TABLE_NAME+" WHERE "+_id+"="+Long.toString(id);
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int noteID = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
        String context = cursor.getString(cursor.getColumnIndexOrThrow(CONTEXT));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE));
        long date = cursor.getLong(cursor.getColumnIndexOrThrow(DATE));
        String address = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY));
        byte[] imageByte = cursor.getBlob(cursor.getColumnIndexOrThrow(PHOTO));
        Bitmap photo;
        if (imageByte == null){
            photo = null;
        }
        else {
            photo = DbBitmapUtility.getImage(imageByte);
        }

        Note note = new Note(noteID,category, title, context, longitude, latitude, date, address,photo);
        return note;
    }
    public void deleteAllNotesByCategory(String categoryName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + NOTE_TABLE_NAME + " WHERE "+CATEGORY+" = " + categoryName);

        System.out.printf("Notes with id=%s category deleted\n", categoryName);
    }

    public void deletePhoto(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        byte[] bitmap = null;
        values.put(PHOTO,bitmap);
        String searchQuery = _id+ "="+id;
        db.update(NOTE_TABLE_NAME,values,searchQuery,null);


        System.out.printf("Photo with id=%d deleted\n", id);
    }

    public void updateNoteByID(Note note){
        System.out.println("Updating Data ON NOTE TABLE ID="+note.noteID);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, note.title);
        values.put(CONTEXT, note.context);
        values.put(LATITUDE, note.latitude);
        values.put(LONGITUDE, note.longitude);
        values.put(DATE, note.date);
        values.put(CATEGORY, note.category);
        values.put(ADDRESS, note.address);
        byte[] photoByte;
        if (note.photo == null){
            photoByte = null;
        }else {
            photoByte = DbBitmapUtility.getBytes(note.photo);
        }
        values.put(PHOTO,photoByte );
        String searchQuery = _id+ "="+note.noteID;
        db.update(NOTE_TABLE_NAME,values,searchQuery,null);

    }

    public void insertNote(Note note) {
        System.out.println("Inserting Data on NOTE Table");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(this.TITLE, note.title);
        values.put(this.LONGITUDE, note.longitude);
        values.put(this.CONTEXT, note.context);
        values.put(this.LATITUDE, note.latitude);
        values.put(this.ADDRESS, note.address);
        values.put(this.DATE, note.date);
        values.put(this.CATEGORY, note.category);
        byte[] photoByte;
        if(note.photo == null){
            photoByte = null;
            values.put(this.PHOTO,photoByte);
        }else{

        photoByte =  DbBitmapUtility.getBytes(note.photo);
        values.put(this.PHOTO,photoByte );
        }
        db.insert(NOTE_TABLE_NAME, null, values);
        //  db.close();

    }
    public Cursor getNoteCursorByCategory(String whereClause) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT * FROM " + NOTE_TABLE_NAME
                + (whereClause.isEmpty() ? "" : " WHERE "+CATEGORY+" = '"+ whereClause+"'");
        Cursor cursor = db.rawQuery(searchQuery, null);
        return cursor;
    }
    public Cursor getNoteCursorFiltered(String whereClause) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT * FROM " + NOTE_TABLE_NAME
                + (whereClause.isEmpty() ? "" : " WHERE "+TITLE+" LIKE '%" + whereClause+"%'");
        Cursor cursor = db.rawQuery(searchQuery, null);
        return cursor;
    }

    public Cursor getNoteCursor(String category) {

        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "SELECT * FROM " + NOTE_TABLE_NAME+" WHERE "+CATEGORY+"= '"+category+ "' ORDER BY "+TITLE+" ASC";

        Cursor cursor = db.rawQuery(searchQuery, null);


        return cursor;
    }
    public Cursor getNoteCursorDESC(String category) {

        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "SELECT * FROM " + NOTE_TABLE_NAME+" WHERE "+CATEGORY+"= '"+category+ "' ORDER BY "+TITLE+" DESC";

        Cursor cursor = db.rawQuery(searchQuery, null);


        return cursor;
    }
    public Cursor getNoteCursorDateDESC(String category) {

        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "SELECT * FROM " + NOTE_TABLE_NAME+" WHERE "+CATEGORY+"= '"+category+ "' ORDER BY "+DATE+" DESC";

        Cursor cursor = db.rawQuery(searchQuery, null);


        return cursor;
    }
    public Cursor getNoteCursorDateASC(String category) {

        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "SELECT * FROM " + NOTE_TABLE_NAME+" WHERE "+CATEGORY+"= '"+category+ "' ORDER BY "+DATE+" ASC";

        Cursor cursor = db.rawQuery(searchQuery, null);


        return cursor;
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + NOTE_TABLE_NAME + " WHERE _id = " + Integer.toString(id));

        System.out.printf("Data with id=%d deleted\n", id);
    }

    public ArrayList<String> getAllCategoryArray() {
        System.out.println("getting Data from Notes");
        String selectQuery = "select DISTINCT "+CATEGORY+" from " + NOTE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        final  ArrayList<String> allCategoryArray = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            System.out.println(" (CATEGORY) GETData inside");
            do {

                String category = cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY));
                System.out.println(category);


                allCategoryArray.add(category);


            } while (cursor.moveToNext());
        }

        //db.close();

        return allCategoryArray;

    }

    public ArrayList<Note> getAllNotes() {
        System.out.println("getting Data from Notes");
        String selectQuery = "select * from " + NOTE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        final  ArrayList<Note> noteArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            System.out.println(" (NOTE) GETData inside");
            do {
                int id =  cursor.getInt(cursor.getColumnIndexOrThrow(_id));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
                String context = cursor.getString(cursor.getColumnIndexOrThrow(CONTEXT));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE));
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(DATE));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY));
                Bitmap photo;
                byte[] imageByte = cursor.getBlob(cursor.getColumnIndexOrThrow(PHOTO));
                if (imageByte == null){
                    photo =null;
                }
                else {
                    photo = DbBitmapUtility.getImage(imageByte);
                }
                Note note = new Note(id,category,title,context,longitude,latitude,date,address,photo);


                System.out.println(note.title);
                noteArrayList.add(note);
                System.out.println("Data from DataBase :" + cursor.getString(1).toString());

            } while (cursor.moveToNext());
        }


        return noteArrayList;

    }




}
