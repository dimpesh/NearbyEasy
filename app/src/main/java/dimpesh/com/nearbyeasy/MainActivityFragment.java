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
/*
        String fakeLtd = "26.2807";
        String fakeLng = "73.0272";
*/


 /*       new SearchTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + fakeLtd + "," + fakeLng + "&radius=" + "15000" + "&types=" + "restaurent" + "&name=&sensor=false&key=" + BuildConfig.MyGoogleMapKey);
*/
        new SearchTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ltd+","+lng+"&radius=" + rangeStr + "&types=" + categoryStr + "&name=&sensor=false&key="+ BuildConfig.MyGoogleMapKey);

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
