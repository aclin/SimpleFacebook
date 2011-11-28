package com.alin.simplefacebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alin.simplefacebook.AsyncFacebookRunner.RequestListener;

public class Profile extends Activity {
	
	private static final String TAG = "Class_Profile";
	
	private Facebook fb = SimpleFacebook.fb;
	private AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(fb);
	
	String profileName;
	String profileId;
	
	TextView tvFrdName;
	EditText etWallMsg;
	Button btPostMsg;
	
	ArrayAdapter<String> fbArrayAdapter;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        
        findViews();
        
        Bundle b = this.getIntent().getExtras();
        profileName = b.getString(SimpleFacebook.KEY_NAME);
        profileId = b.getString(SimpleFacebook.KEY_ID);
        
        tvFrdName.setText(profileName);
        //Log.d(TAG, "Name: " + profileName);
        
        displayWallMsgs();
    }
    
    private void findViews() {
    	tvFrdName = (TextView) findViewById(R.id.tvFrdName);
    	etWallMsg = (EditText) findViewById(R.id.etWallMsg);
    }
    
    private void displayWallMsgs() {
    	fbAsyncRunner.request("me/feeds", feedsListener);
    }
    
    private RequestListener feedsListener = new RequestListener() {

		public void onComplete(final String response, final Object state) {
			Profile.this.runOnUiThread(new Runnable() {
				public void run() {
					JSONObject wall;
					JSONArray feeds;
					try {
						wall = new JSONObject(response);
						feeds = new JSONArray("data");
						
						if (feeds != null) {
							for (int i=0; i<feeds.length(); i++) {
								if (feeds.getJSONObject(i).getString("type").equals("status")) {
									// Check if the feed type is "status"
									if (feeds.getJSONObject(i).getJSONArray("from").getJSONObject(0).getString("id").equals(profileId)) {
										fbArrayAdapter.add(feeds.getJSONObject(i).getString("message"));
									}
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				};
			});
		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
    	
    };
}
