package com.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	/*
	 * make a splash, look at res -> drawable -> background_splash.xml, 
	 *                        res -> values -> styles.xml -> SplashTheme
	 *                        AndroidManifest.xml -> "@style/SplashTheme"
	 */
	
	private Handler handler;
	private Runnable runnable;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		{
		    handler = new Handler();
		    runnable = new Runnable() {
		        @Override
		        public void run() {
		            try {
		            	handler.removeCallbacks(runnable);
		            }
		            finally {
		                finish();
		                goToMainActivity();
		                handler.removeCallbacks(runnable);
		            }
		        }
		    }; 
		    handler.postDelayed(runnable, 2000);
		}
		
    }
	 
	public void goToMainActivity() {
		Intent main = new Intent();
	    main.setClass(getApplicationContext(), MainActivity.class);
	    startActivity(main);
	}
	 
}
