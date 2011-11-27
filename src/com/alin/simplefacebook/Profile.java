package com.alin.simplefacebook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Profile extends Activity {
	
	private static final String TAG = "Class_Profile";
	
	private Facebook fb = new Facebook(SimpleFacebook.APP_ID);
	private AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(fb);
	
	String profileName;
	String profileId;
	
	TextView tvFrdName;
	EditText etWallMsg;
	Button btPostMsg;
	
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
        Log.d(TAG, "Name: " + profileName);
    }
    
    private void findViews() {
    	tvFrdName = (TextView) findViewById(R.id.tvFrdName);
    	etWallMsg = (EditText) findViewById(R.id.etWallMsg);
    }
}
