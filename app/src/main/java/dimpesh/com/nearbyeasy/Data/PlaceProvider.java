package dimpesh.com.nearbyeasy.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by DIMPESH : ${month}
 */

public class PlaceProvider extends ContentProvider {
    public static final String TAG = PlaceProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PlaceDbHelper placeDbHelper;

    // Codes For UriMatcher
    private static final int PLACE = 100;
    private static final int PLACE_WITH_ID = 200;


    private static UriMatcher buildUriMatcher() {

        // Build URI Matcher By adding specific code to return based on common to use NO_MATCHER

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = PlaceContract.CONTENT_AUTHORITY;
        // add code for each type we add the Add Uri matcher

        matcher.addURI(authority, PlaceContract.PATH_PLACE, PLACE);
        matcher.addURI(authority, PlaceContract.PlaceEntry.TABLE_PLACE + "/*", PLACE_WITH_ID);


        return matcher;


    }

    @Override
    public boolean onCreate() {

        placeDbHelper = new PlaceDbHelper(getContext());
        return true;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        //     Cursor retCursor;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PLACE: {
                return PlaceContract.PlaceEntry.CONTENT_DIR_TYPE;
            }
            case PLACE_WITH_ID:
                return PlaceContract.PlaceEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unsupported URI :" + uri);
        }

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selArgs, String sort) {
        Cursor retCursor = null;

        switch (sUriMatcher.match(uri)) {
            case PLACE: {
                retCursor = placeDbHelper.getReadableDatabase().query(PlaceContract.PlaceEntry.TABLE_PLACE, projection, selection, selArgs, null, null, sort);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;

            }

            case PLACE_WITH_ID: {
                retCursor = placeDbHelper.getReadableDatabase().query(PlaceContract.PlaceEntry.TABLE_PLACE, projection, PlaceContract.PlaceEntry.COLUMN_ID, new String[]{String.valueOf(ContentUris.parseId(uri))}, null, null, sort);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);

                return retCursor;

            }
            default:
                throw new UnsupportedOperationException("Unsupported Uri :" + uri);
        }
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {


        SQLiteDatabase db=placeDbHelper.getWritableDatabase();
        Uri returnUri;

        switch(sUriMatcher.match(uri))
        {
            case PLACE :
            {
                long _id=db.insert(PlaceContract.PlaceEntry.TABLE_PLACE,null,contentValues );
                if (_id>0)
                {
                    returnUri=PlaceContract.PlaceEntry.buildPlaceUri(_id);

                }
                else
                    throw new android.database.SQLException("Unsupported URI :" + uri);
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown URI :"+uri);


        }
        getContext().getContentResolver().notifyChange(uri, null);


        db.close();
        return returnUri;


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db=placeDbHelper.getWritableDatabase();
        int rowsDeleted;
//        This will delete all rows returning the number of rows deleted
//        if(null==selection)
//            selection="1"
        switch(sUriMatcher.match(uri))
        {
            case PLACE :
                rowsDeleted=db.delete(PlaceContract.PlaceEntry.TABLE_PLACE,selection,selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI : "+uri);

        }

        //
        if(rowsDeleted!=0)
            getContext().getContentResolver().notifyChange(uri,null);

        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
    {

        SQLiteDatabase db=placeDbHelper.getWritableDatabase();
        int rowsUpdated;
        switch(sUriMatcher.match(uri))
        {
            case PLACE :
                rowsUpdated=db.update(PlaceContract.PlaceEntry.TABLE_PLACE,contentValues,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI :"+uri);
        }
        if(rowsUpdated!=0)
            getContext().getContentResolver().notifyChange(uri,null);

        return rowsUpdated;

    }


}