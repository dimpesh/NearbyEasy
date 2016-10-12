package dimpesh.com.nearbyeasy.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import java.net.URL;

import dimpesh.com.nearbyeasy.Data.PlaceContract;
import dimpesh.com.nearbyeasy.R;

/**
 * Created by DIMPESH : ${month}
 */

public class PlaceWidget extends AppWidgetProvider
{
    public static String FORCE_WIDGET_UPDATE = "com.widget.FORCE_WIDGET_UPDATE";

    public static String TAG=PlaceWidget.class.getSimpleName();
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


    public void updatePlace(Context context, AppWidgetManager appWidgetManager,int []appWidgetIds)
    {
//        Log.v(TAG,"updatePlace Method with 3 arguments Called");
        Cursor lastPlace;
        ContentResolver  cr=context.getContentResolver();
        lastPlace=cr.query(PlaceContract.PlaceEntry.CONTENT_URI,
                PLACE_COLUMNS, null,
                null, null);

        String name="";
        String icon="";
        String vicinity="";
        if (lastPlace != null) {
            try {
                if (lastPlace.moveToLast()) {
                     name = lastPlace.getString(lastPlace.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_NAME));
                     icon = lastPlace.getString(lastPlace.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ICON));
                     vicinity = lastPlace.getString(lastPlace.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_VICINITY));

                }
            }
            finally
            {
                lastPlace.close();
            }
        }


        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.place_widget);
            views.setTextViewText(R.id.widget_name_text_view, name);
            views.setTextViewText(R.id.widget_vicinity_text_view,vicinity);
            URL newurl = null;
            try {
                newurl = new URL(icon);
                Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
                views.setImageViewBitmap(R.id.widget_icon_image_view,mIcon_val);

            } catch (Exception e) {
                e.printStackTrace();
                views.setImageViewBitmap(R.id.widget_icon_image_view,null);
            }


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }








    public void updatePlace(Context context) {
//        Log.v(TAG,"updatePlace Method with 1 argument Called");

        ComponentName thisWidget = new ComponentName(context,
                PlaceWidget.class);
        AppWidgetManager appWidgetManager =
                AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        updatePlace(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        Log.v(TAG,"onUpdate Override Called");
        // Update the Widget UI with the latest Place details.
        updatePlace(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        Log.v(TAG,"onReceive Called");
        if(PlaceWidget.FORCE_WIDGET_UPDATE.equals(intent.getAction())){
            updatePlace(context);
        }
    }
}
