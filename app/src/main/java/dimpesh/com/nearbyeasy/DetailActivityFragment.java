package dimpesh.com.nearbyeasy;

import android.content.ContentValues;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public static final String TAG = "DetailsActivityFragment";
    boolean isFavourite = false;
    ImageView iv_head;
    ProgressBar pg;
    ImageView iv_icon;
    TextView rating;
    TextView address;
    TextView phone;
    TextView vicinity;
    FloatingActionButton fab;
    String title = "Description";
    public static MyObject mRecieved;
    public PlaceObject placeObject = new PlaceObject();
    TextView titleVicinity, titleAddress;
    public Typeface Courgette;
    public Typeface BalooBhaina;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        Log.v(TAG, "Parcelable : " + mRecieved.getName());
        Log.v(TAG, "Parcelable : " + mRecieved.getId());

/*
        String str = getActivity().getIntent().getExtras().getString(Intent.EXTRA_TEXT);
        String name = getActivity().getIntent().getExtras().getString("name");
*/
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

                    Toast.makeText(getActivity(), "Added TO Favourite Successfully...", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.like);
                    isFavourite = true;
                    String result = "";
                    Cursor c = getActivity().getApplicationContext().getContentResolver().query(fetchUri, null, null, null, null);
                    if (c.moveToFirst()) {
                        do {
                            {
                                result = "S. NUMBER           : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry._ID))
                                        + "\nPLACE_ID             : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACEID))
                                        + "\nPLACE NAME          : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_NAME))
                                        + "\nPLACEE PHOTO REF       : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PHOTOREF))
                                        + "\nPLACE ICON   : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ICON))
                                        + "\nPLACE OPEN           : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_OPEN))
                                        + "\nPLACE PHONE         : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PHONE))
                                        + "\nMOVIE ADDRESS         : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ADDRESS))
                                        + "\nMOVIE VICINITY       : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_VICINITY)).toString();

                                Log.v("RESULT_QUERY VERBOSE", result);
                            }
                        } while (c.moveToNext());
                    }

                }
                else
                {
                    Toast.makeText(getActivity(),"Removed From Favorite Successfully ...",Toast.LENGTH_SHORT).show();
                    int delID=getActivity().getApplicationContext().getContentResolver().delete(fetchUri,"_placeid='"+mRecieved.getId()+"'",null);
                    fab.setImageResource(R.drawable.dislike);
                    isFavourite=false;

                }

            }
        });

        return view;
    }


    public class SearchDetailTask extends AsyncTask<String, Void, PlaceObject> {
        PlaceObject mObj = new PlaceObject();

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
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

                Log.v(TAG, obj2.getString("formatted_phone_number"));
                Log.v(TAG, obj2.getString("icon"));
                Log.v(TAG, obj2.getString("international_phone_number"));
                Log.v(TAG, obj2.getString("name"));
                Log.v(TAG, obj2.getString("vicinity"));
                Log.v(TAG, obj2.getString("website"));
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
                Log.v("String", str);
                mObj.setPhotoReference(obj3.getString("photo_reference"));
                Log.v("Photo Referemce GET : ", "Reference" + mObj.getPhotoReference());
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
                // TODO Auto-generated catch block
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
                Toast.makeText(getActivity(), "Null Data", Toast.LENGTH_SHORT).show();
            }
            try {
                Log.v("LA", result.getLatitude());
                Log.v("LO", result.getLongitude());

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
        String photoRef = result.getPhotoReference();
        String urlStr = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef + "&key=AIzaSyBPXwJ6XQDhCfQGX1QGJBsoy4z6a1rc0lw";

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


}
