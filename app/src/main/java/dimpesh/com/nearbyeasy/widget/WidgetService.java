package dimpesh.com.nearbyeasy.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import dimpesh.com.nearbyeasy.Data.PlaceContract;

/**
 * Created by DIMPESH : ${month}
 */

public class WidgetService extends RemoteViewsService {
    public final String TAG = WidgetService.class.getSimpleName();

    private static final String[] PLACE_COLUMNS = {
            PlaceContract.PlaceEntry.COLUMN_ID,
            PlaceContract.PlaceEntry.COLUMN_NAME,
            PlaceContract.PlaceEntry.COLUMN_ICON,
            PlaceContract.PlaceEntry.COLUMN_VICINITY
    };
    // Index must match to the Projection for retrieving database content.
    static final int INDEX_COLUMN_ID = 0;
    static final int INDEX_COLUMN_NAME = 1;
    static final int INDEX_COLUMN_ICON = 2;
    static final int INDEX_COLUMN_VICINITY = 3;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to handle here...
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                try {

/*
                        data = WidgetService.this.getContentResolver().query(PlaceContract.PlaceEntry.CONTENT_URI,
                                new String[]{QuoteColumns.SYMBOL,QuoteColumns.BIDPRICE,
                                        QuoteColumns.PERCENT_CHANGE,
                                        QuoteColumns.CHANGE,}, QuoteColumns.ISCURRENT + " = ?",
                                new String[]{"1"}, null);
*/
                    data = WidgetService.this.getContentResolver().query(PlaceContract.PlaceEntry.CONTENT_URI,
                            PLACE_COLUMNS, null,
                            null, null);


                    data.moveToFirst();
                    Binder.restoreCallingIdentity(identityToken);

                } catch (Exception e) {
                    Log.v("Widget Gen. Exception :", e.getMessage());
                }


            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        android.R.layout.simple_list_item_1);

                String name = data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_NAME));
                String vicinity = data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_VICINITY));
                String icon = data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ICON));

                views.setTextViewText(android.R.id.text1,name+"\t\t"+vicinity);

                final Intent fillInIntent = new Intent();
/*
                fillInIntent.putExtra("symbol", data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
*/
                views.setOnClickFillInIntent(android.R.id.text1, fillInIntent);


                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_COLUMN_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
