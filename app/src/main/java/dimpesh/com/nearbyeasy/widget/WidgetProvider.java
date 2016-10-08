package dimpesh.com.nearbyeasy.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import dimpesh.com.nearbyeasy.MainActivity;
import dimpesh.com.nearbyeasy.R;

/**
 * Created by DIMPESH : ${month}
 */

public class WidgetProvider extends AppWidgetProvider {
    // for updating widget
    public static final String ACTION_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String DATABASE_CHANGED = " widget.DATABASE_CHANGED";
    public static final String TAG=WidgetProvider.class.getSimpleName();
    int[] APP_WIDGET_ID = {1};

    @Override
    public void onReceive(Context context, Intent intent) {
/*
        super.onReceive(context, intent);
        Log.v(TAG,"onReceive Called");
        String action = intent.getAction();
        if (action.equals(ACTION_UPDATE)) {
            onUpdateWidget(context);
        }
*/
        String action = intent.getAction();
        if (action.equals(DATABASE_CHANGED) || action.equals(Intent.ACTION_DATE_CHANGED))
        {
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            int[] ids = gm.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
            this.onUpdate(context, gm, ids);
        }
        else
        {
            super.onReceive(context, intent);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            Log.v(TAG,"onUpdate Called");
            RemoteViews mViews = initViews(context, appWidgetManager, widgetId);
            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            mViews.setOnClickPendingIntent(R.id.widget, pendingIntent);


            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, mViews);
            } else {
                setRemoteAdapterV11(context, mViews);
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId,R.id.widget_list);
            appWidgetManager.updateAppWidget(appWidgetIds, mViews);

        }
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private RemoteViews initViews(Context context, AppWidgetManager widgetManager, int widgetId) {
        RemoteViews mView = new RemoteViews(context.getPackageName(), R.layout.widget_detail);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(widgetId, R.id.widgetCollectionList, intent);

        return mView;
    }


    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    public void onUpdateWidget(Context context)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }


}
