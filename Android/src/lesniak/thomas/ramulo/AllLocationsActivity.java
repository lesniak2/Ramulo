package lesniak.thomas.ramulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.os.Build;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
 

import android.os.AsyncTask;

public class AllLocationsActivity extends ListActivity implements OnItemClickListener {

	 // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> locationsList;
 
    // url to get_all_locations
    private static String url_all_locations = "http://10.0.2.2:5000/locations";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LOCATIONS = "locations";
    private static final String TAG_LOCATION_ID = "location_id";
    private static final String TAG_NAME = "name";
 
    // locations JSONArray
    JSONArray locations = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_locations);
 
        // Hashmap for ListView
        locationsList = new ArrayList<HashMap<String, String>>();
        
        // Loading locations in Background Thread
        new LoadAllLocations().execute();
 
        // Get listview
        ListView lv = getListView();
 
        // on selecting single location
        // launching rankings screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String location_id = ((TextView) view.findViewById(R.id.location_id)).getText()
                		.toString();
 
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        ShowRankingsActivity.class);
                // sending location_id to next activity
                in.putExtra(TAG_LOCATION_ID, location_id);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });
 
    }
 
    // Response from ShowRankingsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if result code 100 is received,
        // it means the user changed/added a ranking for a song
        if (resultCode == 100) {
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
 
    /**
     * Background Async Task to load all locations by making HTTP Request
     * */
    class LoadAllLocations extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllLocationsActivity.this);
            pDialog.setMessage("Loading locations. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting all locations from url
         * */
        protected String doInBackground(String... args) {
            // Attach the latitude longitude params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude, latitude;
            if(location == null) {
            	// use Siebel Center for testing
            	latitude = 40.1138;
            	longitude = -88.2249;
            }
            else {
            	longitude = location.getLongitude();
            	latitude = location.getLatitude();
                 
            }
            params.add(new BasicNameValuePair("latitude", "" + latitude));
            params.add(new BasicNameValuePair("longitude", "" + longitude));
            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_locations, "GET", params);
 
            // Check log cat for JSON reponse
            Log.d("All Locations: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
            	int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // locations found
                    // put them in a json array
                    locations = json.getJSONArray(TAG_LOCATIONS);
 
                    // looping through locations
                    for (int i = 0; i < locations.length(); i++) {
                        JSONObject c = locations.getJSONObject(i);
 
                        // Storing each item in variable
                        String id = c.getString(TAG_LOCATION_ID);
                        String name = c.getString(TAG_NAME);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_LOCATION_ID, id);
                        map.put(TAG_NAME, name);
 
                        // adding HashList to ArrayList
                        locationsList.add(map);
                    }
                } else {
                    // no locations found
                	//TODO: Display no locations
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all locations
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     */
                    ListAdapter adapter = new SimpleAdapter(
                            AllLocationsActivity.this, locationsList,
                            R.layout.list_item, new String[] { TAG_LOCATION_ID,
                                    TAG_NAME},
                            new int[] { R.id.location_id, R.id.name });
                    // updating listview
                    setListAdapter(adapter);		
                }
            });
 
        }
 
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		            // Then you start a new Activity via Intent
		            Intent intent = new Intent();
		            intent.setClass(this, ShowRankingsActivity.class);
		            int selectedID = Integer.parseInt(locationsList.get(position).get(TAG_LOCATION_ID));
		            intent.putExtra("lid", selectedID);
		            
		            Context context = getApplicationContext();
		            CharSequence text = "Location id:" + locationsList.get(position).get(TAG_LOCATION_ID);
		            int duration = Toast.LENGTH_SHORT;

		            Toast toast = Toast.makeText(context, text, duration);
		            toast.show();
		            
		            startActivity(intent);
		
	}
}