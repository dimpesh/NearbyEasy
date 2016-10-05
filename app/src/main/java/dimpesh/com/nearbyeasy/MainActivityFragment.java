package dimpesh.com.nearbyeasy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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
import java.util.ArrayList;
import java.util.List;

import dimpesh.com.nearbyeasy.Data.PlaceContract;
import dimpesh.com.nearbyeasy.adapter.MyListAdapter;
import dimpesh.com.nearbyeasy.adapter.PlaceCursorAdapter;

import static android.content.Context.MODE_WORLD_WRITEABLE;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, LoaderManager.LoaderCallbacks<Cursor> {

    //  Shared Preferences Object Data...
    SharedPreferences pref;
    SharedPreferences.Editor editorPref;
    ArrayList<String> menuArr = new ArrayList<String>();
    View empty;

    public static final String TAG = "MainActivityFragment";
    ListView lv;
    ProgressBar pg;
    MyObject[] mobj = new MyObject[]{};
    String rangeStr;
    String categoryStr;
    MyListAdapter mAdapter;

    // for location...
    double lng;
    double ltd;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
/*
    private double currentLatitude;
    private double currentLongitude;
*/


    /// Tablet UI Mode Design
    private static final String STATE_ACTIVATED_POSITION = "activated_position";


    // For Content Provider and Loader
    // Declaring MyCursorAdapter variable and using it in programmin..
    public PlaceCursorAdapter placeCursorAdapter;

    //    public MovieCursorAdapter movieCursorAdapter;
    // CursorLoader Implementation Step 1, create Loader ID
    private static final int PLACE_LOADER = 0;
    // Adding String [] columns.
    private static final String[] PLACE_COLUMNS =
            {
                    PlaceContract.PlaceEntry.COLUMN_ID,
                    PlaceContract.PlaceEntry.COLUMN_PLACEID,
                    PlaceContract.PlaceEntry.COLUMN_NAME,
                    PlaceContract.PlaceEntry.COLUMN_VICINITY,
                    PlaceContract.PlaceEntry.COLUMN_ICON,
            };

    // Defining Colummn Indices....
    public static final int COL_ID = 0;
    public static final int COL_PLACEID = 1;
    public static final int COL_NAME = 2;
    public static final int COL_VICINITY = 3;
    public static final int COL_ICON = 4;
    // UPTIL HERE


    MyObject mClicked;

    public boolean favMenuSelected = false;
    List<MyObject> listMyObject;


    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader------------Called Successfully-----------------");

        String url = "content://dimpesh.com.nearbyeasy.app/place";

        Uri fetchUri = Uri.parse(url);
        return new CursorLoader(getActivity(), fetchUri, PLACE_COLUMNS, null, null, null);
    }

    // onLoadFinished is called when the Data is Ready...
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        Log.v(TAG, "onLoadFinished----------Called Successfully-----------------");
        // Check If This Works...
/*
        if (c.moveToFirst()) {
            do {
                {
                    String result = "S. NUMBER           : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry._ID))
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
*/
        if (placeCursorAdapter == null)
            placeCursorAdapter = new PlaceCursorAdapter(getContext(), c, PLACE_LOADER);
        placeCursorAdapter.swapCursor(c);
        Log.v("Favourite VERBOSE", String.valueOf(favMenuSelected));

        if (favMenuSelected)
            lv.setAdapter(placeCursorAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG, "onLoaderReset----------------Called Successfully-----------------");

        placeCursorAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(MyObject m);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(MyObject m) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */


    public MainActivityFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //   super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_main, menu);
        MenuItem item = menu.findItem(R.id.action_category);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        initMenuArray();

        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuArr)); // set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                    editorPref = pref.edit();
                    editorPref.putString("category", selectedText.getText().toString());
                    editorPref.commit();
                    categoryStr = pref.getString("category", "atm");
                    favMenuSelected = false;
                    new SearchTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + ltd + "," + lng + "&radius=" + rangeStr + "&types=" + categoryStr + "&name=&sensor=false&key=" + BuildConfig.MyGoogleMapKey);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_range) {
            favMenuSelected = false;
//            Toast.makeText(getActivity(),"Menu Clicked",Toast.LENGTH_SHORT).show();
            chooseRange();
            return true;
        }

        if (id == R.id.action_favorite) {
            favMenuSelected = true;
            getLoaderManager().restartLoader(PLACE_LOADER, null, this);
//            viewFavorite();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("settings", MODE_WORLD_WRITEABLE);


        lv = (ListView) view.findViewById(R.id.ma_list);
        empty = view.findViewById(R.id.empty);
        lv.setEmptyView(empty);

        pg = (ProgressBar) view.findViewById(R.id.ma_pg);    //26.23456,72.9098

        rangeStr = pref.getString("range", "2000");
        categoryStr = pref.getString("category", "atm");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000);
/*
        new SearchTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ltd+","+lng+"&radius=" + rangeStr + "&types=" + categoryStr + "&name=&sensor=false&key="++ BuildConfig.MyGoogleMapKey);
*/
        String fakeLtd = "26.2807";
        String fakeLng = "73.0272";


        new SearchTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + fakeLtd + "," + fakeLng + "&radius=" + "15000" + "&types=" + "restaurent" + "&name=&sensor=false&key=" + BuildConfig.MyGoogleMapKey);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(),position+"",Toast.LENGTH_SHORT).show();
//                String key = mobj[position].getId();
/*
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, key).putExtra("name", mobj[position].getName());
                startActivity(intent);
*/
                if (favMenuSelected == false) {
                    Log.v(TAG,"online Movie Clicked");
                    mCallbacks.onItemSelected(mobj[position]);

                }
                else
                {
                    Cursor cursor= (Cursor) parent.getItemAtPosition(position);
                    MyObject mo=new MyObject();
                    Log.v(TAG,"offline Movie Clicked.");

                    mo.setName(cursor.getString(cursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_NAME)));
                    mo.setId(cursor.getString(cursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACEID)));
                    mo.setVicinity(cursor.getString(cursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_VICINITY)));
                    mo.setIcon(cursor.getString(cursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ICON)));


                    mCallbacks.onItemSelected(mo);
                }
            }
        });
        setHasOptionsMenu(true);

        // Database saved details shown Here...

        // CursorAdapter work...
        String url = "content://dimpesh.com.nearbyeasy.app/place";

        Uri fetchUri = Uri.parse(url);
        Cursor c = getContext().getContentResolver().query(fetchUri, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                {
                    String result = "S. NUMBER           : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ID))
                            + "\nPHONE             : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PHONE))
                            + "\nNAME          : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_NAME))
                            + "\nVICINITY       : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_VICINITY))
                            + "\nOPEN   : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_OPEN))
                            + "\nPHOTOREF           : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PHOTOREF))
                            + "\nADDRESS         : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ADDRESS))
                            + "\nPLACEID       : " + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACEID)).toString();

                    Log.v("RESULT_QUERY VERBOSE", result);
                }
            } while (c.moveToNext());
        }
