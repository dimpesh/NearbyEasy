package dimpesh.com.nearbyeasy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.location.LocationListener;
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


    // Widget Intent
    public static String ACTION_DATA_UPDATED="dimpesh.com.nearbyeasy.app.ACTION_DATA_UPDATED";

    //  Shared Preferences Object Data...
    SharedPreferences pref;
    SharedPreferences.Editor editorPref;
    ArrayList<String> menuArr = new ArrayList<String>();
    View empty;

    public static final String TAG = MainActivityFragment.class.getSimpleName();
    ListView lv;
    ProgressBar pg;
    MyObject[] mobj = new MyObject[]{};
    String rangeStr;
    String categoryStr;
    private boolean mIsInForegroundMode=false;

    MyListAdapter mAdapter;
    ArrayList<MyObject> mArrList=new ArrayList<MyObject>();
    Spinner spinner;

    // for location...
    double lng;
    double ltd;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    /// Tablet UI Mode Design
    private static final String STATE_ACTIVATED_POSITION = "activated_position";


    // Variables for making URL
    String BASE_URL="https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    String APIKEY_KEY="key";
    String SENSOR_KEY="sensor";
    String SENSOR_VALUE="false";
    String NAME_KEY="name";
    String NAME_VALUE="";
    String TYPES_KEY="types";
    String RADIUS_KEY="radius";
    String LOCATION_KEY="location";

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
    int position=0;


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
//        Log.v(TAG, "onCreateLoader------------Called Successfully-----------------");

        String url = "content://dimpesh.com.nearbyeasy.app/place";

        Uri fetchUri = Uri.parse(url);
        return new CursorLoader(getActivity(), fetchUri, PLACE_COLUMNS, null, null, null);
    }

    // onLoadFinished is called when the Data is Ready...
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
//        Log.v(TAG, "onLoadFinished----------Called Successfully-----------------");
        if (placeCursorAdapter == null)
            placeCursorAdapter = new PlaceCursorAdapter(getContext(), c, PLACE_LOADER);
        placeCursorAdapter.swapCursor(c);
//        Log.v(TAG, getString(R.string.fav_verbose) + String.valueOf(favMenuSelected));

        if (favMenuSelected)
            lv.setAdapter(placeCursorAdapter);


        getActivity().sendBroadcast(new Intent("com.widget.FORCE_WIDGET_UPDATE"));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        Log.v(TAG, "onLoaderReset----------------Called Successfully-----------------");

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
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        initMenuArray();

        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuArr)); // set the adapter to provide layout of rows and content

        spinner.setSelection(position,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);

                position=spinner.getSelectedItemPosition();
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                    editorPref = pref.edit();
//                    editorPref.putInt("position",position);
                    editorPref.putString("category", selectedText.getText().toString());
                    editorPref.commit();
                    categoryStr = pref.getString("category", "atm");
                    favMenuSelected = false;
                    fetchDataOnMenuChange();
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
            chooseRange();
            return true;
        }

        if (id == R.id.action_favorite) {
            favMenuSelected = true;
            getLoaderManager().restartLoader(PLACE_LOADER, null, this);
            return true;
        }
        if (id == R.id.action_other) {
            favMenuSelected = false;
            fetchDataOnMenuChange();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("settings", MODE_WORLD_WRITEABLE);

//        Log.v(TAG,"onCreateView called");
        lv = (ListView) view.findViewById(R.id.ma_list);
        empty = view.findViewById(R.id.empty);
        lv.setEmptyView(empty);

        pg = (ProgressBar) view.findViewById(R.id.ma_pg);

        rangeStr = pref.getString("range", "2000");
            categoryStr = pref.getString("category", "atm");
//        Log.v(TAG,categoryStr);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        fetchDataOnMenuChange();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (favMenuSelected == false) {
//                    Log.v(TAG, "online Movie Clicked");
                    mCallbacks.onItemSelected(mobj[position]);

                } else {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    MyObject mo = new MyObject();
//                    Log.v(TAG, "offline Movie Clicked.");

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
                    String result = c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ID))
                            +   c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PHONE))
                            + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_NAME))
                            + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_VICINITY))
                            + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_OPEN))
                            + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PHOTOREF))
                            + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_ADDRESS))
                            + c.getString(c.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACEID)).toString();

