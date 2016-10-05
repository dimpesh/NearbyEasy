package dimpesh.com.nearbyeasy.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DIMPESH : ${month}
 */

public class PlaceDbHelper extends SQLiteOpenHelper {
    public static final String TAG = PlaceDbHelper.class.getSimpleName();

    // Database Name
    public static final String DATABASE_NAME = "places.db";

    // Database Version
    public static final int DATABASE_VERSION = 1;


    public PlaceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating the Database...

//  OPEN is Stored as String only instead of boolean so Change the DetailActivityFragment also.


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PLACE_TABLE = "CREATE TABLE " + PlaceContract.PlaceEntry.TABLE_PLACE + "("
                + PlaceContract.PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlaceContract.PlaceEntry.COLUMN_PLACEID + " TEXT NOT NULL,"
                + PlaceContract.PlaceEntry.COLUMN_NAME + " TEXT,"
                + PlaceContract.PlaceEntry.COLUMN_PHOTOREF + " TEXT,"
                + PlaceContract.PlaceEntry.COLUMN_ICON + " TEXT,"
                + PlaceContract.PlaceEntry.COLUMN_OPEN + " TEXT,"
                + PlaceContract.PlaceEntry.COLUMN_PHONE + " TEXT,"
                + PlaceContract.PlaceEntry.COLUMN_ADDRESS + " TEXT,"
                + PlaceContract.PlaceEntry.COLUMN_VICINITY + " TEXT );";

        // Executing Query
        db.execSQL(SQL_CREATE_PLACE_TABLE);

    }

    //  onUpdate Database Code...
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop the Table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlaceContract.PlaceEntry.TABLE_PLACE);

        // Recreate the Table
        onCreate(sqLiteDatabase);

    }

}
