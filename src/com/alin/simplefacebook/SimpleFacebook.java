package com.alin.simplefacebook;

import android.app.Activity;
import android.os.Bundle;

public class SimpleFacebook extends Activity {
	
	private static final String TAG = "SimpleFacebook";
	private static final String APP_ID = "";
	
	private Facebook fb;
	private AsyncFacebookRunner asyncRunner;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}