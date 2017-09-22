package com.example.berkayayaz.deneme_note_app;



    import android.content.Context;
    import android.database.Cursor;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.SimpleAdapter;
    import android.widget.SimpleCursorAdapter;
    import android.widget.TextView;

    import java.util.ArrayList;

    /**
     * Created by BerkayAyaz on 2016-08-12.
     */

    public class CustomListSimpleAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private Context appContext;
        private int layout;
        private Cursor cr;
        private final LayoutInflater inflater;
        DBHelperNote db;

        private static final String _id = "_id";
        private static final String TITLE = "title";
        private static final String CONTEXT = "context";
        private static final String LONGITUDE = "longitude";
        private static final String LATITUDE = "latitude";
        private static final String DATE = "date";
        private static final String ADDRESS = "address";
        public CustomListSimpleAdapter(DBHelperNote db,Context context, int layout, ArrayList<String> categoryArray) {
            super(context, layout,categoryArray);
            this.db =db;
            this.layout = layout;
            this.mContext = context;
            this.inflater = LayoutInflater.from(context);




        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row ==null){

                row = inflater.inflate(layout, parent, false);


                TextView pCategory = (TextView) row.findViewById(R.id.cell_categoryName);

                row.setTag(position);

                pCategory.setText(getItem(position));

//                btnDelRow.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int id = (int) ((Button) v).getTag();
//                        String categoryName = getItem(position);
//                        System.out.printf("Delete button tapped (id=%d)\n", id);
//                        db.deleteAllNotesByCategory(categoryName);
//
////
////
////                        swapCursor(dbHelper.getNoteCursor());
//                        notifyDataSetChanged();
//                    }
//                });
            }


            return row;
        }



    }
