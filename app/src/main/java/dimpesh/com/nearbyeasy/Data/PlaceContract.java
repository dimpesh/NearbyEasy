package dimpesh.com.nearbyeasy.Data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DIMPESH : ${month}
 */

public class PlaceContract {
    public static final String CONTENT_AUTHORITY = "dimpesh.com.nearbyeasy.app";

    // Base Content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Specify path for  places...
    public static final String PATH_PLACE = "place";


    public static final class PlaceEntry implements BaseColumns {
        // Table Name Declaration
        public static final String TABLE_PLACE = "place";

        // Columns Name and Types defined Here.

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PLACEID = "_placeid";
        public static final String COLUMN_NAME = "_name";
        public static final String COLUMN_PHOTOREF = "_photoref";
        public static final String COLUMN_ICON = "_icon";
        public static final String COLUMN_OPEN = "_open";
        public static final String COLUMN_PHONE = "_phone";
        public static final String COLUMN_ADDRESS = "_address";
        public static final String COLUMN_VICINITY = "_vicinity";

        // Content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_PLACE).build();

        // Content Dir Type defined
        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;

        // Content Item Type
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;


        public static Uri buildMoviesUri(long id) {

//            return CONTENT_URI.buildUpon().appendPath(id).build();
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }




}

