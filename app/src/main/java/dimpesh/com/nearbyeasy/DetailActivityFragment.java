package dimpesh.com.nearbyeasy;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import dimpesh.com.nearbyeasy.Data.PlaceContract;
import dimpesh.com.nearbyeasy.widget.WidgetProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public static final String TAG = DetailActivityFragment.class.getSimpleName();
    boolean isFavourite = false;
    ImageView iv_head;
    ProgressBar pg;
    ImageView iv_icon;
    TextView rating;
    TextView address;
    TextView phone;
    TextView vicinity;
    FloatingActionButton fab;
    String title;
    public static MyObject mRecieved;
    public PlaceObject placeObject = new PlaceObject();
    TextView titleVicinity, titleAddress;
    public Typeface Courgette;
    public Typeface BalooBhaina;
    AdView mAdView;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView Called");
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);
        pg = (ProgressBar) view.findViewById(R.id.detail_progress);
        iv_head = (ImageView) view.findViewById(R.id.detail_img_head);
        iv_icon = (ImageView) view.findViewById(R.id.detail_img_icon);
        rating = (TextView) view.findViewById(R.id.detail_rating);
        address = (TextView) view.findViewById(R.id.detail_address);
        phone = (TextView) view.findViewById(R.id.detail_phone);
        vicinity = (TextView) view.findViewById(R.id.detail_vicinity);
        fab = (FloatingActionButton) view.findViewById(R.id.detail_fab);

        // Fontface declaration...
        Courgette = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "Courgette-Regular.ttf");
        BalooBhaina = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "BalooBhaina-Regular.ttf");


        String str = mRecieved.getId();
        String name = mRecieved.getName();
        title = name;

        new SearchDetailTask().execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + str + "&key=" + BuildConfig.MyGoogleMapKey);
        getActivity().setTitle(title);

        // Reference for Font Style Change...
        titleVicinity = (TextView) view.findViewById(R.id.detail_vicinity_title);
        titleAddress = (TextView) view.findViewById(R.id.detail_address_title);
        titleVicinity.setTypeface(BalooBhaina);
        titleAddress.setTypeface(BalooBhaina);

        String tryUri=PlaceContract.PlaceEntry.CONTENT_URI+"";
        Log.v(TAG,"URI : "+tryUri);
        // Icon For Fab
        String url = "content://dimpesh.com.nearbyeasy.app/place";

        Uri fetchUri = Uri.parse(url);
        Cursor findQuery = getContext().getContentResolver().query(fetchUri, null, "_placeid='" + mRecieved.getId()+"'", null, null);
        try {
            if (findQuery.moveToFirst()) {
                isFavourite = true;
            } else {
                isFavourite=false;
            }
        }
        catch (Exception e)
        {
            Log.v(TAG,"Cursor Exception while fetching fab icon");
        }
        if (isFavourite == true) {
            fab.setImageResource(R.drawable.like);

        } else {
            fab.setImageResource(R.drawable.dislike);

        }

        // fab click listener.

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v(TAG,"onClick Called");
                String url = "content://dimpesh.com.nearbyeasy.app/place";

                Uri fetchUri = Uri.parse(url);

                // Add to Database...
                if (isFavourite == false) {
                    ContentValues values = new ContentValues();
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACEID, mRecieved.getId());
                    values.put(PlaceContract.PlaceEntry.COLUMN_NAME, mRecieved.getName());
                    values.put(PlaceContract.PlaceEntry.COLUMN_PHOTOREF, placeObject.getPhotoReference());
                    values.put(PlaceContract.PlaceEntry.COLUMN_ICON, placeObject.getIcon());
                    String checkOpen;
                    if (placeObject.getOpen())
                        checkOpen = "true";
                    else
                        checkOpen = "false";

                    values.put(PlaceContract.PlaceEntry.COLUMN_OPEN, checkOpen);
                    values.put(PlaceContract.PlaceEntry.COLUMN_PHONE, phone.getText().toString());
                    values.put(PlaceContract.PlaceEntry.COLUMN_ADDRESS, address.getText().toString());
                    values.put(PlaceContract.PlaceEntry.COLUMN_VICINITY, vicinity.getText().toString());

                    // Intert into Database...
                    Uri uri = getActivity().getApplicationContext().getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, values);

                    Toast.makeText(getActivity(), getString(R.string.add_success), Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.like);
                    isFavourite = true;

                }
                else
                {
                    Toast.makeText(getActivity(),getString(R.string.remove_success),Toast.LENGTH_SHORT).show();
                    int delID=getActivity().getApplicationContext().getContentResolver().delete(fetchUri,"_placeid='"+mRecieved.getId()+"'",null);
                    fab.setImageResource(R.drawable.dislike);
                    isFavourite=false;

                }

                // Handling widget Update on Changing Favorite
