package dimpesh.com.nearbyeasy.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dimpesh.com.nearbyeasy.MainActivityFragment;
import dimpesh.com.nearbyeasy.R;

/**
 * Created by DIMPESH : ${month}
 */

public class PlaceCursorAdapter extends CursorAdapter {

    public static final String TAG=PlaceCursorAdapter.class.getSimpleName();
    private Context mContext;
    private int loader_id;
    public PlaceCursorAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
        mContext=context;
        loader_id=flags;
        Log.v(TAG,"PlaceCursorAdapter-------Constructor Called--------------");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        Log.v(TAG,"MovieCursorAdapter------------newViewCalled----------------");
        View view= LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        Log.v(TAG,"PlaceCursorAdapter:---------- Bind View Called---------");

        TextView tv_name,tv_vicinity;
        ImageView iv_icon;

        tv_name = (TextView) view.findViewById(R.id.item_title);
        tv_vicinity = (TextView) view.findViewById(R.id.item_vicinity);
        iv_icon=(ImageView)view.findViewById(R.id.item_img);
        String name=cursor.getString(MainActivityFragment.COL_NAME);
        String vicinity=cursor.getString(MainActivityFragment.COL_VICINITY);
        String icon=cursor.getString(MainActivityFragment.COL_ICON);

        tv_name.setText(name);
        tv_vicinity.setText(vicinity);
        Picasso.with(context)
                .load(icon).placeholder(R.drawable.img_placeholder)
                .resize(50,50)
                .centerCrop()
                .error(R.drawable.img_placeholder)
                .into(iv_icon);
    }

    // tried implementing getCount... can be replaced...

    @Override
    public int getCount() {

        if(getCursor()==null)
            return 0;
        return super.getCount();
    }
}
