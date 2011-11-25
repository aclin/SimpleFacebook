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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.alin.simplefacebook.AsyncFacebookRunner.RequestListener;
import com.alin.simplefacebook.Facebook.DialogListener;

public class SimpleFacebook extends Activity implements View.OnClickListener {
	
	private static final String TAG = "SimpleFacebook";
	private static final String APP_ID = "175729095772478";
	
	private Facebook fb = new Facebook(APP_ID);
	private AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(fb);
	
	Button btLogin;
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
        if (fb.isSessionValid())
        	btLogin.setText("Logout");
        else
        	btLogin.setText("Login");
    }
    
    private void findViews() {
    	btLogin = (Button) findViewById(R.id.btLogin);
    	lvFriends = (ListView) findViewById(R.id.lvFriends);
    }
    
    private void setListeners() {
    	btLogin.setOnClickListener(this);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fb.authorizeCallback(requestCode, resultCode, data);
    }
    
    public void onClick(View v) {
    	switch(v.getId()) {
    	case R.id.btLogin:
    		if (!fb.isSessionValid()) {
	    		fb.authorize(SimpleFacebook.this, new String[] {"read_friendlists",},
						new DialogListener() {
							@Override
							public void onComplete(Bundle values) {
								fbAsyncRunner.request("me/friends", friendsRequestListener);
								lvFriends.setAdapter(fbArrayAdapter);
							}
							
							@Override
							public void onFacebookError(FacebookError error) {}
							
							@Override
							public void onError(DialogError e) {}
							
							@Override
							public void onCancel() {}
						}
				);
	    		btLogin.setText("Logout");
    		} else {
    			fbAsyncRunner.logout(SimpleFacebook.this, logoutListener);
    			btLogin.setText("Login");
    		}
    		break;
    	}
    }
    
    private RequestListener friendsRequestListener = new RequestListener() {

		@Override
		public void onComplete(final String response, final Object state) {
			SimpleFacebook.this.runOnUiThread(new Runnable() {
				public void run() {
					JSONObject friend;
					JSONArray friendlist;
					try {
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
    
    private RequestListener logoutListener = new RequestListener() {

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
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
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onComplete(String response, Object state) {
			SimpleFacebook.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(SimpleFacebook.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
				}
			});
		}
    };
}