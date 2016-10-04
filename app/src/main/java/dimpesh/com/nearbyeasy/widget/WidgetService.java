package dimpesh.com.nearbyeasy.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by DIMPESH : ${month}
 */

public class WidgetService extends RemoteViewsService {
    public final String TAG = WidgetService.class.getSimpleName();

    /*
        private static final String [] STOCK_COLUMNS={
                QuoteColumns._ID,
                QuoteColumns.SYMBOL,
                QuoteColumns.BIDPRICE,
                QuoteColumns.PERCENT_CHANGE
        };
    */
// Index must match to the Projection for retrieving database content.
/*
    static final int INDEX_STOCK_ID = 0;
    static final int INDEX_STOCK_SYMBOL = 1;
    static final int INDEX_STOCK_BIDPRICE = 2;
    static final int INDEX_STOCK_PERCENT_CHANGE = 3;
*/


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

                    final long identityToken = Binder.clearCallingIdentity();

                    try {
/*
                        data = WidgetService.this.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                                new String[]{QuoteColumns.SYMBOL,QuoteColumns.BIDPRICE,
                                        QuoteColumns.PERCENT_CHANGE,
                                        QuoteColumns.CHANGE,}, QuoteColumns.ISCURRENT + " = ?",
                                new String[]{"1"}, null);

                        data.moveToFirst();
                        Binder.restoreCallingIdentity(identityToken);
*/
                    } catch (Exception e) {
                        Log.v("Widget Gen. Exception :", e.getMessage());
                    }
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
                return null;
            }

            @Override
            public RemoteViews getLoadingView() {
//                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
                // comment this line..after uncommenting above
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
/*
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_STOCK_ID);
                return position;
*/
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
