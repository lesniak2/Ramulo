package lesniak.thomas.ramulo;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		Button btn = (Button)findViewById(R.id.btnViewLocations);
	    btn.setOnClickListener(new OnClickListener() 
	    {  
	    	@Override
	    	public void onClick(View v) 
	        {   
	            Intent intent = new Intent(MainScreenActivity.this, AllLocationsActivity.class);
	                startActivity(intent);      
	                finish();
	        }
	    	
	    });
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		Intent myIntent = new Intent(MainScreenActivity.this, AllLocationsActivity.class);
		MainScreenActivity.this.startActivity(myIntent);
		return super.onOptionsItemSelected(item);
	}
}
