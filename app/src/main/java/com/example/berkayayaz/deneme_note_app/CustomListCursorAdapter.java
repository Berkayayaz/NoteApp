package com.example.berkayayaz.deneme_note_app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by BerkayAyaz on 2016-08-11.
 */

    public class CustomListCursorAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private Context appContext;
        private int layout;
        private Cursor cr;
        private final LayoutInflater inflater;
        private  final  DBHelperNote dbHelper;
    private static final String _id = "_id";
    private static final String TITLE = "title";
    private static final String CONTEXT = "context";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String DATE = "date";
    private static final String ADDRESS = "address";
    private static final String PHOTO = "photo";
    public CustomListCursorAdapter(DBHelperNote dbHelper, Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        this.layout = layout;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.cr = c;
        this.dbHelper = dbHelper;
    }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            TextView pTitle = (TextView) view.findViewById(R.id.cell_titleTextView);
            TextView pDate = (TextView) view.findViewById(R.id.cell_dateTextView);
            TextView pLocation = (TextView) view.findViewById(R.id.cell_locationTextView);
            Button btnDelRow = (Button) view.findViewById(R.id.cell_delete_btn);



System.out.println("Check Cursor cell ID:"+(cursor.getString(cursor.getColumnIndexOrThrow(_id)))+" title:" + (cursor.getString(cursor.getColumnIndexOrThrow(TITLE))));
            pTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
            long epochTime = cursor.getLong(cursor.getColumnIndexOrThrow(DATE));
            pDate.setText(date(epochTime));
            pLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS)));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
            byte[] photoByte = cursor.getBlob(cursor.getColumnIndexOrThrow(PHOTO));
            final String category = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.CATEGORY));
            Bitmap photo;
            if (photoByte != null) {
                photo = DbBitmapUtility.getImage(photoByte);
            }
            //FIXME:ADD photo IMAGEVIEW ON CELL
            getItemId(cursor.getPosition());

            btnDelRow.setTag(id);

          //  Bitmap lastPhoto = dbHelper.getLastPhotoByNoteID(id);
            //pImage.setImageBitmap(lastPhoto);

            btnDelRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int) ((Button) v).getTag();

                    System.out.printf("Delete button tapped (id=%d)\n", id);

                    dbHelper.deleteNote(id);

                    swapCursor(dbHelper.getNoteCursor(category));
                    notifyDataSetChanged();
                }
            });
        }
    public String date(long epochTime){
        Date date = new Date(epochTime);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        return format.format(date);
    }
    }