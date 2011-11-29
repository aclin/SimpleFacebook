package com.alin.simplefacebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alin.simplefacebook.AsyncFacebookRunner.RequestListener;
import com.alin.simplefacebook.Facebook.DialogListener;

public class SimpleFacebook extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
	
	private static final String TAG = "SimpleFacebook";
	
	public static final String APP_ID = "175729095772478";
	public static final String KEY_ID = "FriendID";
	public static final String KEY_NAME = "FriendName";
	
	private static final int PROFILE_REQUEST = 0;
	
	public static Facebook fb = new Facebook(APP_ID);
	private AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(fb);
	
	private JSONArray friendlist;
	
	Button btLogin;
	TextView tvHello;
	ListView lvFriends;
	ArrayAdapter<String> fbArrayAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViews();
        setListeners();
        //fbArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        fbArrayAdapter = new ArrayAdapter<String>(this, R.layout.friendlist);
        if (fb.isSessionValid()) {
			tvHello.setText("Friends List");
        	btLogin.setText("Logout");
        	fbAsyncRunner.request("me/friends", friendsRequestListener);
			lvFriends.setAdapter(fbArrayAdapter);
        } else {
        	btLogin.setText("Login");
        }
    }
    
    private void findViews() {
    	btLogin = (Button) findViewById(R.id.btLogin);
    	tvHello = (TextView) findViewById(R.id.tvHello);
    	lvFriends = (ListView) findViewById(R.id.lvFriends);
    }
    
    private void setListeners() {
    	btLogin.setOnClickListener(this);
    	lvFriends.setOnItemClickListener(this);
    }
    
    public void onClick(View v) {
    	switch(v.getId()) {
    	case R.id.btLogin:
    		if (!fb.isSessionValid()) {
	    		fb.authorize(SimpleFacebook.this, new String[] {"read_friendlists", "publish_stream", "read_stream"},
						new DialogListener() {
							
							public void onComplete(Bundle values) {
								fbAsyncRunner.request("me/friends", friendsRequestListener);
								lvFriends.setAdapter(fbArrayAdapter);
								btLogin.setText("Logout");
								tvHello.setText("Friends List");
							}
							
							
							public void onFacebookError(FacebookError error) {}
							
							
							public void onError(DialogError e) {}
							
							
							public void onCancel() {}
						}
				);
	    		
    		} else {
    			fbAsyncRunner.logout(SimpleFacebook.this, logoutListener);
    		}
    		break;
    	}
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	//Log.d(TAG, "Position: " + position + " Id: " + id);
    	Intent i_profile = new Intent().setClass(SimpleFacebook.this, Profile.class);
    	Bundle b = new Bundle();
    	try {
    		b.putString(KEY_NAME, friendlist.getJSONObject(position).getString("name"));
    		b.putString(KEY_ID, friendlist.getJSONObject(position).getString("id"));
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	i_profile.putExtras(b);
    	
    	startActivityForResult(i_profile, PROFILE_REQUEST);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PROFILE_REQUEST) {
        	tvHello.setText("Message sent to " + data.getStringExtra("name"));
        	//handler.postDelayed(updateUITimer, 2000);
        }
        fb.authorizeCallback(requestCode, resultCode, data);
    }
    
    private final Runnable updateUITimer = new Runnable() {
    	public void run() {
    		tvHello.setText("Friends List");
    	}
    };
    
    private final Handler handler = new Handler();
    
    private RequestListener friendsRequestListener = new RequestListener() {

		
		public void onComplete(final String response, final Object state) {
			SimpleFacebook.this.runOnUiThread(new Runnable() {
				public void run() {
					try {
						JSONObject friend;
						friend = new JSONObject(response);
						friendlist = friend.getJSONArray("data");
						
						if (friendlist != null) {
							for (int i=0; i < friendlist.length(); i++) {
								//Log.d("DebugLog", "Friend #" + i + " >>" + friendlist.getJSONObject(i).getString("name"));
								fbArrayAdapter.add(friendlist.getJSONObject(i).getString("name"));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					fbArrayAdapter.notifyDataSetChanged();
				}
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
    
    private RequestListener logoutListener = new RequestListener() {

		
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}

		
		public void onComplete(String response, Object state) {
			SimpleFacebook.this.runOnUiThread(new Runnable() {
				
				public void run() {
					fbArrayAdapter.clear();
					fbArrayAdapter.notifyDataSetChanged();
					Toast.makeText(SimpleFacebook.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
	    			btLogin.setText("Login");
	    			tvHello.setText("Hello World, SimpleFacebook!");
				}
			});
		}
    };
}