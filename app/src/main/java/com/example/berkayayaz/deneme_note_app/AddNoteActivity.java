package com.example.berkayayaz.deneme_note_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddNoteActivity extends AppCompatActivity {
    DBHelperNote db;
    //boolean result=Utility.checkPermission(AddNoteActivity.this);
    String userImageChoosenTask;
    int SELECT_FILE = 1;
    int REQUEST_CAMERA = 0;
    ImageView ivImage, locationImage;
    boolean registeredNote;
    Bitmap pickedPhoto;
    EditText titleEdit, noteEdit;
    String noteTitle, noteContent;
    Double latitude, longitude;
    Toolbar toolbar;
    long registeredNoteid;
    String selectedCategory;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        titleEdit = (EditText) findViewById(R.id.titleEditView);
        noteEdit = (EditText) findViewById(R.id.editText);
        ivImage = (ImageView) findViewById(R.id.imageView);

        db = new DBHelperNote(this);
        ArrayList<String> categoryArrayList = new ArrayList<String>();
        categoryArrayList = db.getAllCategoryArray();

        Intent intent = getIntent();
        registeredNote = intent.getExtras().getBoolean("registeredNote");
        selectedCategory = intent.getStringExtra("selectedCategory");
        System.out.println("*********"+selectedCategory);
        if (registeredNote){
            System.out.println("registeredNote TRUE");
            registeredNoteid = intent.getLongExtra("id",0);
            System.out.println("INTENT TEST DATABASE ID"+Long.toString(registeredNoteid));

            Note note = db.getNoteByID(registeredNoteid);
            titleEdit.setText(note.title);
            noteEdit.setText(note.context);
            //FIXME: image seems null
            if (note.photo != null) {
                ivImage.setImageBitmap(note.photo);
                pickedPhoto = note.photo;
            }
        }
        else {
            System.out.print("registeredNote FALSE");

        }

        final GPSTracker gps = new GPSTracker(this);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        Location lc = gps.getLocation();
        if(gps.canGetLocation()){
            
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registeredNote) {
                    System.out.println("INSIDE FLOATBUTTON registeredNote TRUE");
                    if (!TextUtils.isEmpty(titleEdit.getText().toString()) && !TextUtils.isEmpty(noteEdit.getText().toString())) {
                        noteTitle = titleEdit.getText().toString();
                        noteContent = noteEdit.getText().toString();
                        long epoch = System.currentTimeMillis() ;
                        Note newNote = new Note();
                        newNote.noteID = (int)registeredNoteid;
                        newNote.title = noteTitle;
                        newNote.context = noteContent;
                        newNote.address = "LAT:"+latitude+"    LONG:"+longitude;
                        newNote.date = epoch;
                        newNote.category = selectedCategory;
                        newNote.latitude = latitude;
                        newNote.longitude = longitude;
                        newNote.photo = pickedPhoto;
                        db.updateNoteByID(newNote);


                        Intent saveNote = new Intent(view.getContext(), CategoryListActivity.class);
                        startActivity(saveNote);
                    } else {
                        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(AddNoteActivity.this);
                        alert.setTitle("Missing fields");
                        alert.setMessage("You haven't filled out all the fields");
                        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alert.show();
                    }

                } else {
                    if (!TextUtils.isEmpty(titleEdit.getText().toString()) && !TextUtils.isEmpty(noteEdit.getText().toString())) {
                        noteTitle = titleEdit.getText().toString();
                        noteContent = noteEdit.getText().toString();
                        Note newNote = new Note();
                        long epoch = System.currentTimeMillis() ;
                        newNote.title = noteTitle;
                        newNote.context = noteContent;
                        newNote.address = "LAT:"+latitude+"    LONG:"+longitude;
                        newNote.date = epoch;
                        newNote.latitude = latitude;
                        newNote.category = selectedCategory;
                        newNote.longitude = longitude;
                        newNote.photo = pickedPhoto;
                        db.insertNote(newNote);


                        Intent saveNote = new Intent(view.getContext(), CategoryListActivity.class);
                        startActivity(saveNote);
                    } else {
                        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(AddNoteActivity.this);
                        alert.setTitle("Missing fields");
                        alert.setMessage("You haven't filled out all the fields");
                        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alert.show();
                    }

                }
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.camera) {

            selectImage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // CAMERA AND GALLERY FUNCTIONS
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userImageChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userImageChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(AddNoteActivity.this);

                if (items[item].equals("Take Photo")) {
                    userImageChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userImageChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Store taken image from camera or we can store them in array
        pickedPhoto = thumbnail;
        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Store taken from gallery image or we can store them in array

        pickedPhoto = bm;
        ivImage.setImageBitmap(bm);
    }


    //CAMERA AND GALLERY FUNCTIONS END

}




