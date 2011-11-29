package com.alin.simplefacebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alin.simplefacebook.AsyncFacebookRunner.RequestListener;

public class Profile extends Activity implements View.OnClickListener {
	
	private static final String TAG = "Class_Profile";
	
	private Facebook fb = SimpleFacebook.fb;
	private AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(fb);
	
	private String profileName;
	private String profileId;
	
	TextView tvFrdName;
	EditText etWallMsg;
	Button btPostMsg;
	ListView lvMsgs;
	ImageView ivProfileImg;
	
	ArrayList<HashMap<String, Object>> fbListItem;
	SimpleAdapter listItemAdapter;
	ArrayAdapter<String> fbArrayAdapter;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        
        findViews();
        setListeners();
        
        //fbArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        fbListItem = new ArrayList<HashMap<String, Object>>();
        listItemAdapter = new SimpleAdapter(this, fbListItem, R.layout.profile_listview, new String[] { "name", "msg" }, new int[] { R.id.tvProfileName, R.id.tvProfileMsg });
        
        Bundle b = this.getIntent().getExtras();
        profileName = b.getString(SimpleFacebook.KEY_NAME);
        profileId = b.getString(SimpleFacebook.KEY_ID);
        
        tvFrdName.setText(profileName);
        
        displayProfilePicture();
        displayWallMsgs();
    }
    
    private void findViews() {
    	tvFrdName = (TextView) findViewById(R.id.tvFrdName);
    	etWallMsg = (EditText) findViewById(R.id.etWallMsg);
    	lvMsgs = (ListView) findViewById(R.id.lvMsgs);
    	ivProfileImg = (ImageView) findViewById(R.id.ivProfileImg);
    	btPostMsg = (Button) findViewById(R.id.btPostMsg);
    }
    
    private void setListeners() {
    	btPostMsg.setOnClickListener(this);
    }
    
    public void onClick(View v) {
    	if (v.getId() == R.id.btPostMsg) {
	    	Bundle b = new Bundle();
	    	b.putString("method", "POST");
	    	b.putString("message", etWallMsg.getText().toString());
	    	fbAsyncRunner.request(profileId + "/feed", b, postListener);
    	}
    }
    
    private void displayProfilePicture() {
    	URL img_value = null;
        try {
			img_value = new URL("http://graph.facebook.com/" + profileId + "/picture");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        
        Bitmap mIcon1;
        
		try {
			mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
			ivProfileImg.setImageBitmap(mIcon1);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void displayWallMsgs() {
    	fbAsyncRunner.request(profileId + "/feed", feedsListener);
    	//lvMsgs.setAdapter(fbArrayAdapter);
    	lvMsgs.setAdapter(listItemAdapter);
    }
    
    private RequestListener feedsListener = new RequestListener() {

		public void onComplete(final String response, final Object state) {
			Profile.this.runOnUiThread(new Runnable() {
				public void run() {
					JSONObject wall;
					JSONArray feeds;
					try {
						wall = new JSONObject(response);
						feeds = wall.getJSONArray("data");
						
						if (feeds != null) {
							for (int i=0; i<feeds.length(); i++) {
								if (feeds.getJSONObject(i).getString("type").equals("status")) {
									// Check if the feed type is "status"
									if (feeds.getJSONObject(i).getJSONObject("from").getString("id").equals(profileId)) {
										HashMap<String, Object> map = new HashMap<String, Object>();
										map.put("name", profileName + " says:");
										map.put("msg", feeds.getJSONObject(i).getString("message"));
										fbListItem.add(map);
										//fbArrayAdapter.add(feeds.getJSONObject(i).getString("message"));
										//Log.i(TAG, "Array Adapter count: " + fbArrayAdapter.getCount());
									}
									//if (fbArrayAdapter.getCount() == 3)
										//break;
									if (fbListItem.size() == 3)
										break;
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					//fbArrayAdapter.notifyDataSetChanged();
					listItemAdapter.notifyDataSetChanged();
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
    
    private RequestListener postListener = new RequestListener() {

		@Override
		public void onComplete(String response, Object state) {
			Intent i_return = new Intent();
			i_return.putExtra("name", profileName);
			setResult(RESULT_OK, i_return);
			finish();
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
    	
    };
}