/*
                ComponentName name = new ComponentName(getActivity(), WidgetProvider.class);
                int[] ids = AppWidgetManager.getInstance(getActivity()).getAppWidgetIds(name);

                Intent intent = new Intent(getActivity(), WidgetProvider.class);
                intent.setAction(WidgetProvider.ACTION_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                getActivity().sendBroadcast(intent);
*/

                sendUpdateIntent(getActivity().getApplicationContext());
            }
        });

        // Code for loading Banner Ads
        mAdView = (AdView) view.findViewById(R.id.detail_admob);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


        return view;
    }


    public class SearchDetailTask extends AsyncTask<String, Void, PlaceObject> {
        PlaceObject mObj = new PlaceObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg.setVisibility(View.VISIBLE);
        }

        @Override
        protected PlaceObject doInBackground(String... arg0) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String response = null;

            URL url = null;
            try {
                url = new URL(arg0[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    response = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    response = null;
                }
                response = buffer.toString();
                JSONObject obj1 = new JSONObject(response);
                JSONObject obj2 = obj1.getJSONObject("result");

                mObj.setAddr(obj2.getString("formatted_address"));
                mObj.setPhno(obj2.getString("formatted_phone_number"));
                mObj.setIcon(obj2.getString("icon"));
                mObj.setInternationalPhno(obj2.getString("international_phone_number"));
                mObj.setName(obj2.getString("name"));
                mObj.setVicinity(obj2.getString("vicinity"));
                mObj.setWebsite(obj2.getString("website"));

                JSONArray arr = obj2.getJSONArray("photos");
                JSONObject obj3 = arr.getJSONObject(0);
                String str = obj3.getString("photo_reference");
                mObj.setPhotoReference(obj3.getString("photo_reference"));
                JSONObject obj4 = obj2.getJSONObject("geometry");
                JSONObject obj5 = obj4.getJSONObject("location");
                JSONObject open_hrs = obj2.getJSONObject("opening_hours");
                mObj.setOpen(open_hrs.getBoolean("open_now"));
                String la = obj5.getString("lat");
                String lo = obj5.getString("lng");
                mObj.setLatitude(la);
                mObj.setLongitude(lo);


                return mObj;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mObj != null)
                return mObj;
            return null;
        }

        @Override
        protected void onPostExecute(PlaceObject result) {
            super.onPostExecute(result);
            pg.setVisibility(View.INVISIBLE);
            if (result == null) {
                Toast.makeText(getActivity(), getString(R.string.null_data), Toast.LENGTH_SHORT).show();
            }
            try {
                Log.v(TAG,"latitude :"+ result.getLatitude());
                Log.v(TAG, "longitude : "+result.getLongitude());

                Log.v(TAG, "result" + result.getPhotoReference());
            } catch (Exception e) {
/*
                Toast.makeText(getActivity(),"Complete Data Not Available",Toast.LENGTH_SHORT).show();
*/
            }
            populateView(result);


        }
    }

    public void populateView(PlaceObject result) {
        placeObject = result;
        if(result!=null){
        String photoRef = result.getPhotoReference();
        String urlStr = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef + "&key="+BuildConfig.MyGoogleMapKey;

        Picasso.with(getActivity()).load(urlStr).placeholder(R.drawable.img_placeholder)
                .into(iv_head);

        Log.v(TAG, result.getOpen() + "");
        address.setText(result.getAddr());
        address.setTypeface(Courgette);
        vicinity.setTypeface(Courgette);
        phone.setText(result.getPhno());
        phone.setTypeface(BalooBhaina);
        Picasso.with(getActivity()).load(result.getIcon()).placeholder(R.drawable.img_placeholder)
                .into(iv_icon);
        vicinity.setText(result.getVicinity());
        if (result.getOpen()) {
            rating.setText("OPEN");
            rating.setTypeface(Courgette);
        } else {
            rating.setText("CLOSED");
            rating.setTypeface(Courgette);

        }
    }
        else
        {
            String url = "content://dimpesh.com.nearbyeasy.app/place";
            Uri fetchUri = Uri.parse(url);
            try {
                Cursor findQuery = getContext().getContentResolver().query(fetchUri, null, "_placeid='" + mRecieved.getId() + "'", null, null);
                if(findQuery!=null)
                {
                    findQuery.moveToFirst();
                    address.setText(findQuery.getString(findQuery.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ADDRESS)));
                    vicinity.setText(findQuery.getString(findQuery.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_VICINITY)));
                    rating.setText(findQuery.getString(findQuery.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_OPEN)));
                    String urlStr = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + findQuery.getString(findQuery.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PHOTOREF)) + "&key="+BuildConfig.MyGoogleMapKey;
                    Picasso.with(getActivity()).load(urlStr)
                            .placeholder(R.drawable.img_placeholder)
                            .error(R.drawable.img_placeholder)
                            .into(iv_head);

                    Picasso.with(getActivity()).load(findQuery.getString(findQuery.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ICON)))
                            .error(R.drawable.img_placeholder)
                            .placeholder(R.drawable.img_placeholder)
                            .into(iv_icon);

                }
            }
            catch(Exception e)
            {
                Log.v(TAG,"Offline Data is not Available...");
            }

        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate Called");
        if (getArguments() != null) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mRecieved = getArguments().getParcelable("place");
        } else {
            mRecieved = getActivity().getIntent().getParcelableExtra("place");
        }
    }


    public static void sendUpdateIntent(Context context)
    {
        Intent i = new Intent(context, WidgetProvider.class);
        i.setAction(WidgetProvider.DATABASE_CHANGED);
        context.sendBroadcast(i);
    }
}
