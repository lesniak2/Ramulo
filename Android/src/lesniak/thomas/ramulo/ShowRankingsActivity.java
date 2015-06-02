package lesniak.thomas.ramulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lesniak.thomas.ramulo.AllLocationsActivity.LoadAllLocations;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowRankingsActivity extends ListActivity implements
		OnItemClickListener {
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	// url to get_all_locations
	private static String url_rankings = "http://10.0.2.2:5000/rankings";

	// JSON Node names
	private static final String TAG_LOCATION_ID = "location_id";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_RANKINGS = "rankings";
	private static final String TAG_SONG_ID = "song_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_ARTIST = "artist";
	private static final String TAG_RANK = "rank";
	private static final String TAG_ALBUM = "album";
	private static String LOCATION_ID = "";
	ArrayList<HashMap<String, String>> rankingsList;

	// locations JSONArray
	JSONArray rankings = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_locations);

		Intent intent = getIntent();
		// get the location id of the item clicked in the list
		LOCATION_ID = intent.getStringExtra(TAG_LOCATION_ID);
		// System.out.println(LOCATION_ID);

		// load the rankings for the corresponding location id

		// Hashmap for ListView
		rankingsList = new ArrayList<HashMap<String, String>>();

		// Loading locations in Background Thread
		new LoadRankings().execute();

		// Get listview
		ListView lv = getListView();

		// on selecting single location
		// launching rankings screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

	}

	/**
	 * Background Async Task to load rankings via HTTP Requests
	 * */
	class LoadRankings extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ShowRankingsActivity.this);
			pDialog.setMessage("Loading locations. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting all locations from url
		 * */
		protected String doInBackground(String... args) {
			// Attach the location id tag
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("id", LOCATION_ID));

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_rankings, "GET",
					params);

			// Check log cat for JSON reponse
			Log.d("Rankings: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// rankings found
					// put them in a json array
					rankings = json.getJSONArray(TAG_RANKINGS);

					// looping through locations
					for (int i = 0; i < rankings.length(); i++) {
						JSONObject c = rankings.getJSONObject(i);

						// Storing each item in variable
						String id = c.getString(TAG_SONG_ID);
						String artist = c.getString(TAG_ARTIST);
						String title = c.getString(TAG_TITLE);
						String rank = c.getString(TAG_RANK);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_LOCATION_ID, LOCATION_ID);
						map.put(TAG_SONG_ID, id);
						map.put("song", artist + " - " + title + " = "+ rank);

						// adding HashList to ArrayList
						rankingsList.add(map);
					}
				} else {
					// no products found
					// TODO: Prompt user to add a song for ranking
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all locations
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					MyRankAdapter adapter = new MyRankAdapter(
							ShowRankingsActivity.this, rankingsList,
							R.layout.list_item_rank, new String[] { TAG_LOCATION_ID, TAG_SONG_ID,
									"song" }, new int[] { R.id.location_id, R.id.song_id,
									R.id.name });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}

	public class MyRankAdapter extends SimpleAdapter {

		private Context c;
		private List< ? extends Map<String, ?>> list;
		public MyRankAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			c = context;
			list = data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			return row;
		}
		// other stuff
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		new AlertDialog.Builder(this)
	    .setTitle("Increase/Decrease Rank")
	    .setPositiveButton("+", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // increase the rank
	        }
	     })
	    .setNegativeButton("-", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // decrease the rank
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .create()
	     .show();

	}

}