// Uptil Here...

        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        ltd = location.getLatitude();
        lng = location.getLongitude();

        Log.v(TAG, ltd + "/" + lng);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

        } else {
            //If everything went fine lets get latitude and longitude
            ltd = location.getLatitude();
            lng = location.getLongitude();
            Log.v(TAG, ltd + "/" + lng);
//            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
 /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    public class SearchTask extends AsyncTask<String, Void, MyObject[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute Called");
            pg.setVisibility(View.VISIBLE);
        }

        @Override
        protected MyObject[] doInBackground(String... arg0) {
            Log.v(TAG, "doInBackground Called");

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

                // Demo Response
                response = "{\n" +
                        "   \"html_attributions\" : [],\n" +
                        "   \"next_page_token\" : \"CoQC_wAAAK1Ju5DS3mzD-HvHL4i374JYK6xj5Km1Flgcj952L59zu0QnDxBa4nZCPNG98z0b_mxvJSCVJzAzcFJdTm3K9B15GCHcSNsDQ2Lyt3LC4FYYdWV3DzCeiYJq14E1nyYmX7tfQypgRG5VYzwQnK_LFXDD-FjsSfsY2_IYrTra3M2mivYFn1BcJBqw25jv8ZOyxkYz2Th3q8RIe_BvQFtFWpDq39Qmyx9cgp0Q66nwFlWgYHf4Nb4SEWbqP7gITY8T-Xs2I4J102QJ-0yTWYpAI-Glnc3JAGC6YfVkwP46fATMB94zYDg6lNIZOw-pofEFuDaYv39nagEckd2TdJRK0TkSEPAOSvNCYJchaScvQ5UmyGAaFLL5gMAEQAUzqU0AHT_-dWAt1gvk\",\n" +
                        "   \"results\" : [\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.23894689999999,\n" +
                        "               \"lng\" : 73.02430939999999\n" +
                        "            },\n" +
                        "            \"viewport\" : {\n" +
                        "               \"northeast\" : {\n" +
                        "                  \"lat\" : 26.3572277,\n" +
                        "                  \"lng\" : 73.1368445\n" +
                        "               },\n" +
                        "               \"southwest\" : {\n" +
                        "                  \"lat\" : 26.1834777,\n" +
                        "                  \"lng\" : 72.9242419\n" +
                        "               }\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png\",\n" +
                        "         \"id\" : \"a054b6f3b8168d78d4e70c02517a455ad4659728\",\n" +
                        "         \"name\" : \"Jodhpur\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 1366,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/100990859613278056647/photos\\\"\\u003eNicola e Pina Rajasthan\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAAHsBfk70iJ3oJBC2rE9jmaYhU12WlsrQBtk8m9jrKoYo31yvSzsFs4aJ023gZtGY7ZF-2AzVlJxf_esYiC8R3bhOX4GeOkBWiss2DkMiVuLxnS2KkLoWWkAB8weQPnXr27WnMLzfDkTcQZSF1IadupnLW7SPX3st6Atu_pKUWbTEhAvz36XTyu1z8Lu7vy_AkdYGhT3EoiCA6NXmUyjpgkIksEHxH_mfQ\",\n" +
                        "               \"width\" : 2048\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJucwGqk6MQTkRuKvhClvqFIE\",\n" +
                        "         \"reference\" : \"CoQBfQAAAOOfNCcuPSD0uOEn2utRtu451i9TmoofJ-V5LAx9zDMItspSnbtF6JA2rbNdjHmHUh7I0Gp671rS1n2kv1ZA4N2q-HiQPQDVapQ3i2ZAwDUyVB8cro2Z7j2F7Q6Vj5X5N0d_8EIR75Q1BbQSe51Y-qhDKgUXRUbZVhX5HYPo4oKVEhAPo5_zRcsBVP4lfj0d3acMGhSW16_gsL88V8xZ82JGFObdXIpaWw\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"locality\", \"political\" ],\n" +
                        "         \"vicinity\" : \"Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2785808,\n" +
                        "               \"lng\" : 73.00203669999999\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"14ef63e8e0b780daa908297df9dd97f333ffdd09\",\n" +
                        "         \"name\" : \"The Kothi Heritage\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 4608,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115320609171788920171/photos\\\"\\u003eaniket washimkar\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBdwAAABz6AxFUcEULvr0vpKt1mnoB4cvwYWWjfQsx8N3N2xzGgFD7tROyrMX3PGt7nDD5ok2YX0olYVuBRHFX7QTLtyFIVw3lDbUodGGXicDZF20V8kortozfYkpm4MmCQUpFlIABVNMAikCJQTXIsoWS8zLXcFn6Nn51KyN9_DVWrIkKEhBkv5Ncbn3Z445uuAasSjgqGhS8ID4XdOzi76W2xLNjnsMhyUmzBg\",\n" +
                        "               \"width\" : 3456\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJV1OPhi6MQTkRWyA-wy0ayFU\",\n" +
                        "         \"rating\" : 4.5,\n" +
                        "         \"reference\" : \"CnRlAAAAgoD9WIJUbJa0NzcndcnSBlFOFHFEf8ZE2pxC0WZWvaB0sw1D8cZP_Bf0isnZX0v8kfdUPNvtWRa2mvnXiAP3FCradhACOOVAeY_8w-T1hfaONweqCo2yjY3VXe4nqA3m6U4NqcTeWDiOsrMZGJh59BIQJIAF2zUsobzu56v0eQ3IyxoUsDwaI_nIVIHIKbbOoBckeM8nh4E\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Bachraj Ji Ka Bagh, 9th Chopasni Road, Behind HDFC Bank, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2810589,\n" +
                        "               \"lng\" : 73.0476932\n" +
                        "            },\n" +
                        "            \"viewport\" : {\n" +
                        "               \"northeast\" : {\n" +
                        "                  \"lat\" : 26.28154354999999,\n" +
                        "                  \"lng\" : 73.04972315000001\n" +
                        "               },\n" +
                        "               \"southwest\" : {\n" +
                        "                  \"lat\" : 26.28089735,\n" +
                        "                  \"lng\" : 73.04701654999998\n" +
                        "               }\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"60e59d82da79e67927733bd13ad041a0e9575d52\",\n" +
                        "         \"name\" : \"Umaid Bhawan Palace\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 3456,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/100243165038954689412/photos\\\"\\u003eSlawek Prochocki\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAMmGNOMUEoVlTzu31ZueSK47CHMsYCJTeMJOvTbiwA_FLktyP_iw5LuWi-bnUS8CKa0Esr7iNlaCIVU6P0LSjRQ0rBccpOeZUjePImDw-U0axxLZ67RxnroGJjb5U_I-NGuYA-ySMLsA1tflzARkFzhH_maCtShHyyeKPViKhQx7EhBozyK9YGimxjxDWgg0EVH3GhRwqeRrfRt-CBYh74pUsVPpFT__Lg\",\n" +
                        "               \"width\" : 5184\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJ97NXLfqMQTkRcb6AeFAk0Mw\",\n" +
                        "         \"rating\" : 4.6,\n" +
                        "         \"reference\" : \"CnRnAAAAcHtTN7zNQ9dsdZ8gBTZsEnO3an5gL6ye0kOSTCWlK26VLi3Rot_XWFMeo_KfM6HsQcOpnSh9ADmyGOIKDSkZCyivMU_HYWTDmJwo98wgA0tVIZHxFgzNVroI0dJrDCmLmzQ2CoGXT2wywvZLnloUBRIQ00iLZ99cDG0tTnZZjopUORoUBNhssg4ADJSM2sgVYt7T8GnLzCs\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Circuit House Rd, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.273491,\n" +
                        "               \"lng\" : 73.0299227\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"b9cf871162ff284e9d8cf441de50bb4bf2bce283\",\n" +
                        "         \"name\" : \"Ratan Vilas\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 3120,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/116732441645462977927/photos\\\"\\u003eNaimish Adesara\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBdwAAAO67Z1A-7_eqqk7w-0lul9sFVM7Wvoyoh5A1_qJXk-5k3U_j_O1ZYOCBj9lM0h7IXuPvygGwkXPk5zPbbB0fBLSWoWcz0aKIh-XbfZ0uKfhas2d8mgXaqvgWdZU7g-9Xq-kwqzc-z-ua1W0pmjY4oBixkqHquPqWm-Ah7_ALpxPIEhDIuTOHJT9TgB1RUv_gcJVKGhTRgCdaXdf3e4Vnyi7ggjjyzEHmeg\",\n" +
                        "               \"width\" : 4160\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJucwGqk6MQTkRxnh_4FGmvTo\",\n" +
                        "         \"rating\" : 4.4,\n" +
                        "         \"reference\" : \"CmReAAAAvpGjXQJudPhSzeOxjoWgAyydCF_JTLUH3EqCw8EjtqNR2nP0-LXADd9fJ8PgBwiJyR5X_gwAwAzBD82Ajr4YkdG0_3oeaM4IpuLJHAuHui0gflA0Hwb3dj5rj_lEZc-vEhBK1U1F11-bPS5fv8hqRQioGhTlzMN6hvqHWGcqz7Q6Okur1UgsDw\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Near Bhaskar Circle, Loco shed Road, Ratanada, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2672281,\n" +
                        "               \"lng\" : 73.01949639999999\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"eb340436a466a7d2f5490a693ae8e53c1ea85cd0\",\n" +
                        "         \"name\" : \"Vivanta by Taj - Hari Mahal\",\n" +
                        "         \"opening_hours\" : {\n" +
                        "            \"open_now\" : true,\n" +
                        "            \"weekday_text\" : []\n" +
                        "         },\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 2300,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117100967911950113494/photos\\\"\\u003eHey Traveler\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBdwAAANl7f3KtdQMJ2zahip34BUyKdTxfvwlmWmEgxDzA7wMGWpQdJoLZ4BLNPJl-avpBL_KrWecexQ-krH8uaumSkLCKpAe5Qz4TB_L-5IbmC3VzlR9NPDLfMy15VBjPe_CczJq_1b2XGRXoo_vGXoYiXHwemBwyH6v-A2bScKfSzEugEhBlGfeYybMbfwob5KDMx7nMGhSWTGEO1cpciBoTQHk4Ce1z3H2FSA\",\n" +
                        "               \"width\" : 4096\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJYXFz-0CMQTkRD9pBz5_MUa4\",\n" +
                        "         \"rating\" : 4.5,\n" +
                        "         \"reference\" : \"CnRvAAAAmU_Z-eHWygIN0MLUgO3Ii0r-hSiVsKrsIUeS04e1tkRUh2WGCCTykC24XIN0k9KVsvwXCuF67jDb-oY7LUy6TtEWFQjVAf7rjUPGQJf_8E6Yjus_mscF9gBkKy98W9FFkSB78A35Et3sP2IxqsvkRhIQFn1qnEygMgqs9aKtxjrs4xoUQQOSVOlvjGk9cmZ3eL_FV-lPreI\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"5, Residency Road, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2834348,\n" +
                        "               \"lng\" : 73.0383167\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"3d643eceee591030959f3aab5c7492c04e24712d\",\n" +
                        "         \"name\" : \"Ranbanka Palace Hotel Jodhpur\",\n" +
                        "         \"opening_hours\" : {\n" +
                        "            \"open_now\" : true,\n" +
                        "            \"weekday_text\" : []\n" +
                        "         },\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 458,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/112868889012225054065/photos\\\"\\u003eRanbanka Palace Hotel Jodhpur\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAJyHsvKueWU3cYaE8kp9rB0j41HMF_zXCfL_W_TKx7e_tGTHkrPE8gvv16tcccOBmGz_K5nRtirZ3wh7NNcQOEg6WWEKt1FKDshtrkNDeKvgR7I1GauS3sqnatpmitsvhs2Y-rTR_6cWiRfNMzJiT5knVkn0OkePz4mbxjHXpjVDEhBNIajAHe4_mwr_rV4mS_XjGhQFiHuwiYqvTdPEofugP01lsiD6Jw\",\n" +
                        "               \"width\" : 458\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJ1_p1nF-MQTkR6c2s-HieTiw\",\n" +
                        "         \"rating\" : 4.3,\n" +
                        "         \"reference\" : \"CnRwAAAAE4jtnqEc5HwhHmOK5wXYVEnjpmSJsLGAMiIMzEe7xZTi2ITnM-IS7k6hUYLgpRUJiGIMT9POgmlLr59QzdiKdgplYb-cgo88do6gCPrwLL9x67Sf2PkecnAZ9S6FszAbNS2LC-T_VIOoOQWbiaBjcRIQtbvP86m9lESnOnoEzTe_VhoUO4a6FckU9k_1rdYDr46r48rKvek\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Ranbanka Palace, Circuit House Road, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2974304,\n" +
                        "               \"lng\" : 73.0215017\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"06756ac6d58a55192b0fcb0f5f580e6f64763b3b\",\n" +
                        "         \"name\" : \"Krishna Prakash Heritage Haveli\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 2560,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/111375754745737815882/photos\\\"\\u003eSujoy Dattta\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBdwAAAOtgwdEl4tvUdikytusRkmsQ0JMAHaSRbPOILkYIukX2DHmdabMDP9qLdllTmtyXBy6v3qHHgNo8i2oSzjjFkSY9jALNOkF7ysK4LUQJx9bNNasB__hKoc834OMng7n_oHDWQulyY8co42BUG9vv5YEUENJpQfIl6Do2zsPFDBBAEhBymwFw2DKi71uNNA-k7PDMGhTl1F_SJS9txqTx5vqgp3S7wyBGMg\",\n" +
                        "               \"width\" : 1920\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJFYZSb7eNQTkREhe9INduaYw\",\n" +
                        "         \"rating\" : 4,\n" +
                        "         \"reference\" : \"CoQBcwAAAH6MfCvzmf5nQuCDbJBl2XrEGjAZtwSLDnVNynZsDyiG4feRINbtos0bx6kvLLLLjWKFF6CdiD0bW0hEZ-wiUSgVgUbUE5herVWguWJ5KwptslZ3_GCtLYx4XEElOUHAYISX2ZzNxxH2jGWhSF27lCMjyZM5Saj8krf3s0lkMIACEhDSxmiv_pu4lmLkAalyEZ7IGhRhmFFLXi8lHpNSGyVAgR33nYOfyw\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Nayabas, Killikhana, Near Makrana Mohallah, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2694236,\n" +
                        "               \"lng\" : 73.0688346\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"94f9ce3333af5845c14a2c0ad9b6488db06777b8\",\n" +
                        "         \"name\" : \"Indana Palace Jodhpur\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 817,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/114421876140081234841/photos\\\"\\u003eIndana Palace Jodhpur\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAGUkqcVQHC5dIHWV0X7lXGbKpA39ogjRlTBz139mBRasXycTtFq-GDXmmRsrcRtEutiZGA_EFhL8wv975ELIMxmpVmuYXL1ENAGJi8MOgFAAgp2-xRDEk3mnribwNs4MAAwRIz4RtI2OgQ_i90ZfoFghGSs2yiEMzPIfM_xVoyLjEhD0X_7dGjs9vTUwa7hJlhpYGhQaFcgCsuWAVwSSR9_z3gTxtUrRWg\",\n" +
                        "               \"width\" : 1470\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJAzZ4Z7-MQTkRALj4h7Wwhmw\",\n" +
                        "         \"rating\" : 4.1,\n" +
                        "         \"reference\" : \"CnRoAAAAX0YNuwmrIfDU655s6UEfhH2QdZMXyMcHh_S_YcGrh7M-oJ-soae8R8sJvOplp_bWL794_RTsDzotfmYPP0aOSiD_2FqVANlTegt03yXg1MkclomntOaC1Boa7Cd8yHfVXXAdgMbOw7thSGYy7rduShIQ2ml4Y8bz_EgBPwAlnl1OLBoU98U7uAqdByNNj_KxXydh62PynJw\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Opposite Military Officer Quarters, Benayakiya Road, Shikargarh, Pabupura, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.321315,\n" +
                        "               \"lng\" : 73.118178\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"9c8f77a8c3dbf78fc365b594f3ef1163649cc38d\",\n" +
                        "         \"name\" : \"The Ummed Jodhpur Hotel\",\n" +
                        "         \"opening_hours\" : {\n" +
                        "            \"open_now\" : true,\n" +
                        "            \"weekday_text\" : []\n" +
                        "         },\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 2448,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/103629142400651529143/photos\\\"\\u003eravishankar km\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBdwAAAF2nO9gHFlFLZ_cH3sUnmNKoYqWo9wV72Ldq07fEc8lQRi4TlgQneDD_EZ9EMfQQISAM2I3sEnCSD4tuNFcogioT-wLaGGarTrvhQ9YZZNNBZs3l3ch3VTBYm0j-zYBZHqUKYtJWkp1V1MMgc84PcW675EcQ1Rr4yBL4wiPoYM57EhBgjzgn8v7N6AcNBBmcA6sPGhRBjE7vlenK3bNAOCQuPNS4VknM2g\",\n" +
                        "               \"width\" : 3264\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJVVcSOEmMQTkRbsLkKFQQfkI\",\n" +
                        "         \"rating\" : 4.2,\n" +
                        "         \"reference\" : \"CnRqAAAAdlf_Ojj3NST_JI1LlN9s-fe8VUWWiEW32xFfcF_PCS08MguBF8yQD_37fLv-R2xCQsyRDVCag9FeGymmsGMe5cJVwXwZrPYtWt4QEqKVpxysnCQjm3kQPcYLqam5C0bocvVxQsK4z4ZxaRHVPDXLvRIQYbmGO5sitsIZHrwGBSrrwhoUFmRwupWLPDDASn1pdCRDzVGV6iE\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Jaipur Highway, Banar Road, Prem Nagar, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2684362,\n" +
                        "               \"lng\" : 73.03832109999999\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"f75504fedcb31c7606c5cfa7aced4d8117a81d21\",\n" +
                        "         \"name\" : \"Park Plaza Jodhpur\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 480,\n" +
                        "               \"html_attributions\" : [ \"From a Google User\" ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAMErDzIpQWosvLPCmzDKmaE0CITWhJ4OwOx_Ps81b-PfmRVaKBZb1dNFdcvrzGjR_qOs_B665gPZ1ZnmIIeC8hOO7FmoJojmfZl8cy-vi1SfRF0aalpccsCrJV5foKkbFXYfI1WLrLN5joutMd2uS1M9ovfi7lqoxyZvZIrMN4-rEhAN7Qr8txedPHV3vNjHk9bhGhTdgHitt8EBpYWJXnaBGcpypYpj0Q\",\n" +
                        "               \"width\" : 640\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJnaYQI2CMQTkRndxMVSnz1bA\",\n" +
                        "         \"rating\" : 4,\n" +
                        "         \"reference\" : \"CnRmAAAApXQ-XizQxipA0oAQz0ZgK9C1xwdK9cdGgnPhq2cwUv5wsim64S8LZ8Ex9OJ_6oWs5sdyBWmpxvz2vNpu0RMYSQ8Jpg3m0ys7QyGY4WRYC-tuAEpvQaQIu8w43nyp_WmW_ZHYHE_nOP4C-ZOVA_Tj-RIQnm7JNyOEW9MHex8Zg8tknxoUmugIzkhNceCGiJ86HyO9C9fKSJw\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Jhalamand House, Near Panch Batti Circle, Airport Road, Air Force Area, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.27076,\n" +
                        "               \"lng\" : 73.03017799999999\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"6ece52233ca8f280116296988b8e80271902a5bc\",\n" +
                        "         \"name\" : \"Hotel Residency Palace\",\n" +
                        "         \"opening_hours\" : {\n" +
                        "            \"open_now\" : true,\n" +
                        "            \"weekday_text\" : []\n" +
                        "         },\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 1633,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/100009305409121349554/photos\\\"\\u003eHotel Residency Palace\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBdwAAANZ_kjNf-ULhl2rPrj5xBkOGI3fNOn4LpBRSk77TyZfC7JH6Zsu6CNa5UhovLJqAxSriXo_l5PSXk-IgobfHw2itvxLdLXidZv5aP5RcHAWA7hcpbrBK3uRzyWXCeZIoplVg-Az_9WC3wHa-LJ8o7IWXUR3jWKLAw53SmpfJHx_JEhD8-N8AKlKSMLVVG0cwLT4mGhRwWlUUTTcKjtVa6UpbdsMiUrJPVA\",\n" +
                        "               \"width\" : 1636\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJb-HGEJy9VzkR6X5_MCG1y4s\",\n" +
                        "         \"rating\" : 3.9,\n" +
                        "         \"reference\" : \"CnRqAAAAhSB4KlL0szm8QYxwN7Wmp7rgYXHYKnircQFedcuCSWDEQU3Ec2-WoSPC8zrclnnJHoABXqFpktNk-lccXZVw8TenePbJ3DFt3lvkdaIgNoOjdq_A0hHUb07M2o8ZFrH5SYV7ii4iAF2ONZTwlUu74hIQPRI4CeNCltAgnBJM6_0cWhoUqCDJ00h0yw3cdqDbxzQf79dXqoQ\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [\n" +
                        "            \"bar\",\n" +
                        "            \"lodging\",\n" +
                        "            \"restaurant\",\n" +
                        "            \"food\",\n" +
                        "            \"point_of_interest\",\n" +
                        "            \"establishment\"\n" +
                        "         ],\n" +
                        "         \"vicinity\" : \"Ratananda, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.297033,\n" +
                        "               \"lng\" : 73.0261541\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"02b07949bf612e350b5683a9baa2dd445306852c\",\n" +
                        "         \"name\" : \"Jee Ri Haveli\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 3120,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/100960740052443267338/photos\\\"\\u003eVirag Parekh\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAGQInZCpGUlriIZAHT-VxP4hEbNLHAEoKDOHlieSBCoc1jTd-IroI6P66rDYHmCFPHFvLRoer3_2eNFUYeCXJxxUwKDJIUb-mdwb8cLqgasz6kSUCBPdDTX8jQIJFkeWh7jiOW7UdDBAGtFXvSt6KbPf1Bs48duNBnFWtVonFSR_EhA0Z4QPZLjuVCazK60hZmVAGhSB64miZ2DVk3Hc18-yjkz4gUmksw\",\n" +
                        "               \"width\" : 4160\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJp100WLCNQTkRGcNyRbi52-Y\",\n" +
                        "         \"rating\" : 3.8,\n" +
                        "         \"reference\" : \"CnRhAAAA_-9kZgQEYQ07os1DaxtFl_mW60LI_wh0qHW5rj_VzqLdgbuGp3ihKfNFi1w2xOt1Afkhc964jUhNddW56yyUGJY_2PvHEnm_Vrd9XhZsZllWuQFC2x8ez6oFKp-bSMzJmamGlmT8mxUebSP5Qzph0hIQgrT2u2fbFJZvPsdVn4idShoUITCZjIhmRhouWYwL9mvf0c0N3hM\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Near Rajmahal Sr. Higher Secondary School, Gulab Sagar, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.266431,\n" +
                        "               \"lng\" : 73.038535\n" +
                        "            },\n" +
                        "            \"viewport\" : {\n" +
                        "               \"northeast\" : {\n" +
                        "                  \"lat\" : 26.26649845,\n" +
                        "                  \"lng\" : 73.0389847\n" +
                        "               },\n" +
                        "               \"southwest\" : {\n" +
                        "                  \"lat\" : 26.26622865,\n" +
                        "                  \"lng\" : 73.03838509999999\n" +
                        "               }\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"51bb49b35aa5620a1bf127e8957e3391fb97ecb6\",\n" +
                        "         \"name\" : \"Chandra Inn\",\n" +
                        "         \"opening_hours\" : {\n" +
                        "            \"open_now\" : true,\n" +
                        "            \"weekday_text\" : []\n" +
                        "         },\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 768,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/114250744199602899373/photos\\\"\\u003eChandra Inn\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAFPCtBAmaAvAqv4az_yIr3K1wSO0a-jRkKiCE7seAmu0zvinVzE6tT_jhTOJKCZdCpG6Vh6lMszAasQFtUrnnGgEzv-BlvGfx4rzzXT-R_jPTc1apFbud2dBMhW4g6Kd0dtyikTnYSYR-d5IISPuhYD97ODZShniZXLlc9vmOLAlEhDMVZdLhGbmViHvkIMOuF_OGhTJtbCS9T7rnrw7iMV-dxHPwMhIDg\",\n" +
                        "               \"width\" : 1023\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJk5jx2F-MQTkRlSw70pvVAFs\",\n" +
                        "         \"rating\" : 3.7,\n" +
                        "         \"reference\" : \"CmReAAAAf1VXl7SWL3-dkbvoDeZmDGEv3oPg9V_BKY4bq-BPXwCnKG3YT1UMuc4jSDmdq4_D-VsQJh0D_aGjDDP2vbr3_9G1kPa_ejGyU-HGoOsnamd8ruf5BgIaZPO5oduhmtV1EhAdGeJvH6gP5cNpOejoEL3QGhTTq5NT_5EZldMtKCntTVZBFrfkyQ\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Airport Rd, Air Force Area, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2951,\n" +
                        "               \"lng\" : 73.024\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/civic_building-71.png\",\n" +
                        "         \"id\" : \"574515af9be4c4e1dbb33bbc1bd89fe030c947fc\",\n" +
                        "         \"name\" : \"Ghanta Ghar Nagar Nigam Office\",\n" +
                        "         \"place_id\" : \"ChIJ1TxmPLGNQTkROeKuLuVXkl0\",\n" +
                        "         \"reference\" : \"CoQBcQAAAHRmi9wcqSOVk8FUxGsKYWwDi5PooXNt3qNXkMduzPxwy3yVbewNLrR0Xl237F7GKdeo9keszmyybVNftkWYkFpYZvh91zUUl0ZkDI2n7VtlJgsT7JzZk_kPqlnO1UEXIVdo355XG2UlWlHE153tkMe7QuHORNnDhR0W7YXtvdf-EhBcJDW_iGc8VauTfMkls9TvGhQ1owlkj_IIfGkAmg2COAx68BsRhg\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"local_government_office\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Ghantaghar Market, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2950519,\n" +
                        "               \"lng\" : 73.0240369\n" +
                        "            },\n" +
                        "            \"viewport\" : {\n" +
                        "               \"northeast\" : {\n" +
                        "                  \"lat\" : 26.2950826,\n" +
                        "                  \"lng\" : 73.02425005000002\n" +
                        "               },\n" +
                        "               \"southwest\" : {\n" +
                        "                  \"lat\" : 26.2949598,\n" +
                        "                  \"lng\" : 73.02396585\n" +
                        "               }\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                        "         \"id\" : \"3f5197728b8231c7739b642719c8a1e37a9d5a89\",\n" +
                        "         \"name\" : \"Ghantaghar Clock\",\n" +
                        "         \"opening_hours\" : {\n" +
                        "            \"open_now\" : false,\n" +
                        "            \"weekday_text\" : []\n" +
                        "         },\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 2818,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/110700232064731602555/photos\\\"\\u003erajesh memdani\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAFhP9CmxpolFr0NH0nrpe8EQ3s78noNqos7PG1iK60h4YY49zjInIEpSNHeoU_WAyrP7U-wK3jb_PCz6aDuB1xqjKyhB-JO5Sd_YXh11pVr9dAUYwomkPP0FMHai4VMLKiBEMLUSwlZXwHQ3iJJZEILwT9P1x7J1vL_m3_3mvHj_EhCJ75I9BqaoDX1jN77XRAFrGhTjA95HzH4W-ZxZVjKn-hvEjEMiMQ\",\n" +
                        "               \"width\" : 5010\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJZUg_PLGNQTkRqynk7QYaZwk\",\n" +
                        "         \"rating\" : 4.5,\n" +
                        "         \"reference\" : \"CnRjAAAA6bPBiu4t12l4Ux3VxPtmFdG_WZH-j04EPqTCPee7kL92adB0S4VqriB1TUADS3OXnBFCqKb23LKt4KF6gHDi4LbqMxPr2nNCci5OTLukszIAblT484Mi2nUXrC12Kc2mJHRi4QGX95wRsKX0JgfFphIQ-EFPVC87SxvHgHw6yObrxRoUBX-uLcLsxSTpKjqOipo-My3jNRM\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Nai Sarak, Ghantaghar Market, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.3005245,\n" +
                        "               \"lng\" : 73.03908020000001\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"01601640c73ce369f5431e5eeaa0e56d3ebbf126\",\n" +
                        "         \"name\" : \"Hotel Marvel Umed\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 800,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/108052905440985068443/photos\\\"\\u003eMukul sankhala\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAACtopGHLuIbtABxXzUxuSu54yLMR9g-nxyxl1hH19Q8KPjB-E-6wI_2csP3SwZMEOaeHDdi7gExpp76sbQNfJY9unhO2XWddMQIgcXuwF0SNHXmXfmewPp4Zy0l-XVomXjCo7p_d5QJPzeCY00Lty3cg88iv4PcBW3_lQoNTeJkfEhCI9bJIq4LAfD6pQ6x7c0dxGhS2x2AAKSM1nt3sjkKsxYTFWa3ueA\",\n" +
                        "               \"width\" : 480\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJnXKcTqGNQTkR3iwo1TINpd0\",\n" +
                        "         \"rating\" : 4.1,\n" +
                        "         \"reference\" : \"CnRlAAAAYx3R2kU_U7G68usoRZZQ2Pld7w1gc0y_RbX1ty0xlfIHNZNbdPLEDU7l50Fm-T84WZqX1DS2kWKxWI22ms9ewlaLAPTpYPA6khC2o51Vza2WpdKAynJaOiTQ9NjU4IUNDChzJksqlXp4vr53d6z6URIQ3N9M8k0p9oZ0RDmwoZer0BoUI5sO0gM6ZCrVPn3Xr4dm05ikjyg\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Nimhera House, Mandore Road, Opposite Khetsinghji's Bunglow, Paota, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.296675,\n" +
                        "               \"lng\" : 73.021118\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"596ca72470907dc2b51144f7cad690badde7fe9a\",\n" +
                        "         \"name\" : \"Kesar Heritage Guest house\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 2448,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117983471542592029707/photos\\\"\\u003eChristian Triedes\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBdwAAAL1NK1lA_TWb7TWLuKKMk3mUf_Zfes9LmCllX5xVXj7gkUYlQepHEKej-ELJlZOkQnUD6ztxYVS1_T1K54mkS33MxluarUNXp4kZs9suE3w458oMFB_G_YhlRE2U9ANnl53kMNWhT5Gu7jexyDKvwDu7zldqaLdjfsgRIuGEsd7xEhBsyX2zFmLwVSizK6qAPB0jGhQGMyv6gVYiL32zfvIfg9EHtfbI4Q\",\n" +
                        "               \"width\" : 3264\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJHzyNFreNQTkRA3zWVW5-GJs\",\n" +
                        "         \"rating\" : 4,\n" +
                        "         \"reference\" : \"CnRuAAAAwo4N0EtSEV3dVcdsdvGi-mOhsKw9ru8im1rkj5XZhpL51YYb9DAOkmIvXB6lcFBdRdtjbo0tZr6PF2qZyKdnz7wvMjBUymbUuka7pT-Qk4nTGgkvyF8ydqvmsyOL_Cfo6Iq1cNOzJwbIogeEEtFJQBIQsNHHb7HoGOwaEjgsvqp6ThoU6QMVraMhSDL2gr7ISqEBz5QXn-A\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Mehron ka chowk Naya Bas, Killi-khana, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2968588,\n" +
                        "               \"lng\" : 73.0230043\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"ab60650bc4484f675bd9e49bf89b827228491146\",\n" +
                        "         \"name\" : \"Hotel Haveli\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 640,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101940229509321511381/photos\\\"\\u003eyuchen wu\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAHHPn5ndYJkxLJvbMIV4zal_xeohWUx5YNOHJ971f0xc8B-YrO976nex7Z0seTQcm6CnofRPzK-KvRVpV-MqseSXVsvgiwB1y2Nxa8uBN1W0mGRSVjz2_MJIgMQrKtC_TM7gFsg9jEGfShquQ8zfdlzc752_69fEfnFEJfVeb1-DEhD4LjW_95uUzHI5kTSS8PRAGhRGdwVunNhOvOAlDTLyCZilciT4Yg\",\n" +
                        "               \"width\" : 480\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJqf9fYbeNQTkRd0umxTtGPG8\",\n" +
                        "         \"rating\" : 3.6,\n" +
                        "         \"reference\" : \"CmRfAAAAQC-LMektAW6i64uS9tDgRYQ1rA0zOi1OpciGYleetw6L-HN6rc5utBDx1kfoX_gbyVRvfFMR2s8xa-RZQJgpWIDvgoArynr3fIgw4WiVNaK3M7U3Tnh6bbfG3i_9EvxMEhAn2bYfMB4QENOFMRaJzM6OGhRhBKKGUEO_M8tMEiPfujI0UuPF-Q\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Toor Ji Ka Jhalra, Makrana Mohhala, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.3503789,\n" +
                        "               \"lng\" : 73.0400236\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                        "         \"id\" : \"87c999b7eeacb31eb8747dc5a843574e62d2ef79\",\n" +
                        "         \"name\" : \"Mandore Guest House\",\n" +
                        "         \"opening_hours\" : {\n" +
                        "            \"open_now\" : true,\n" +
                        "            \"weekday_text\" : []\n" +
                        "         },\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 300,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/113871554358847524547/photos\\\"\\u003eMandore Guest House\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAANLyhXDbdlb4XBdj59SF0N6R4nxaDBmMh__Lu5C90niAbf3fSwblvztFW_5t_UonP9CJvouuH2pKVN2e5SPmRtIcwRn4ECMsA6aAJLGnuEfXkm8TpEjaqnZ8MpPMoqZB8YFAOUoatWYR4D-VAW1eWiTHIz6SGe3I8OM2D4mNCSYMEhDYXAgfBDmopfgaXkrv3DnWGhQypC09MhI-Pg0DOlRxdHthOA387Q\",\n" +
                        "               \"width\" : 300\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJlxhaDGKSQTkR4oO3IPs0SIU\",\n" +
                        "         \"rating\" : 4.3,\n" +
                        "         \"reference\" : \"CnRnAAAAIrpN0AtGVWrGKZtki8KCZ3_0PM76iUyO8j0P6v1yAARMqzzCWJ6HBWPUlAgKwtYaEHJF_T4DaRAZVNK_6LXF6ZkgeGbTlNFCa4oqMBQDWtLdIoxjny3UmqwOxe43aCy4yiSxZCM_cqDPoJlTNsRN9BIQE4ILtPxd59oUExSfmQI5DBoUg9G56YuuwKgHHmDkCygQAu31VwY\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                        "         \"vicinity\" : \"Dadawari Lane Near, Mandore Road, Mandore, Jodhpur\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 26.2906161,\n" +
                        "               \"lng\" : 73.0206034\n" +
                        "            },\n" +
                        "            \"viewport\" : {\n" +
                        "               \"northeast\" : {\n" +
                        "                  \"lat\" : 26.296027,\n" +
                        "                  \"lng\" : 73.025779\n" +
                        "               },\n" +
                        "               \"southwest\" : {\n" +
                        "                  \"lat\" : 26.2839579,\n" +
                        "                  \"lng\" : 73.01574389999999\n" +
                        "               }\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png\",\n" +
                        "         \"id\" : \"9f9e9a6f0c9338c9e115eebbe4fa16ed4d70fc9c\",\n" +
                        "         \"name\" : \"Rawaton Ka Bass\",\n" +
                        "         \"photos\" : [\n" +
                        "            {\n" +
                        "               \"height\" : 2818,\n" +
                        "               \"html_attributions\" : [\n" +
                        "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/110700232064731602555/photos\\\"\\u003erajesh memdani\\u003c/a\\u003e\"\n" +
                        "               ],\n" +
                        "               \"photo_reference\" : \"CoQBcwAAAFrrVwema6TkJ3l-O4Et-MX8AEuC2OFgiwBVegba7HYC_IMadTjbZ6f5bMlbAKSkgZBR5CQ6cu7YNe6TTn_v6CsCnCL7ASVFV3Hig-jPkrs_nS1jToo8eI9iF8Mdjh1GCT2oakSKbU2UAoIqV-kzyudxx7-UfMPHTMO2slNs7CLFEhBfrux5XSbTlTVFMTLT21ebGhStfnZqhOGwjdfIUvqedz9Ui-W_Nw\",\n" +
                        "               \"width\" : 5010\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"place_id\" : \"ChIJKTGXWLSNQTkRLaCV2wmnbYE\",\n" +
                        "         \"reference\" : \"CpQBjwAAAJJ4oLuqzfb9_N0Q8eEK11uKUqoBxPrF-l79sDm2YK-CanolAVn2g1t51x2ofWxP4VfH_yuqJ3hwswnpFjpO2DBW6u02JPO8eqLtTxGNSD_pxUTbqzvk92o2SRatb-njFX7vKAyw4p6d54_g-cFH6twAC10Py4KKm_rZA8h7xACaTvjxLrvFvNuLUDHRl_HfOxIQ3ii1ZHk_A8BUQYVJ2AWfaxoUpFo5P5uCDebCM83gSuJgIl3xO1c\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"sublocality_level_1\", \"sublocality\", \"political\" ],\n" +
                        "         \"vicinity\" : \"Rawaton Ka Bass\"\n" +
                        "      }\n" +
                        "   ],\n" +
                        "   \"status\" : \"OK\"\n" +
                        "}";

                JSONObject obj1 = new JSONObject(response);

                JSONArray arr1 = obj1.getJSONArray("results");
                Log.v(TAG, arr1 + "");
                mobj = new MyObject[arr1.length()];
                for (int i = 0; i < arr1.length(); i++) {
                    mobj[i] = new MyObject();
                    JSONObject obj2 = arr1.getJSONObject(i);
                    mobj[i].setName(obj2.getString("name"));
                    mobj[i].setVicinity(obj2.getString("vicinity"));
                    mobj[i].setIcon(obj2.getString("icon"));
                    mobj[i].setId(obj2.getString("place_id"));
                    //                   mobj[i].setRating(obj2.getString("rating"));
                }

                Log.v(TAG, mobj + "");
                return mobj;
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

            return null;
        }

        @Override
        protected void onPostExecute(MyObject[] result) {
            super.onPostExecute(result);
            Log.v(TAG, "onPostExecute Called");

            pg.setVisibility(View.INVISIBLE);
            if (result == null) {
                Toast.makeText(getActivity(), "Data Not Available\n Try Again Later...", Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<String> nameArr = new ArrayList<String>();
                ArrayList<String> iconArr = new ArrayList<String>();
                ArrayList<String> vicArr = new ArrayList<String>();

                for (MyObject m : result) {
                    Log.v(TAG, m.getId());
                }
                for (MyObject m : result) {
                    nameArr.add(m.getName());
                    iconArr.add(m.getIcon());
                    vicArr.add(m.getVicinity());
                }
                if (result != null) {
//    			lst.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.row,result));

//                lst.setAdapter(new MyAdapter(getApplicationContext(),result));
                    if (!favMenuSelected) {
                        mAdapter = new MyListAdapter(getActivity(), nameArr, iconArr, vicArr);
                        lv.setAdapter(mAdapter);
                    }
                    mAdapter.notifyDataSetChanged();

/*
                    lv.setAdapter(new MyListAdapter(getActivity(), nameArr, iconArr, vicArr));
*/
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_error), Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    public void chooseRange() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog = inflater.inflate(R.layout.dialog_range, null);
        dialogBuilder.setView(dialog);

        final EditText et = (EditText) dialog.findViewById(R.id.range);
        et.setText(pref.getString("range","2000"));
        dialogBuilder.setTitle("Range For Searching");
        dialogBuilder.setMessage("Enter range in Meters");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String range = et.getText().toString();

                editorPref = pref.edit();
                editorPref.putString("range", range);
                editorPref.commit();
                rangeStr = pref.getString("range", "2000");
                new SearchTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + ltd + "," + lng + "&radius=" + rangeStr + "&types=" + categoryStr + "&name=&sensor=false&key=" + BuildConfig.MyGoogleMapKey);
                Toast.makeText(getActivity(), "Range Update Successful", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void initMenuArray() {
        menuArr.add("atm");
        menuArr.add("airport");
        menuArr.add("bank");
        menuArr.add("cafe");
        menuArr.add("church");
        menuArr.add("dentist");
        menuArr.add("department_store");
        menuArr.add("doctor");
        menuArr.add("gas_station/Petrol Pump");
        menuArr.add("hindu_temple");
        menuArr.add("hospital");
        menuArr.add("library");
        menuArr.add("mosque");
        menuArr.add("movie_theater");
        menuArr.add("museum");
        menuArr.add("park");
        menuArr.add("parking");
        menuArr.add("pharmacy");
        menuArr.add("police");
        menuArr.add("restaurant");
        menuArr.add("school");
        menuArr.add("shopping_mall");
        menuArr.add("taxi_stand");
        menuArr.add("veterinary_care");
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != GridView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        Log.v("ABC : ", "setActivatedOnItemClick Executed");

        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        lv.setChoiceMode(activateOnItemClick
                ? GridView.CHOICE_MODE_SINGLE
                : GridView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {

        Log.v("ABC : ", "setActivatedPosition Executed");
        if (position == GridView.INVALID_POSITION) {
            lv.setItemChecked(mActivatedPosition, false);
        } else {
            lv.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    public void viewFavorite() {

    }

}