//                    Log.v(TAG, result);
                }
            } while (c.moveToNext());
        }
// Uptil Here...

        return view;
    }





    public class SearchTask extends AsyncTask<String, Void, MyObject[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Log.v(TAG, "onPreExecute Called");
            pg.setVisibility(View.VISIBLE);
        }

        @Override
        protected MyObject[] doInBackground(String... arg0) {
//            Log.v(TAG, "doInBackground Called");

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
                mobj = new MyObject[arr1.length()];
                for (int i = 0; i < arr1.length(); i++) {
                    mobj[i] = new MyObject();
                    JSONObject obj2 = arr1.getJSONObject(i);
                    mobj[i].setName(obj2.getString("name"));
                    mobj[i].setVicinity(obj2.getString("vicinity"));
                    mobj[i].setIcon(obj2.getString("icon"));
                    mobj[i].setId(obj2.getString("place_id"));

                    mArrList.add(mobj[i]);
                }

                return mobj;
            } catch (JSONException e) {
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
//            Log.v(TAG, "onPostExecute Called");

            pg.setVisibility(View.INVISIBLE);
            if (result == null) {
//                Toast.makeText(getActivity(), getString(R.string.onpost_result_error), Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<String> nameArr = new ArrayList<String>();
                ArrayList<String> iconArr = new ArrayList<String>();
                ArrayList<String> vicArr = new ArrayList<String>();

                for (MyObject m : result) {
 //                   Log.v(TAG, m.getId());
                }
                for (MyObject m : result) {
                    nameArr.add(m.getName());
                    iconArr.add(m.getIcon());
                    vicArr.add(m.getVicinity());
                }
                if (result != null) {
                    if (!favMenuSelected) {
                        mAdapter = new MyListAdapter(getActivity(), nameArr, iconArr, vicArr);
                        lv.setAdapter(mAdapter);
                    }
                    mAdapter.notifyDataSetChanged();

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
        et.setText(pref.getString("range", "2000"));
        dialogBuilder.setTitle(getString(R.string.alert_title));
        dialogBuilder.setMessage(getString(R.string.alert_message));
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String range = et.getText().toString();

                editorPref = pref.edit();
                editorPref.putString("range", range);
                editorPref.commit();
                rangeStr = pref.getString("range", "2000");
                fetchDataOnMenuChange();

                Toast.makeText(getActivity(), getString(R.string.range_success), Toast.LENGTH_SHORT).show();
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
//        Log.v(TAG,"onResume called");
        super.onResume();
        mGoogleApiClient.connect();
        mIsInForegroundMode = true;
        fetchDataOnMenuChange();

        setRetainInstance(true);


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
//        Log.v(TAG, "setActivatedOnItemClick Executed");

        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        lv.setChoiceMode(activateOnItemClick
                ? GridView.CHOICE_MODE_SINGLE
                : GridView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {

//        Log.v(TAG, "setActivatedPosition Executed");
        if (position == GridView.INVALID_POSITION) {
            lv.setItemChecked(mActivatedPosition, false);
        } else {
            lv.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    public void fetchDataOnMenuChange() {
        String ltdlng=ltd+","+lng;
        Uri buildUri=Uri.parse(BASE_URL).buildUpon().appendQueryParameter(LOCATION_KEY,ltdlng)
                .appendQueryParameter(RADIUS_KEY,rangeStr).appendQueryParameter(TYPES_KEY,categoryStr)
                .appendQueryParameter(NAME_KEY,NAME_VALUE).appendQueryParameter(SENSOR_KEY,SENSOR_VALUE)
                .appendQueryParameter(APIKEY_KEY,BuildConfig.MyGoogleMapKey).build();
        String fetchUrl=buildUri.toString();
        new SearchTask().execute(fetchUrl);


    }


    @Override
    public void onStart() {
        super.onStart();
        // new code
        mGoogleApiClient.connect();
        // here
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

    }
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.i(TAG,location.toString());
        ltd=location.getLatitude();
        lng=location.getLongitude();
    }



    @Override
    public void onConnectionSuspended(int i) {
//        Log.i(TAG,"connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.i(TAG,"connection Failed");

    }
    @Override
    public void onPause() {
        super.onPause();

    }

}
