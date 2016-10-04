package dimpesh.com.nearbyeasy.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import dimpesh.com.nearbyeasy.MainActivity;
import dimpesh.com.nearbyeasy.R;

/**
 * Created by DIMPESH : ${month}
 */

public class WidgetProvider extends AppWidgetProvider
{
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
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
/*
            boolean useDetailActivity = context.getResources()
                    .getBoolean(R.bool.use_detail_activity);
            Intent clickIntentTemplate = useDetailActivity
                    ? new Intent(context, GraphDisplayActivity.class)
                    : new Intent(context, MyStocksActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mViews.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
*/
/*
            mViews.setEmptyView(R.id.widget_list, R.id.widget_empty);
*/

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetIds, mViews);

//              Earlier for this
//            appWidgetManager.updateAppWidget(widgetId,mViews);
//            Intent intent = new Intent(context, GraphDisplayActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            mViews.setOnClickPendingIntent(R.id.widget, pendingIntent);


            /*
        // setting collection for Specific Version
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            {
                setRemo
            }
        }
        */
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private RemoteViews initViews(Context context,AppWidgetManager widgetManager,int widgetId)
    {
//        RemoteViews mView=new RemoteViews(context.getPackageName(),R.layout.widget_detail);
        Intent intent=new Intent(context,WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//        mView.setRemoteAdapter(widgetId,R.id.widgetCollectionList,intent);

//        return mView;
        return null;
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

}
