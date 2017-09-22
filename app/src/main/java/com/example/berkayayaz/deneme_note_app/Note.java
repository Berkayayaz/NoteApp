package com.example.berkayayaz.deneme_note_app;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by BerkayAyaz on 2016-08-11.
 */
public class Note {

    int noteID;
    String title;
    String context;
    double longitude,latitude;
    long date;
    String address;
    Bitmap photo;
    String category;



    public Note(int noteID,String category, String title, String context,double longitude,double latitude,
                long date,String address,Bitmap photo){
        this.noteID = noteID;
        this.title = title;
        this.context = context;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
        this.address = address;
        this.photo = photo;
        this.category = category;



    }

    public Note(String category,String title, String context,double longitude,double latitude,
                long date,String address,Bitmap photo){
        this.category = category;
        this.title = title;
        this.context = context;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
        this.address = address;
        this.photo = photo;


    }


    public Note(){
        noteID = 0;
        title = "";
        context = "";
        longitude = 0;
        latitude = 0;
        date = 0;
        address = "";
        photo = null;
        category = "";

    }
}